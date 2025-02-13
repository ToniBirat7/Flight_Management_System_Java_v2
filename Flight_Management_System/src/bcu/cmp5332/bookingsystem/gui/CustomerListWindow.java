package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerListWindow extends JFrame {
    
    private final FlightBookingSystem fbs;
    private JTable customerTable;
    
    public CustomerListWindow(FlightBookingSystem fbs) {
        this.fbs = fbs;
        initialize();
    }
    
    private void initialize() {
        setTitle("Customer List");
        setSize(800, 500);
        setLocationRelativeTo(null);
        
        // Create header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(51, 153, 255));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        
        JLabel titleLabel = new JLabel("Customer Management");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.add(titleLabel);
        
        // Create table
        String[] columns = {"ID", "Name", "Phone", "Email", "Total Bookings"};
        List<Customer> customers = fbs.getCustomers();
        Object[][] data = new Object[customers.size()][5];
        
        for (int i = 0; i < customers.size(); i++) {
            Customer customer = customers.get(i);
            data[i][0] = customer.getId();
            data[i][1] = customer.getName();
            data[i][2] = customer.getPhone();
            data[i][3] = customer.getEmail();
            data[i][4] = customer.getBookings().size();
        }
        
        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        customerTable = new JTable(model);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customerTable.getTableHeader().setReorderingAllowed(false);
        customerTable.setRowHeight(25);
        
        // Style the table
        customerTable.getTableHeader().setBackground(new Color(51, 153, 255));
        customerTable.getTableHeader().setForeground(Color.WHITE);
        customerTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // Add double-click listener
        customerTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = customerTable.getSelectedRow();
                    if (row != -1) {
                        int customerId = (int) customerTable.getValueAt(row, 0);
                        Customer customer = fbs.getCustomers().get(row);
                        new CustomerBookingsWindow(customer);
                    }
                }
            }
        });
        
        // Add components to frame
        add(headerPanel, BorderLayout.NORTH);
        add(new JScrollPane(customerTable), BorderLayout.CENTER);
        
        // Add buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        JButton addButton = new JButton("Add New Customer");
        JButton viewButton = new JButton("View Bookings");
        JButton closeButton = new JButton("Close");
        
        addButton.addActionListener(e -> new AddCustomerWindow(fbs, this));
        viewButton.addActionListener(e -> {
            int row = customerTable.getSelectedRow();
            if (row != -1) {
                Customer customer = fbs.getCustomers().get(row);
                new CustomerBookingsWindow(customer);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Please select a customer first", 
                    "No Selection", 
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        closeButton.addActionListener(e -> dispose());
        
        buttonPanel.add(addButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        setVisible(true);
    }
    
    public void refreshTable() {
        // Refresh the table data
        DefaultTableModel model = (DefaultTableModel) customerTable.getModel();
        model.setRowCount(0);
        
        for (Customer customer : fbs.getCustomers()) {
            model.addRow(new Object[]{
                customer.getId(),
                customer.getName(),
                customer.getPhone(),
                customer.getEmail(),
                customer.getBookings().size()
            });
        }
    }
} 