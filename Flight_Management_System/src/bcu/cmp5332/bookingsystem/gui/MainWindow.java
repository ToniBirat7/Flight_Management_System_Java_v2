package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.gui.utils.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.ListSelectionModel;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.time.temporal.ChronoUnit;

/**
 * Main window of the GUI application.
 * Manages all views and user interactions for the Flight Booking System.
 * This class serves as the primary container for all GUI components and
 * coordinates between user actions and the business logic.
 */
public class MainWindow extends JFrame {

    /** Fee charged when cancelling a booking */
    private static final double CANCELLATION_FEE = 50.0;

    /** Fee charged when rebooking a flight */
    private static final double REBOOKING_FEE = 30.0;

    /** Main panel containing all GUI components */
    private final JPanel mainPanel;

    /** Table for displaying flights, bookings, or customers */
    private JTable contentTable;

    /** Tracks the current view being displayed */
    private View currentView;

    /** Reference to the flight booking system model */
    private final FlightBookingSystem fbs;

    // Menu Items for Flights
    /** Menu item for viewing all flights */
    private final JMenuItem flightsView;

    /** Menu item for adding a new flight */
    private final JMenuItem flightsAdd;

    /** Menu item for deleting a flight */
    private final JMenuItem flightsDel;

    // Menu Items for Bookings
    /** Menu item for creating a new booking */
    private final JMenuItem bookingsIssue;

    /** Menu item for viewing all bookings */
    private final JMenuItem bookingsView;

    /** Menu item for updating an existing booking */
    private final JMenuItem bookingsUpdate;

    /** Menu item for cancelling a booking */
    private final JMenuItem bookingsCancel;

    // Menu Items for Customers
    /** Menu item for viewing all customers */
    private final JMenuItem custView;

    /** Menu item for adding a new customer */
    private final JMenuItem custAdd;

    /** Menu item for deleting a customer */
    private final JMenuItem custDel;

    /**
     * Constructs the main window of the Flight Booking System.
     * Initializes all GUI components, sets up the menu system, and creates the
     * welcome screen.
     * 
     * @param fbs The FlightBookingSystem instance that contains the business logic
     *            and data
     */
    public MainWindow(FlightBookingSystem fbs) {
        this.fbs = fbs;
        this.mainPanel = new JPanel(new BorderLayout(10, 10));

        // Initialize menu items
        flightsView = createMenuItem("View Flights", e -> showFlights());
        flightsAdd = createMenuItem("Add Flight", e -> new AddFlightDialog(this));
        flightsDel = createMenuItem("Delete Flight", e -> deleteSelectedFlight());

        bookingsIssue = createMenuItem("New Booking", e -> new AddBookingDialog(this));
        bookingsView = createMenuItem("View Bookings", e -> showBookings());
        bookingsUpdate = createMenuItem("Edit Booking", e -> editSelectedBooking());
        bookingsCancel = createMenuItem("Cancel Booking", e -> cancelSelectedBooking());

        custView = createMenuItem("View Customers", e -> showCustomers());
        custAdd = createMenuItem("Add Customer", e -> new AddCustomerDialog(this));
        custDel = createMenuItem("Delete Customer", e -> deleteSelectedCustomer());

        initialize();
    }

