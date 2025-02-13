package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.AddCustomer;
import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.gui.utils.BaseDialog;
import bcu.cmp5332.bookingsystem.gui.utils.Theme;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class AddCustomerDialog extends BaseDialog {
    
	// Instance variables

	/** Reference to the main application window (parent component). */
	private final MainWindow parent;

	/** Text field for entering the customer's name, styled using createStyledTextField(). */
	private final JTextField nameField = createStyledTextField();

	/** Text field for entering the customer's phone number, styled using createStyledTextField(). */
	private final JTextField phoneField = createStyledTextField();

	/** Text field for entering the customer's email address, styled using createStyledTextField(). */
	private final JTextField emailField = createStyledTextField();

	/** Label for displaying error messages. Initially empty to avoid clutter. */
	private final JLabel errorLabel = new JLabel(" ");
    
    public AddCustomerDialog(MainWindow parent) {
        super(parent, "Add New Customer");
        this.parent = parent;
        
        initializeComponents();
        setupValidation();
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }
    
    private void initializeComponents() {
        // Set up the form
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Add form fields with hints
        addFormFieldWithHint("Name:", nameField, "Enter customer's full name", gbc, 0);
        addFormFieldWithHint("Phone:", phoneField, "Enter 11-digit phone number", gbc, 1);
        addFormFieldWithHint("Email:", emailField, "Enter valid email address", gbc, 2);
        
        // Error label
        errorLabel.setForeground(Theme.WARNING);
        errorLabel.setFont(Theme.REGULAR_FONT);
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        contentPanel.add(errorLabel, gbc);
        
        // Add buttons
        JButton addButton = createButton("Add Customer", Theme.Button.SUCCESS_BG);
        JButton cancelButton = createButton("Cancel", Theme.Button.SECONDARY_BG);
        
        addButton.addActionListener(e -> addCustomer());
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
                field.setBorder(new CompoundBorder(
                    new LineBorder(Theme.SECONDARY, 1),
                    new EmptyBorder(5, 10, 5, 10)
                ));
            }
        });
    }
    
    private void setupValidation() {
        // Real-time phone number validation
        phoneField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validatePhone(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validatePhone(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validatePhone(); }
        });
        
        // Real-time email validation
        emailField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validateEmail(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validateEmail(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validateEmail(); }
        });
    }
    
    private void validatePhone() {
        String phone = phoneField.getText().trim();
        if (!phone.isEmpty() && !phone.matches("^\\d{11}$")) {
            phoneField.setBackground(new Color(255, 240, 240));
            errorLabel.setText("Phone must be 11 digits");
        } else {
            phoneField.setBackground(Color.WHITE);
            errorLabel.setText(" ");
        }
    }
    
    private void validateEmail() {
        String email = emailField.getText().trim();
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            emailField.setBackground(new Color(255, 240, 240));
            errorLabel.setText("Invalid email format");
        } else {
            emailField.setBackground(Color.WHITE);
            errorLabel.setText(" ");
        }
    }
    
    private void addCustomer() {
        try {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();

            if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                showError("All fields are required");
                return;
            }

            // Validate phone format
            if (!phone.matches("^\\d{11}$")) {
                showError("Phone must be 11 digits");
                return;
            }

            // Validate email format
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                showError("Invalid email format");
                return;
            }

            // Create and execute the AddCustomer command
            Command addCustomer = new AddCustomer(name, phone, email);
            addCustomer.execute(parent.getFlightBookingSystem());
            
            // Save changes and update view
            parent.saveData();
            parent.showCustomers();
            
            dispose();
            showSuccess("Customer added successfully");
            
        } catch (FlightBookingSystemException ex) {
            showError(ex.getMessage());
        }
    }
} 