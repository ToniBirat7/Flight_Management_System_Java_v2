package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerBookingsWindow extends JFrame {
    
    private final Customer customer;
    private JTable bookingsTable;
    
    public CustomerBookingsWindow(Customer customer) {
        this.customer = customer;
        initialize();
    }
    
    private void initialize() {
        setTitle("Bookings for " + customer.getName());
        setSize(700, 400);
        setLocationRelativeTo(null);
        
        // Create header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(51, 153, 255));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        
        JLabel titleLabel = new JLabel(customer.getDetailsShort());
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.add(titleLabel);
        
        // Create table
        String[] columns = {"Booking ID", "Flight No", "From", "To", "Date", "Status"};
        List<Booking> bookings = customer.getBookings();
        Object[][] data = new Object[bookings.size()][6];
        
        for (int i = 0; i < bookings.size(); i++) {
            Booking booking = bookings.get(i);
            data[i][0] = booking.getId();
            data[i][1] = booking.getFlight().getFlightNumber();
            data[i][2] = booking.getFlight().getOrigin();
            data[i][3] = booking.getFlight().getDestination();
            data[i][4] = booking.getFlight().getDepartureDate();
            data[i][5] = booking.isActive() ? "Active" : "Cancelled";
        }
        
        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        bookingsTable = new JTable(model);
        bookingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookingsTable.getTableHeader().setReorderingAllowed(false);
        bookingsTable.setRowHeight(25);
        
        // Style the table
        bookingsTable.getTableHeader().setBackground(new Color(51, 153, 255));
        bookingsTable.getTableHeader().setForeground(Color.WHITE);
        bookingsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // Add components to frame
        add(headerPanel, BorderLayout.NORTH);
        add(new JScrollPane(bookingsTable), BorderLayout.CENTER);
        
        // Add close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        setVisible(true);
    }
} 