    /**
     * Initializes the main window components.
     * Sets up the window properties, creates the welcome panel, and configures the
     * layout.
     */
    private void initialize() {
        setTitle("Flight Booking Management System");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel.setBackground(Theme.BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        setupTable();
        createMenuBar();

        // Create welcome panel with header
        JPanel headerPanel = new JPanel();
        headerPanel.setName("headerPanel");
        headerPanel.setBackground(Theme.PRIMARY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Welcome");
        titleLabel.setFont(Theme.TITLE_FONT);
        titleLabel.setForeground(Theme.TEXT_LIGHT);
        headerPanel.add(titleLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        createWelcomePanel();

        setContentPane(mainPanel);
        setVisible(true);
    }

    /**
     * Creates and configures the main menu bar.
     * Sets up the Flights, Bookings, and Customers menus with their respective
     * items.
     */
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Theme.PRIMARY);
        menuBar.setBorder(BorderFactory.createEmptyBorder());

        // Flights Menu
        JMenu flightsMenu = createMenu("Flights");
        flightsMenu.add(flightsView);
        flightsMenu.add(flightsAdd);
        flightsMenu.add(flightsDel);

        // Bookings Menu
        JMenu bookingsMenu = createMenu("Bookings");
        bookingsMenu.add(bookingsIssue);
        bookingsMenu.add(bookingsView);
        bookingsMenu.add(bookingsUpdate);
        bookingsMenu.add(bookingsCancel);

        // Customers Menu
        JMenu customersMenu = createMenu("Customers");
        customersMenu.add(custView);
        customersMenu.add(custAdd);
        customersMenu.add(custDel);

        menuBar.add(flightsMenu);
        menuBar.add(bookingsMenu);
        menuBar.add(customersMenu);

        setJMenuBar(menuBar);
    }

    /**
     * Creates a styled menu with the specified title.
     * 
     * @param title The text to display in the menu
     * @return A configured JMenu instance
     */
    private JMenu createMenu(String title) {
        JMenu menu = new JMenu(title);
        menu.setForeground(Theme.TEXT_LIGHT);
        menu.setFont(Theme.HEADER_FONT);
        return menu;
    }

    /**
     * Creates a menu item with the specified title and action listener.
     * 
     * @param title    The text to display in the menu item
     * @param listener The action listener to handle clicks
     * @return A configured JMenuItem instance
     */
    private JMenuItem createMenuItem(String title, ActionListener listener) {
        JMenuItem item = new JMenuItem(title);
        item.setFont(Theme.REGULAR_FONT);
        item.addActionListener(listener);
        return item;
    }

    /**
     * Creates and configures the welcome panel displayed when the application
     * starts.
     * Sets up a centered layout with welcome message and instructions.
     */
    private void createWelcomePanel() {
        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.setBackground(Theme.BACKGROUND);

        JLabel welcomeLabel = new JLabel("Welcome to Flight Booking System");
        welcomeLabel.setFont(Theme.TITLE_FONT);
        welcomeLabel.setForeground(Theme.TEXT_PRIMARY);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel instructionLabel = new JLabel("Please select an option from the menu above");
        instructionLabel.setFont(Theme.REGULAR_FONT);
        instructionLabel.setForeground(Theme.TEXT_PRIMARY);
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        welcomePanel.add(Box.createVerticalGlue());
        welcomePanel.add(welcomeLabel);
        welcomePanel.add(Box.createVerticalStrut(20));
        welcomePanel.add(instructionLabel);
        welcomePanel.add(Box.createVerticalGlue());

        mainPanel.add(welcomePanel, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    /**
     * Sets up the main content table with custom tooltip support for price
     * information.
     * Configures table properties such as selection mode and column reordering.
     */
    private void setupTable() {
        contentTable = new JTable() {
            @Override
            public String getToolTipText(MouseEvent e) {
                try {
                    int row = rowAtPoint(e.getPoint());
                    int col = columnAtPoint(e.getPoint());

                    if (row >= 0 && col >= 0 && getColumnName(col).equals("Price")) {
                        int flightId = (int) getValueAt(row, 0);
                        Flight flight = fbs.getFlightByID(flightId);
                        return getPriceDetailsTooltip(flight);
                    }
                } catch (FlightBookingSystemException ex) {
                    // Ignore tooltip on error
                }
                return super.getToolTipText(e);
            }
        };

        Theme.styleTable(contentTable);
        contentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contentTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(contentTable);
        scrollPane.getViewport().setBackground(Theme.BACKGROUND);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Updates the header panel with a new title.
     * Removes the existing header and creates a new one with the specified title.
     *
     * @param title The text to display in the header
     */
    private void updateHeader(String title) {
        // Remove existing header
        for (Component comp : mainPanel.getComponents()) {
            if (comp instanceof JPanel && "headerPanel".equals(comp.getName())) {
                mainPanel.remove(comp);
                break;
            }
        }

        // Create new header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setName("headerPanel");
        headerPanel.setBackground(Theme.PRIMARY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Theme.TITLE_FONT);
        titleLabel.setForeground(Theme.TEXT_LIGHT);
        headerPanel.add(titleLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    /**
     * Updates the main panel with new data in a table format.
     * Clears existing content and creates a new table with the provided data.
     *
     * @param title   The title for the panel header
     * @param columns The column headers for the table
     * @param data    The table data as a 2D array
     */
    private void updateMainPanel(String title, String[] columns, Object[][] data) {
        mainPanel.removeAll();

        // Create header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Theme.PRIMARY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Theme.TITLE_FONT);
        titleLabel.setForeground(Theme.TEXT_LIGHT);
        headerPanel.add(titleLabel);

        // Create table
        contentTable = new JTable(data, columns);
        Theme.styleTable(contentTable);
        JScrollPane scrollPane = new JScrollPane(contentTable);
        scrollPane.getViewport().setBackground(Theme.BACKGROUND);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    /**
     * Displays the list of flights in the table.
     * Populates the table with flight details such as ID, flight number, origin,
     * destination, departure date, available seats, price, and status.
     * 
     * The flight prices are dynamically calculated based on the system date.
     * The table is set to be non-editable.
     * 
     * If an error occurs while retrieving flight data, an error message is
     * displayed.
     */
    public void showFlights() {
        try {
            String[] columns = { "ID", "Flight No", "Origin", "Destination", "Date", "Capacity", "Price", "Status" };
            DefaultTableModel model = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }

                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    if (columnIndex == 0)
                        return Integer.class;
                    return String.class;
                }
            };

            for (Flight flight : fbs.getFlights()) {
                double currentPrice = flight.calculateCurrentPrice(fbs.getSystemDate());
                String priceDisplay = formatPrice(flight.getPrice(), currentPrice);
                String status = getFlightStatus(flight);

                model.addRow(new Object[] {
                        flight.getId(),
                        flight.getFlightNumber(),
                        flight.getOrigin(),
                        flight.getDestination(),
                        flight.getDepartureDate(),
                        String.format("%d/%d", flight.getAvailableSeats(), flight.getCapacity()),
                        priceDisplay,
                        status
                });
            }

            contentTable.setModel(model);
            currentView = View.FLIGHTS;
            updateHeader("Flights");

        } catch (Exception ex) {
            showError("Error loading flights: " + ex.getMessage());
        }
    }

    /**
     * Formats the price display string based on base and current prices.
     * Shows an arrow if the current price differs from the base price.
     *
     * @param basePrice    The original price of the flight
     * @param currentPrice The calculated current price
     * @return Formatted price string
     */
    private String formatPrice(double basePrice, double currentPrice) {
        return basePrice == currentPrice ? String.format("£%.2f", currentPrice)
                : String.format("£%.2f → £%.2f", basePrice, currentPrice);
    }

    /**
     * Determines the status of a flight based on its properties.
     *
     * @param flight The flight to check
     * @return Status string ("Departed", "Full", or "Available")
     */
    private String getFlightStatus(Flight flight) {
        if (flight.hasDeparted(fbs.getSystemDate()))
            return "Departed";
        if (!flight.hasAvailableSeats())
            return "Full";
        return "Available";
    }

    /**
     * Generates a detailed tooltip showing price breakdown for a flight.
     *
     * @param flight The flight to generate the tooltip for
     * @return HTML-formatted tooltip string
     */
    private String getPriceDetailsTooltip(Flight flight) {
        StringBuilder tooltip = new StringBuilder("<html>Price Breakdown:<br>");
        tooltip.append(String.format("Base Price: £%.2f<br>", flight.getPrice()));

        addPriceFactors(tooltip, flight);

        tooltip.append(String.format("<br>Current Price: £%.2f</html>",
                flight.calculateCurrentPrice(fbs.getSystemDate())));

        return tooltip.toString();
    }

    /**
     * Adds price adjustment factors to the tooltip.
     * Includes demand-based and time-based price adjustments.
     *
     * @param tooltip The StringBuilder to append price factors to
     * @param flight  The flight being analyzed
     */
    private void addPriceFactors(StringBuilder tooltip, Flight flight) {
        double occupancyRate = 1.0 - (flight.getAvailableSeats() / (double) flight.getCapacity());
        if (occupancyRate >= 0.8) {
            tooltip.append("High Demand (+30%)<br>");
        } else if (occupancyRate >= 0.5) {
            tooltip.append("Medium Demand (+15%)<br>");
        }

        long daysUntil = ChronoUnit.DAYS.between(fbs.getSystemDate(), flight.getDepartureDate());
        if (daysUntil <= 7) {
            tooltip.append("Last Minute (+50%)<br>");
        } else if (daysUntil <= 30) {
            tooltip.append("Near Departure (+25%)<br>");
        }
    }

    /**
     * Retrieves the instance of the FlightBookingSystem.
     *
     * @return The FlightBookingSystem instance.
     */
    public FlightBookingSystem getFlightBookingSystem() {
        return fbs;
    }

    /**
     * Saves the current flight booking system data.
     * Displays an error message if saving fails.
     */
    public void saveData() {
        try {
            FlightBookingSystemData.store(fbs);
        } catch (IOException ex) {
            showError("Failed to save data: " + ex.getMessage());
        }
    }

    /**
     * Attempts to delete the selected flight from the system
     * Checks for capacity and existing bookings before deletion
     * Shows confirmation dialog before proceeding
     */
    private void deleteSelectedFlight() {
        int flightId = getSelectedFlightId();
        if (flightId == -1)
            return;

        try {
            Flight flight = fbs.getFlightByID(flightId);

            // Check if flight is at capacity
            if (!flight.hasAvailableSeats()) {
                showError("This flight is at full capacity");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this flight?\n\n" +
                            "Flight Number: " + flight.getFlightNumber() + "\n" +
                            "Route: " + flight.getOrigin() + " to " + flight.getDestination() + "\n" +
                            "Date: " + flight.getDepartureDate() + "\n" +
                            "Capacity: " + flight.getCapacity() + " seats\n" +
                            "Price: £" + String.format("%.2f", flight.getPrice()) + "\n\n" +
                            "This action cannot be undone.",
                    "Confirm Flight Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                fbs.removeFlight(flight);
                saveData();
                showFlights();
                showSuccess("Flight deleted successfully");
            }
        } catch (FlightBookingSystemException ex) {
            showError(ex.getMessage());
        }
    }

    /**
     * Gets the ID of the selected flight after performing validation checks.
     * Verifies the flight exists and can be modified.
     * 
     * @return the flight ID if valid, -1 if invalid or no selection
     */
    private int getSelectedFlightId() {
        int selectedRow = contentTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a flight to delete");
            return -1;
        }

        // Verify we're in the flights view
        if (contentTable.getColumnCount() != 8 ||
                !contentTable.getColumnName(0).equals("ID") ||
                !contentTable.getColumnName(1).equals("Flight No")) {
            showError("Please view flights first before performing this operation");
            return -1;
        }

        int flightId = (int) contentTable.getValueAt(selectedRow, 0);

        try {
            Flight flight = fbs.getFlightByID(flightId);
            if (flight == null) {
                showError("Selected flight not found in the system");
                return -1;
            }

            // Check if flight is in the past
            if (flight.getDepartureDate().isBefore(fbs.getSystemDate())) {
                showError("Cannot modify flights from the past");
                return -1;
            }

            // Check if flight has passengers when deleting
            if (!flight.getPassengers().isEmpty()) {
                StringBuilder message = new StringBuilder();
                message.append("Cannot delete flight with existing bookings:\n\n");
                message.append(String.format("Flight: %s (%s to %s)\n",
                        flight.getFlightNumber(), flight.getOrigin(), flight.getDestination()));
                message.append(String.format("Date: %s\n", flight.getDepartureDate()));
                message.append(String.format("Passengers: %d\n\n", flight.getPassengers().size()));
                message.append("Please cancel all bookings first.");

                showError(message.toString());
                return -1;
            }

            return flightId;

        } catch (Exception ex) {
            showError("Error validating flight: " + ex.getMessage());
            return -1;
        }
    }

    /**
     * Shows all bookings in the system with their current status.
     * Displays booking details including customer, flight, and status.
     */
    public void showBookings() {
        List<Booking> bookings = getAllBookings();
        String[] columns = { "ID", "Customer", "Flight", "Date", "Price", "Status" };
        Object[][] data = new Object[bookings.size()][6];

        for (int i = 0; i < bookings.size(); i++) {
            Booking booking = bookings.get(i);
            data[i] = new Object[] {
                    booking.getId(),
                    booking.getCustomer().getName(),
                    booking.getFlight().getFlightNumber(),
                    booking.getFlight().getDepartureDate(),
                    booking.getPriceBreakdown().replace("\n", "<br>"),
                    booking.isActive() ? "Active" : "Cancelled"
            };
        }

        updateMainPanel("Bookings", columns, data);
    }

    /**
     * Gets all bookings from all customers in the system.
     * 
     * @return List of all bookings
     */
    public List<Booking> getAllBookings() {
        List<Booking> allBookings = new ArrayList<>();
        for (Customer customer : fbs.getCustomers()) {
            allBookings.addAll(customer.getBookings());
        }
        return allBookings;
    }

    /**
     * Finds a booking by its ID.
     * 
     * @param bookingId The ID of the booking to find
     * @return The booking if found, null otherwise
     */
    public Booking findBookingById(int bookingId) {
        return getAllBookings().stream()
                .filter(b -> b.getId() == bookingId)
                .findFirst()
                .orElse(null);
    }

    /**
     * Cancels a booking with the given ID.
     * Shows confirmation dialog and updates the flight's passenger list.
     * 
     * @param bookingId The ID of the booking to cancel
     * @throws FlightBookingSystemException if booking cannot be cancelled
     */
    public void cancelBooking(int bookingId) throws FlightBookingSystemException {
        Booking booking = findBookingById(bookingId);
        if (booking == null) {
            throw new FlightBookingSystemException("Booking not found");
        }
        if (!booking.isActive()) {
            throw new FlightBookingSystemException("Booking is already cancelled");
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel this booking?\n\n" +
                        "Customer: " + booking.getCustomer().getName() + "\n" +
                        "Flight: " + booking.getFlight().getFlightNumber() + "\n" +
                        "Date: " + booking.getFlight().getDepartureDate() + "\n" +
                        "Price: " + booking.getPriceBreakdown() + "\n\n" +
                        "A cancellation fee of £" + CANCELLATION_FEE + " will be applied.\n" +
                        "This action cannot be undone.",
                "Confirm Booking Cancellation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            throw new FlightBookingSystemException("Cancellation aborted by user");
        }

        try {
            booking.cancel("Cancelled through GUI");
            booking.getFlight().removePassenger(booking.getCustomer());
            saveData();
        } catch (Exception ex) {
            throw new FlightBookingSystemException("Failed to cancel booking: " + ex.getMessage());
        }
    }

    /**
     * Shows the customers view with all active (non-deleted) customers.
     * Displays customer details including their active bookings count.
     */
    public void showCustomers() {
        List<Customer> customers = fbs.getCustomers();
        String[] columns = { "ID", "Name", "Phone", "Email", "Active Bookings" };
        Object[][] data = new Object[customers.size()][5];

        for (int i = 0; i < customers.size(); i++) {
            Customer customer = customers.get(i);
            long activeBookings = customer.getBookings().stream()
                    .filter(Booking::isActive)
                    .count();

            data[i] = new Object[] {
                    customer.getId(),
                    customer.getName(),
                    customer.getPhone(),
                    customer.getEmail(),
                    activeBookings
            };
        }

        updateMainPanel("Customers", columns, data);
    }

    /**
     * Shows an error message dialog.
     * 
     * @param message the error message to display
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Shows a success message dialog.
     * 
     * @param message the success message to display
     */
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Gets the ID of the selected booking after performing validation checks.
     * Verifies the booking is active and not in the past.
     * 
     * @return the booking ID if valid, -1 if invalid or no selection
     */
    private int getSelectedBookingId() {
        int selectedRow = contentTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a booking to edit");
            return -1;
        }

        // Verify we're in the bookings view
        if (contentTable.getColumnCount() != 6 ||
                !contentTable.getColumnName(0).equals("ID") ||
                !contentTable.getColumnName(2).equals("Flight")) {
            showError("Please view bookings first before editing");
            return -1;
        }

        int bookingId = (int) contentTable.getValueAt(selectedRow, 0);
        String status = (String) contentTable.getValueAt(selectedRow, 5);

        // Check if booking is cancelled
        if ("Cancelled".equals(status)) {
            showError("Cannot edit cancelled bookings");
            return -1;
        }

        // Verify booking exists and is valid
        try {
            Booking booking = findBookingById(bookingId);
            if (booking == null) {
                showError("Selected booking not found in the system");
                return -1;
            }
            if (!booking.isActive()) {
                showError("This booking is no longer active");
                return -1;
            }
            if (booking.getFlight().getDepartureDate().isBefore(fbs.getSystemDate())) {
                showError("Cannot edit bookings for past flights");
                return -1;
            }
            return bookingId;

        } catch (Exception ex) {
            showError("Error validating booking: " + ex.getMessage());
            return -1;
        }
    }

    /**
     * Updates the main window's system state with new data.
     * 
     * @param newState the new FlightBookingSystem state
     */
    public void updateSystemState(FlightBookingSystem newState) {
        this.fbs = newState;
    }

    /**
     * Handles editing of a selected booking.
     * Validates the booking and opens the edit dialog if valid.
     */
    private void editSelectedBooking() {
        int selectedRow = contentTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a booking to edit");
            return;
        }

        try {
            int bookingId = (int) contentTable.getValueAt(selectedRow, 0);
            Booking booking = findBookingById(bookingId);

            if (booking == null) {
                showError("Booking not found");
                return;
            }

            if (!booking.isActive()) {
                showError("Cannot edit cancelled bookings");
                return;
            }

            if (booking.getFlight().hasDeparted(fbs.getSystemDate())) {
                showError("Cannot edit bookings for departed flights");
                return;
            }

            new EditBookingDialog(this, bookingId);

        } catch (Exception ex) {
            showError("Error editing booking: " + ex.getMessage());
        }
    }

    /**
     * Handles the cancellation of a selected booking.
     * Shows confirmation dialog and processes the cancellation if confirmed.
     */
    private void cancelSelectedBooking() {
        int selectedRow = contentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a booking to cancel",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookingId = (int) contentTable.getValueAt(selectedRow, 0);
        try {
            cancelBooking(bookingId);
            showBookings(); // Refresh the view
            JOptionPane.showMessageDialog(this,
                    "Booking cancelled successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (FlightBookingSystemException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Attempts to delete the selected customer from the system.
     * Checks for active bookings before deletion and shows confirmation dialog.
     */
    public void deleteSelectedCustomer() {
        try {
            int selectedRow = contentTable.getSelectedRow();
            if (selectedRow == -1) {
                showError("Please select a customer to delete");
                return;
            }

            int customerId = (int) contentTable.getValueAt(selectedRow, 0);
            Customer customer = fbs.getCustomerByID(customerId);

            if (customer == null) {
                showError("Customer not found");
                return;
            }

            // Check for active bookings
            boolean hasActiveBookings = customer.getBookings().stream()
                    .anyMatch(Booking::isActive);

            if (hasActiveBookings) {
                showError("Cannot delete customer with active bookings. Please cancel all bookings first.");
                return;
            }

            // Confirm deletion
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this customer?\n\n" +
                            "Customer: " + customer.getName() + "\n" +
                            "Email: " + customer.getEmail() + "\n\n" +
                            "This action cannot be undone.",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            // Delete the customer
            fbs.removeCustomer(customer);
            saveData();
            showCustomers(); // Refresh the view
            showSuccess("Customer deleted successfully");

        } catch (FlightBookingSystemException ex) {
            showError(ex.getMessage());
        }
    }
}
