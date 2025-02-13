package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FlightPassengersWindow extends JFrame {
    
    private final Flight flight;
    private JTable passengersTable;
    
    public FlightPassengersWindow(Flight flight) {
        this.flight = flight;
        initialize();
    }
    
    private void initialize() {
        setTitle("Passengers on Flight #" + flight.getId() + " - " + flight.getFlightNumber());
        setSize(600, 400);
        setLocationRelativeTo(null);
        
        // Create header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(51, 153, 255));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        
        JLabel titleLabel = new JLabel(flight.getDetailsShort());
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.add(titleLabel);
        
        // Create table
        String[] columns = {"ID", "Name", "Phone", "Email", "Total Bookings"};
        List<Customer> passengers = flight.getPassengers();
        Object[][] data = new Object[passengers.size()][5];
        
        for (int i = 0; i < passengers.size(); i++) {
            Customer passenger = passengers.get(i);
            data[i][0] = passenger.getId();
            data[i][1] = passenger.getName();
            data[i][2] = passenger.getPhone();
            data[i][3] = passenger.getEmail();
            data[i][4] = passenger.getBookings().size();
        }
        
        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        passengersTable = new JTable(model);
        passengersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        passengersTable.getTableHeader().setReorderingAllowed(false);
        passengersTable.setRowHeight(25);
        
        // Style the table
        passengersTable.getTableHeader().setBackground(new Color(51, 153, 255));
        passengersTable.getTableHeader().setForeground(Color.WHITE);
        passengersTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // Add components to frame
        add(headerPanel, BorderLayout.NORTH);
        add(new JScrollPane(passengersTable), BorderLayout.CENTER);
        
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