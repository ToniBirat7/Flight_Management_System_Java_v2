package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.AddFlight;
import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.gui.utils.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class AddFlightWindow extends JFrame implements ActionListener {

    private final MainWindow parent;
    private final JTextField flightNoText = new JTextField();
    private final JTextField originText = new JTextField();
    private final JTextField destinationText = new JTextField();
    private final JTextField depDateText = new JTextField();
    private final JTextField capacityText = new JTextField();
    private final JTextField priceText = new JTextField();

    private final JButton addBtn = new JButton("Add");
    private final JButton cancelBtn = new JButton("Cancel");

    public AddFlightWindow(MainWindow parent) {
        this.parent = parent;
        initialize();
    }

    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fall back to default look and feel
        }

        setTitle("Add New Flight");
        setSize(350, 250);
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        addFormField(formPanel, "Flight No:", flightNoText);
        addFormField(formPanel, "Origin:", originText);
        addFormField(formPanel, "Destination:", destinationText);
        addFormField(formPanel, "Departure Date (YYYY-MM-DD):", depDateText);
        addFormField(formPanel, "Capacity:", capacityText);
        addFormField(formPanel, "Price (£):", priceText);

        // Create buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        Theme.styleButton(addBtn, Theme.Button.PRIMARY_BG);
        Theme.styleButton(cancelBtn, Theme.Button.SECONDARY_BG);
        
        addBtn.addActionListener(this);
        cancelBtn.addActionListener(this);
        
        buttonPanel.add(addBtn);
        buttonPanel.add(cancelBtn);

        // Add panels to frame
        setLayout(new BorderLayout(10, 10));
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private void addFormField(JPanel panel, String label, JTextField field) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(Theme.REGULAR_FONT);
        panel.add(lbl);
        
        Theme.styleTextField(field);
        panel.add(field);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == addBtn) {
            addFlight();
        } else if (ae.getSource() == cancelBtn) {
            dispose();
        }
    }

    private void addFlight() {
        try {
            validateInputs();
            
            String flightNumber = flightNoText.getText().trim();
            String origin = originText.getText().trim();
            String destination = destinationText.getText().trim();
            LocalDate departureDate = LocalDate.parse(depDateText.getText().trim());
            int capacity = Integer.parseInt(capacityText.getText().trim());
            double price = Double.parseDouble(priceText.getText().trim());

            Command addFlight = new AddFlight(flightNumber, origin, destination, 
                                            departureDate, capacity, price);
            addFlight.execute(parent.getFlightBookingSystem());
            
            parent.saveData();
            parent.showFlights();
            dispose();
            
            JOptionPane.showMessageDialog(parent, 
                "Flight added successfully", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (FlightBookingSystemException | NumberFormatException | DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, 
                ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void validateInputs() throws FlightBookingSystemException {
        if (flightNoText.getText().trim().isEmpty() || 
            originText.getText().trim().isEmpty() || 
            destinationText.getText().trim().isEmpty() || 
            depDateText.getText().trim().isEmpty() || 
            capacityText.getText().trim().isEmpty() || 
            priceText.getText().trim().isEmpty()) {
            throw new FlightBookingSystemException("All fields are required");
        }

        try {
            int capacity = Integer.parseInt(capacityText.getText().trim());
            if (capacity <= 0) {
                throw new FlightBookingSystemException("Capacity must be greater than 0");
            }
        } catch (NumberFormatException ex) {
            throw new FlightBookingSystemException("Invalid capacity format");
        }

        try {
            double price = Double.parseDouble(priceText.getText().trim());
            if (price <= 0) {
                throw new FlightBookingSystemException("Price must be greater than 0");
            }
        } catch (NumberFormatException ex) {
            throw new FlightBookingSystemException("Invalid price format");
        }

        try {
            LocalDate departureDate = LocalDate.parse(depDateText.getText().trim());
            if (departureDate.isBefore(parent.getFlightBookingSystem().getSystemDate())) {
                throw new FlightBookingSystemException("Departure date cannot be in the past");
            }
        } catch (DateTimeParseException ex) {
            throw new FlightBookingSystemException("Invalid date format (use YYYY-MM-DD)");
        }
    }
}
