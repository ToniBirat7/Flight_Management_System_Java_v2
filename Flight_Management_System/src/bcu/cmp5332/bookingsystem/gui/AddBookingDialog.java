package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.AddBooking;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.gui.utils.BaseDialog;
import bcu.cmp5332.bookingsystem.gui.utils.Theme;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class AddBookingDialog extends BaseDialog {

    private final MainWindow parent;
    private final JComboBox<CustomerItem> customerCombo = new JComboBox<>();
    private final JComboBox<FlightItem> flightCombo = new JComboBox<>();
    private final JLabel priceLabel = new JLabel();

    public AddBookingDialog(MainWindow parent) {
        super(parent, "Add New Booking");
        this.parent = parent;

        initializeComponents();
        loadCustomers();
        loadFlights();

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private void initializeComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Customer selection
        addFormField("Customer:", customerCombo, gbc, 0);

        // Flight selection
        addFormField("Flight:", flightCombo, gbc, 1);

        // Price Panel with vertical layout
        JPanel pricePanel = new JPanel(new BorderLayout());
        pricePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Price Details"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        pricePanel.setBackground(Theme.BACKGROUND);

        priceLabel.setFont(Theme.REGULAR_FONT);
        priceLabel.setForeground(Theme.TEXT_PRIMARY);
        pricePanel.add(priceLabel, BorderLayout.CENTER);

        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        contentPanel.add(pricePanel, gbc);

        // Add flight selection listener
        flightCombo.addActionListener(e -> updatePriceDisplay());

        // Buttons
        JButton addButton = createButton("Add Booking", Theme.Button.SUCCESS_BG);
        JButton cancelButton = createButton("Cancel", Theme.Button.SECONDARY_BG);

        addButton.addActionListener(e -> addBooking());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
    }

    private void loadCustomers() {
        List<Customer> customers = parent.getFlightBookingSystem().getCustomers();
        for (Customer customer : customers) {
            customerCombo.addItem(new CustomerItem(customer));
        }
    }

    private void loadFlights() {
        List<Flight> flights = parent.getFlightBookingSystem().getFlights();
        for (Flight flight : flights) {
            if (flight.hasAvailableSeats() &&
                    !flight.getDepartureDate().isBefore(parent.getFlightBookingSystem().getSystemDate())) {
                flightCombo.addItem(new FlightItem(flight, parent.getFlightBookingSystem().getSystemDate()));
            }
        }
    }

    private void updatePriceDisplay() {
        FlightItem selectedFlight = (FlightItem) flightCombo.getSelectedItem();
        if (selectedFlight != null) {
            StringBuilder priceText = new StringBuilder("<html><div style='width: 300px;'>");

            // Base Price
            priceText.append("<div style='margin-bottom: 10px;'>");
            priceText.append(String.format("<b>Base Price:</b> £%.2f", selectedFlight.basePrice));
            priceText.append("</div>");

            // Price Factors Section
            priceText.append("<div style='margin-bottom: 10px;'>");
            priceText.append("<b>Price Factors:</b>");

            // Calculate and show multipliers
            double totalMultiplier = 1.0;
            boolean hasFactors = false;

            if (selectedFlight.occupancyRate >= 0.8) {
                priceText.append("<div style='margin-left: 15px;'>• High Demand (+30%)</div>");
                totalMultiplier += 0.3;
                hasFactors = true;
            } else if (selectedFlight.occupancyRate >= 0.5) {
                priceText.append("<div style='margin-left: 15px;'>• Medium Demand (+15%)</div>");
                totalMultiplier += 0.15;
                hasFactors = true;
            }

            if (selectedFlight.daysUntil <= 7) {
                priceText.append("<div style='margin-left: 15px;'>• Last Minute Booking (+50%)</div>");
                totalMultiplier += 0.5;
                hasFactors = true;
            } else if (selectedFlight.daysUntil <= 30) {
                priceText.append("<div style='margin-left: 15px;'>• Near Departure (+25%)</div>");
                totalMultiplier += 0.25;
                hasFactors = true;
            }

            if (!hasFactors) {
                priceText.append("<div style='margin-left: 15px;'>• No additional factors</div>");
            }

            priceText.append("</div>");

            // Show calculation
            double calculatedPrice = Math.round(selectedFlight.basePrice * totalMultiplier * 100.0) / 100.0;
            priceText.append("<div style='margin-top: 10px; padding-top: 10px; border-top: 1px solid #ccc;'>");
            priceText.append(String.format("<b>Final Price:</b> £%.2f", calculatedPrice));
            if (calculatedPrice != selectedFlight.basePrice) {
                priceText.append(String.format(" (%.0f%% increase)", (totalMultiplier - 1.0) * 100));
            }
            priceText.append("</div>");

            priceText.append("</div></html>");
            priceLabel.setText(priceText.toString());
        } else {
            priceLabel.setText(
                    "<html><div style='width: 300px; padding: 10px;'>Select a flight to see price details</div></html>");
        }
    }

    private void addBooking() {
        try {
            CustomerItem selectedCustomer = (CustomerItem) customerCombo.getSelectedItem();
            FlightItem selectedFlight = (FlightItem) flightCombo.getSelectedItem();

            if (selectedCustomer == null || selectedFlight == null) {
                throw new FlightBookingSystemException("Please select both customer and flight");
            }

            // Check flight capacity
            if (!selectedFlight.flight.hasAvailableSeats()) {
                throw new FlightBookingSystemException("Selected flight is at full capacity");
            }

            // Execute the command
            new AddBooking(selectedCustomer.customer.getId(), selectedFlight.flight.getId())
                    .execute(parent.getFlightBookingSystem());

            // Save changes
            parent.saveData();

            // Update view and close dialog
            parent.showBookings();
            dispose();
            showSuccess("Booking added successfully");

        } catch (FlightBookingSystemException ex) {
            showError(ex.getMessage());
        }
    }

    // Helper classes for combo boxes
    private static class CustomerItem {
        final Customer customer;

        CustomerItem(Customer customer) {
            this.customer = customer;
        }

        @Override
        public String toString() {
            return String.format("#%d - %s", customer.getId(), customer.getName());
        }
    }

    private static class FlightItem {
        final Flight flight;
        final double currentPrice;
        final double basePrice;
        final double occupancyRate;
        final long daysUntil;

        FlightItem(Flight flight, LocalDate systemDate) {
            this.flight = flight;
            this.basePrice = flight.getPrice();
            this.currentPrice = flight.calculateCurrentPrice(systemDate);
            this.occupancyRate = 1.0 - ((double) flight.getAvailableSeats() / flight.getCapacity());
            this.daysUntil = ChronoUnit.DAYS.between(systemDate, flight.getDepartureDate());
        }

        @Override
        public String toString() {
            return String.format("%s - %s to %s - %s (in %d days) - £%.2f (Base: £%.2f)",
                flight.getFlightNumber(),
                flight.getOrigin(),
                flight.getDestination(),
                flight.getDepartureDate(),
                daysUntil,
                currentPrice,
                basePrice);
        }
    }
}