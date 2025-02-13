package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.gui.utils.BaseDialog;
import bcu.cmp5332.bookingsystem.gui.utils.Theme;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ViewBookingsDialog extends BaseDialog {
    
    private final MainWindow parent;
    private final Customer customer;
    private JTable bookingsTable;
    
    public ViewBookingsDialog(MainWindow parent, Customer customer) {
        super(parent, "Bookings for " + customer.getName());
        this.parent = parent;
        this.customer = customer;
        
        initializeComponents();
        loadBookings();
        
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }
    
    private void initializeComponents() {
        // Create table
        String[] columns = {"Booking ID", "Flight No", "From", "To", "Date", "Price", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        bookingsTable = new JTable(model);
        Theme.styleTable(bookingsTable);
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.setPreferredSize(new Dimension(800, 300));
        contentPanel.add(scrollPane);
        
        // Add close button
        JButton closeButton = createButton("Close", Theme.Button.SECONDARY_BG);
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
    }
    
    private void loadBookings() {
        DefaultTableModel model = (DefaultTableModel) bookingsTable.getModel();
        List<Booking> bookings = customer.getBookings();
        
        for (Booking booking : bookings) {
            model.addRow(new Object[]{
                booking.getId(),
                booking.getFlight().getFlightNumber(),
                booking.getFlight().getOrigin(),
                booking.getFlight().getDestination(),
                booking.getFlight().getDepartureDate(),
                String.format("£%.2f", booking.getTotalPrice()),
                booking.isActive() ? "Active" : "Cancelled"
            });
        }
    }
} 