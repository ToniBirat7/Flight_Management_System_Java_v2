package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.EditBooking;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.gui.utils.BaseDialog;
import bcu.cmp5332.bookingsystem.gui.utils.Theme;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.time.temporal.ChronoUnit;

public class EditBookingDialog extends BaseDialog {
    
    private static final double CANCELLATION_FEE = 50.0;
    private static final double REBOOKING_FEE = 30.0;
    
    private final MainWindow parent;
    private final Booking bookingToEdit;
    private final JComboBox<FlightItem> flightCombo = createStyledComboBox();
    private final JLabel currentDetailsLabel = new JLabel();
    private final JLabel newFlightDetailsLabel = new JLabel();
    private final JLabel errorLabel = new JLabel(" ");
    private final JLabel priceLabel = new JLabel();
    
    public EditBookingDialog(MainWindow parent, int bookingId) {
        super(parent, "Edit Booking");
        this.parent = parent;
        
        // Find the booking to edit
        this.bookingToEdit = parent.findBookingById(bookingId);
        if (bookingToEdit == null) {
            showError("Booking not found");
            dispose();
            return;
        }
        
        initializeComponents();
        loadAvailableFlights();
        updateLabels();
        
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }
    
    private void initializeComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Current booking details section
        JPanel currentDetailsPanel = new JPanel();
        currentDetailsPanel.setLayout(new BoxLayout(currentDetailsPanel, BoxLayout.Y_AXIS));
        currentDetailsPanel.setBackground(Theme.BACKGROUND);
        currentDetailsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Theme.SECONDARY),
            "Current Booking Details"
        ));
        
        currentDetailsLabel.setFont(Theme.REGULAR_FONT);
        currentDetailsPanel.add(currentDetailsLabel);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        contentPanel.add(currentDetailsPanel, gbc);
        
        // New flight selection
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        addFormFieldWithHint("Select New Flight:", flightCombo, 
            "Choose a new flight for this booking", gbc, 1);
        
        // New flight details section
        JPanel newFlightPanel = new JPanel();
        newFlightPanel.setLayout(new BoxLayout(newFlightPanel, BoxLayout.Y_AXIS));
        newFlightPanel.setBackground(Theme.BACKGROUND);
        newFlightPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Theme.SECONDARY),
            "New Flight Details"
        ));
        
        newFlightDetailsLabel.setFont(Theme.REGULAR_FONT);
        newFlightPanel.add(newFlightDetailsLabel);
        
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        contentPanel.add(newFlightPanel, gbc);
        
        // Price display
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        priceLabel.setFont(Theme.REGULAR_FONT);
        contentPanel.add(priceLabel, gbc);
        
        // Error label
        errorLabel.setForeground(Theme.WARNING);
        errorLabel.setFont(Theme.REGULAR_FONT);
        gbc.gridy = 4;
        contentPanel.add(errorLabel, gbc);
        
        // Buttons
        JButton updateButton = createButton("Update Booking", Theme.Button.SUCCESS_BG);
        JButton cancelButton = createButton("Cancel", Theme.Button.SECONDARY_BG);
        
        updateButton.addActionListener(e -> updateBooking());
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(updateButton);
        buttonPanel.add(cancelButton);
        
        // Add flight selection listener
        flightCombo.addActionListener(e -> {
            FlightItem selectedFlight = (FlightItem) flightCombo.getSelectedItem();
            if (selectedFlight != null) {
                updateNewFlightDetails(selectedFlight.flight);
            }
        });
    }
    
    private <T> JComboBox<T> createStyledComboBox() {
        JComboBox<T> comboBox = new JComboBox<>();
        comboBox.setFont(Theme.REGULAR_FONT);
        comboBox.setPreferredSize(new Dimension(300, 35));
        comboBox.setBorder(new CompoundBorder(
            new LineBorder(Theme.SECONDARY, 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
        return comboBox;
    }
    
    private void addFormFieldWithHint(String label, JComponent field, String hint, 
            GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        JLabel labelComponent = createLabel(label);
        labelComponent.setFont(Theme.REGULAR_FONT);
        contentPanel.add(labelComponent, gbc);
        
        gbc.gridx = 1;
        contentPanel.add(field, gbc);
        
        field.setToolTipText(hint);
    }
    
    private void loadAvailableFlights() {
        List<Flight> flights = parent.getFlightBookingSystem().getFlights();
        flightCombo.removeAllItems();
        
        LocalDate systemDate = parent.getFlightBookingSystem().getSystemDate();
        
        for (Flight flight : flights) {
            // Don't show current flight, past flights, or flights without seats
            if (flight.getId() != bookingToEdit.getFlight().getId() && 
                flight.hasAvailableSeats() && 
                !flight.hasDeparted(systemDate)) {
                flightCombo.addItem(new FlightItem(flight, systemDate));
            }
        }
    }
    
    private void updateLabels() {
        // Update current booking details
        currentDetailsLabel.setText(String.format(
            "<html><b>Customer:</b> %s<br>" +
            "<b>Current Flight:</b> %s (%s)<br>" +
            "<b>From:</b> %s<br>" +
            "<b>To:</b> %s<br>" +
            "<b>Date:</b> %s<br>" +
            "<b>Price Details:</b><br>%s</html>",
            bookingToEdit.getCustomer().getName(),
            bookingToEdit.getFlight().getFlightNumber(),
            bookingToEdit.getFlight().getDepartureDate(),
            bookingToEdit.getFlight().getOrigin(),
            bookingToEdit.getFlight().getDestination(),
            bookingToEdit.getFlight().getDepartureDate(),
            bookingToEdit.getPriceBreakdown().replace("\n", "<br>")
        ));
    }
    
    private void updateNewFlightDetails(Flight flight) {
        if (flight != null) {
            double currentPrice = flight.calculateCurrentPrice(
                parent.getFlightBookingSystem().getSystemDate());
            
            newFlightDetailsLabel.setText(String.format(
                "<html><b>Flight Number:</b> %s<br>" +
                "<b>From:</b> %s<br>" +
                "<b>To:</b> %s<br>" +
                "<b>Date:</b> %s<br>" +
                "<b>Available Seats:</b> %d<br>" +
                "<b>Current Price:</b> £%.2f</html>",
                flight.getFlightNumber(),
                flight.getOrigin(),
                flight.getDestination(),
                flight.getDepartureDate(),
                flight.getAvailableSeats(),
                currentPrice));
        }
    }
    
    private void updatePriceDisplay() {
        FlightItem selectedFlight = (FlightItem) flightCombo.getSelectedItem();
        if (selectedFlight != null) {
            double currentPrice = selectedFlight.flight.calculateCurrentPrice(
                parent.getFlightBookingSystem().getSystemDate());
            
            StringBuilder priceText = new StringBuilder("<html>");
            priceText.append(String.format("Original Booking Price: £%.2f<br>", bookingToEdit.getBookedPrice()));
            priceText.append(String.format("New Flight Price: £%.2f<br>", currentPrice));
            priceText.append(String.format("Rebooking Fee: £%.2f<br>", REBOOKING_FEE));
            priceText.append("<br>");
            priceText.append(String.format("<b>Total Price: £%.2f</b>", currentPrice + REBOOKING_FEE));
            
            // Show price factors
            priceText.append("<br><br>Price Factors:<br>");
            
            double occupancyRate = 1.0 - (selectedFlight.flight.getAvailableSeats() / 
                (double) selectedFlight.flight.getCapacity());
            
            if (occupancyRate >= 0.8) {
                priceText.append("• High Demand (+30%)<br>");
            } else if (occupancyRate >= 0.5) {
                priceText.append("• Medium Demand (+15%)<br>");
            }
            
            long daysUntil = ChronoUnit.DAYS.between(
                parent.getFlightBookingSystem().getSystemDate(), 
                selectedFlight.flight.getDepartureDate());
            
            if (daysUntil <= 7) {
                priceText.append("• Last Minute Booking (+50%)<br>");
            } else if (daysUntil <= 30) {
                priceText.append("• Near Departure (+25%)<br>");
            }
            
            priceText.append("</html>");
            priceLabel.setText(priceText.toString());
        }
    }
    
    private void updateBooking() {
        try {
            FlightItem selectedFlight = (FlightItem) flightCombo.getSelectedItem();
            if (selectedFlight == null) {
                throw new FlightBookingSystemException("Please select a new flight");
            }
            
            // Check flight capacity
            if (!selectedFlight.flight.hasAvailableSeats()) {
                throw new FlightBookingSystemException("Selected flight is at full capacity");
            }
            
            // Show rebooking fee warning
            int confirm = JOptionPane.showConfirmDialog(this,
                String.format("Rebooking will incur a fee of £%.2f\n\n" +
                    "Current Price: £%.2f\n" +
                    "New Flight Price: £%.2f\n" +
                    "Rebooking Fee: £%.2f\n" +
                    "Total New Price: £%.2f\n\n" +
                    "Do you want to proceed?",
                    REBOOKING_FEE,
                    bookingToEdit.getBookedPrice(),
                    selectedFlight.currentPrice,
                    REBOOKING_FEE,
                    selectedFlight.currentPrice + REBOOKING_FEE),
                "Confirm Rebooking",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
                
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
            
            String reason = JOptionPane.showInputDialog(this,
                "Please enter a reason for rebooking:",
                "Rebooking Reason",
                JOptionPane.QUESTION_MESSAGE);
                
            if (reason == null || reason.trim().isEmpty()) {
                reason = "No reason provided";
            }
            
            // Execute the command
            bookingToEdit.rebook(selectedFlight.flight, 
                parent.getFlightBookingSystem().getSystemDate(), 
                reason);
            
            // Save changes
            parent.saveData();
            
            // Update view and close dialog
            parent.showBookings();
            dispose();
            showSuccess("Booking updated successfully");
            
        } catch (FlightBookingSystemException ex) {
            showError(ex.getMessage());
        }
    }
    
    private static class FlightItem {
        final Flight flight;
        final double currentPrice;
        
        FlightItem(Flight flight, LocalDate systemDate) {
            this.flight = flight;
            this.currentPrice = flight.calculateCurrentPrice(systemDate);
        }
        
        @Override
        public String toString() {
            return String.format("#%d - %s (%s to %s) - %s - %d seats - £%.2f",
                flight.getId(),
                flight.getFlightNumber(),
                flight.getOrigin(),
                flight.getDestination(),
                flight.getDepartureDate(),
                flight.getAvailableSeats(),
                currentPrice);
        }
    }
} 