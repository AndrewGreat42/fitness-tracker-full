package ru.fitness.client.view;

import javax.swing.*;
import java.awt.*;

public abstract class BasePanel extends JPanel {
    protected final ru.fitness.client.MainFrame parentFrame;

    protected static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    protected static final Color SECONDARY_COLOR = new Color(50, 50, 60);
    protected static final Color ACCENT_COLOR = new Color(100, 200, 255);
    protected static final Color TEXT_COLOR = Color.WHITE;
    protected static final Color PANEL_BG = new Color(40, 40, 45);

    public BasePanel(ru.fitness.client.MainFrame frame) {
        this.parentFrame = frame;
        setLayout(new BorderLayout());
        setBackground(PANEL_BG);
    }

    protected abstract void initialize();

    protected JButton createStyledButton(String text, Color bgColor, int width, int height) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(TEXT_COLOR);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(width, height));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        return button;
    }

    protected JPanel createCardPanel(String title, Component content) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(new Color(50, 50, 55));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 85), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        if (title != null) {
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            titleLabel.setForeground(ACCENT_COLOR);
            card.add(titleLabel, BorderLayout.NORTH);
        }

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    protected JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 28));
        label.setForeground(TEXT_COLOR);
        label.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        return label;
    }

    protected JTextField createStyledTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setBackground(new Color(60, 60, 65));
        field.setForeground(TEXT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 85), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setCaretColor(TEXT_COLOR);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return field;
    }

    protected JPasswordField createStyledPasswordField(int columns) {
        JPasswordField field = new JPasswordField(columns);
        field.setBackground(new Color(60, 60, 65));
        field.setForeground(TEXT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 85), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setCaretColor(TEXT_COLOR);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return field;
    }

    protected void showError(String message) {
        JOptionPane.showMessageDialog(parentFrame, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    protected void showInfo(String message) {
        JOptionPane.showMessageDialog(parentFrame, message, "Информация", JOptionPane.INFORMATION_MESSAGE);
    }
}
