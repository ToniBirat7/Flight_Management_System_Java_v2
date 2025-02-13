package bcu.cmp5332.bookingsystem.gui.utils;

import java.awt.Color;
import java.awt.Font;

public class Theme {
    // Main colors
    public static final Color PRIMARY = new Color(45, 52, 54);      // Dark slate
    public static final Color SECONDARY = new Color(52, 152, 219);  // Blue
    public static final Color ACCENT = new Color(46, 204, 113);     // Green
    public static final Color WARNING = new Color(231, 76, 60);     // Red
    public static final Color BACKGROUND = new Color(236, 240, 241); // Light gray
    
    // Text colors
    public static final Color TEXT_PRIMARY = new Color(44, 62, 80);
    public static final Color TEXT_LIGHT = Color.WHITE;
    
    // Fonts
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    
    // Button styles
    public static class Button {
        public static final Color PRIMARY_BG = SECONDARY;
        public static final Color SECONDARY_BG = PRIMARY;
        public static final Color SUCCESS_BG = ACCENT;
        public static final Color DANGER_BG = WARNING;
    }
    
    // Custom styling methods
    public static void styleButton(javax.swing.JButton button, Color bgColor) {
        button.setFont(BUTTON_FONT);
        button.setBackground(bgColor);
        button.setForeground(TEXT_LIGHT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }
    
    public static void styleTable(javax.swing.JTable table) {
        table.setFont(REGULAR_FONT);
        table.setRowHeight(30);
        table.setShowGrid(true);
        table.setGridColor(new Color(189, 195, 199));
        
        // Style header
        table.getTableHeader().setFont(HEADER_FONT);
        table.getTableHeader().setBackground(SECONDARY);
        table.getTableHeader().setForeground(TEXT_LIGHT);
        table.getTableHeader().setOpaque(false);
    }
    
    public static void styleTextField(javax.swing.JTextField textField) {
        textField.setFont(REGULAR_FONT);
        textField.setPreferredSize(new java.awt.Dimension(200, 35));
        textField.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            textField.getBorder(),
            javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }
} 