package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.AddFlight;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.gui.utils.BaseDialog;
import bcu.cmp5332.bookingsystem.gui.utils.Theme;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class AddFlightDialog extends BaseDialog {
    
    private final MainWindow parent;
    private final JTextField flightNoField = createStyledTextField();
    private final JTextField originField = createStyledTextField();
    private final JTextField destinationField = createStyledTextField();
    private final JTextField dateField = createStyledTextField();
    private final JTextField capacityField = createStyledTextField();
    private final JTextField priceField = createStyledTextField();
    private final JLabel errorLabel = new JLabel(" ");
    
    public AddFlightDialog(MainWindow parent) {
        super(parent, "Add New Flight");
        this.parent = parent;
        
        initializeComponents();
        setupValidation();
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }
    
    private void initializeComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Add form fields with hints
        addFormFieldWithHint("Flight Number:", flightNoField, "Enter unique flight number", gbc, 0);
        addFormFieldWithHint("Origin:", originField, "Enter departure city", gbc, 1);
        addFormFieldWithHint("Destination:", destinationField, "Enter arrival city", gbc, 2);
        addFormFieldWithHint("Date:", dateField, "Format: YYYY-MM-DD", gbc, 3);
        addFormFieldWithHint("Capacity:", capacityField, "Enter total seats", gbc, 4);
        addFormFieldWithHint("Price (£):", priceField, "Enter ticket price", gbc, 5);
        
        // Error label
        errorLabel.setForeground(Theme.WARNING);
        errorLabel.setFont(Theme.REGULAR_FONT);
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        contentPanel.add(errorLabel, gbc);
        
        // Add buttons
        JButton addButton = createButton("Add Flight", Theme.Button.SUCCESS_BG);
        JButton cancelButton = createButton("Cancel", Theme.Button.SECONDARY_BG);
        
        addButton.addActionListener(e -> addFlight());
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
    }
    
    private JTextField createStyledTextField() {
        JTextField field = createTextField();
        field.setPreferredSize(new Dimension(250, 35));
        field.setBorder(new CompoundBorder(
            new LineBorder(Theme.SECONDARY, 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }
    
    private void addFormFieldWithHint(String label, JTextField field, String hint, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        contentPanel.add(createLabel(label), gbc);
        
        gbc.gridx = 1;
        contentPanel.add(field, gbc);
        
        field.setToolTipText(hint);
        
        // Add focus listeners for visual feedback
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                field.setBorder(new CompoundBorder(
                    new LineBorder(Theme.ACCENT, 2),
                    new EmptyBorder(4, 9, 4, 9)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                validateField(field);
                field.setBorder(new CompoundBorder(
                    new LineBorder(Theme.SECONDARY, 1),
                    new EmptyBorder(5, 10, 5, 10)
                ));
            }
        });
    }
    
    private void setupValidation() {
        // Real-time validation for numeric fields
        capacityField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validateCapacity(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validateCapacity(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validateCapacity(); }
        });
        
        priceField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validatePrice(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validatePrice(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validatePrice(); }
        });
        
        dateField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validateDate(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validateDate(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validateDate(); }
        });
    }
    
    private void validateField(JTextField field) {
        if (field == capacityField) {
            validateCapacity();
        } else if (field == priceField) {
            validatePrice();
        } else if (field == dateField) {
            validateDate();
        }
    }
    
    private void validateCapacity() {
        String capacity = capacityField.getText().trim();
        try {
            int cap = Integer.parseInt(capacity);
            if (cap <= 0) {
                setFieldError(capacityField, "Capacity must be greater than 0");
            } else {
                clearFieldError(capacityField);
            }
        } catch (NumberFormatException e) {
            if (!capacity.isEmpty()) {
                setFieldError(capacityField, "Invalid capacity format");
            }
        }
    }
    
    private void validatePrice() {
        String price = priceField.getText().trim();
        try {
            double p = Double.parseDouble(price);
            if (p <= 0) {
                setFieldError(priceField, "Price must be greater than 0");
            } else {
                clearFieldError(priceField);
            }
        } catch (NumberFormatException e) {
            if (!price.isEmpty()) {
                setFieldError(priceField, "Invalid price format");
            }
        }
    }
    
    private void validateDate() {
        String date = dateField.getText().trim();
        try {
            LocalDate d = LocalDate.parse(date);
            if (d.isBefore(LocalDate.now())) {
                setFieldError(dateField, "Date must be in the future");
            } else {
                clearFieldError(dateField);
            }
        } catch (DateTimeParseException e) {
            if (!date.isEmpty()) {
                setFieldError(dateField, "Invalid date format (YYYY-MM-DD)");
            }
        }
    }
    
    private void setFieldError(JTextField field, String message) {
        field.setBackground(new Color(255, 240, 240));
        errorLabel.setText(message);
    }
    
    private void clearFieldError(JTextField field) {
        field.setBackground(Color.WHITE);
        errorLabel.setText(" ");
    }
    
    private void addFlight() {
        try {
            // Get and validate the flight details
            String flightNumber = flightNoField.getText().trim();
            String origin = originField.getText().trim();
            String destination = destinationField.getText().trim();
            String dateText = dateField.getText().trim();
            String capacityText = capacityField.getText().trim();
            String priceText = priceField.getText().trim();
            
            // Validate inputs
            if (flightNumber.isEmpty() || origin.isEmpty() || destination.isEmpty() 
                || dateText.isEmpty() || capacityText.isEmpty() || priceText.isEmpty()) {
                throw new FlightBookingSystemException("All fields are required");
            }
            
            // Parse and validate date
            LocalDate date = LocalDate.parse(dateText);
            if (date.isBefore(parent.getFlightBookingSystem().getSystemDate())) {
                throw new FlightBookingSystemException("Flight date cannot be in the past");
            }
            
            // Parse and validate capacity
            int capacity = Integer.parseInt(capacityText);
            if (capacity <= 0) {
                throw new FlightBookingSystemException("Capacity must be positive");
            }
            
            // Parse and validate price
            double price = Double.parseDouble(priceText);
            if (price <= 0) {
                throw new FlightBookingSystemException("Price must be positive");
            }
            
            // Execute the command
            new AddFlight(flightNumber, origin, destination, date, capacity, price)
                .execute(parent.getFlightBookingSystem());
            
            // Save changes
            parent.saveData();
            
            // Update view and close dialog
            parent.showFlights();
            dispose();
            showSuccess("Flight added successfully");
            
        } catch (NumberFormatException ex) {
            showError("Invalid number format");
        } catch (DateTimeParseException ex) {
            showError("Invalid date format (use YYYY-MM-DD)");
        } catch (FlightBookingSystemException ex) {
            showError(ex.getMessage());
        }
    }
} 