package bcu.cmp5332.bookingsystem.gui.utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class BaseDialog extends JDialog {
    
    protected final JPanel mainPanel;
    protected final JPanel headerPanel;
    protected final JPanel contentPanel;
    protected final JPanel buttonPanel;
    
    public BaseDialog(Window owner, String title) {
        super(owner);
        setTitle(title);
        setModal(true);
        
        // Main panel
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Theme.BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header panel
        headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(Theme.PRIMARY);
        headerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Theme.TITLE_FONT);
        titleLabel.setForeground(Theme.TEXT_LIGHT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(titleLabel);
        
        // Content panel
        contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Theme.BACKGROUND);
        
        // Button panel
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Theme.BACKGROUND);
        
        // Add panels
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    protected JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        Theme.styleButton(button, bgColor);
        return button;
    }
    
    protected JTextField createTextField() {
        JTextField textField = new JTextField();
        Theme.styleTextField(textField);
        return textField;
    }
    
    protected JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.REGULAR_FONT);
        label.setForeground(Theme.TEXT_PRIMARY);
        return label;
    }
    
    protected void addFormField(String labelText, JComponent field, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        contentPanel.add(createLabel(labelText), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        contentPanel.add(field, gbc);
    }
    
    protected void showError(String message) {
        JOptionPane.showMessageDialog(this,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
    
    protected void showSuccess(String message) {
        JOptionPane.showMessageDialog(this,
            message,
            "Success",
            JOptionPane.INFORMATION_MESSAGE);
    }
} 