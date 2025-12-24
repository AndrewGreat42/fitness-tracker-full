package ru.fitness.client.view;

import ru.fitness.client.MainFrame;
import ru.fitness.client.api.ApiException;
import ru.fitness.common.dto.UserDto;
import ru.fitness.common.dto.UserUpdateRequest;

import javax.swing.*;
import java.awt.*;

public class EditProfileDialog extends JDialog {

    private final MainFrame frame;
    private final UserDto user;
    private boolean saved = false;

    private JTextField nameField;
    private JTextField emailField;
    private JComboBox<String> genderCombo;
    private JSpinner ageSpinner;
    private JSpinner weightSpinner;
    private JSpinner heightSpinner;
    private JComboBox<Double> activityCombo;
    private JComboBox<String> goalCombo;

    private JButton saveButton;
    private JButton cancelButton;

    public EditProfileDialog(MainFrame parent, UserDto user) {
        super(parent, "Редактирование профиля", true);
        this.frame = parent;
        this.user = user;
        buildUI();
    }

    public boolean isSaved() {
        return saved;
    }

    private void buildUI() {
        setSize(520, 610);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        JPanel main = new JPanel(new GridBagLayout());
        main.setBackground(new Color(40, 40, 45));
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        JLabel title = new JLabel("✏️ Редактирование профиля");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        main.add(title, gbc);

        gbc.gridwidth = 1;

        nameField = textField(user.name);
        emailField = textField(user.email);

        genderCombo = combo(new String[]{"male", "female"});
        genderCombo.setSelectedItem(user.gender);

        ageSpinner = spinnerInt(user.age, 10, 100, 1);
        weightSpinner = spinnerDouble(user.weight, 30, 300, 0.1);
        heightSpinner = spinnerDouble(user.height, 100, 250, 1);

        activityCombo = combo(new Double[]{1.2, 1.375, 1.55, 1.725, 1.9});
        activityCombo.setSelectedItem(user.activityLevel);

        goalCombo = combo(new String[]{"loss", "maintenance", "gain"});
        goalCombo.setSelectedItem(user.goal);

        gbc.gridy++;
        addRow(main, gbc, "Имя:", nameField);
        gbc.gridy++;
        addRow(main, gbc, "Email:", emailField);
        gbc.gridy++;
        addRow(main, gbc, "Пол:", genderCombo);
        gbc.gridy++;
        addRow(main, gbc, "Возраст:", ageSpinner);
        gbc.gridy++;
        addRow(main, gbc, "Вес (кг):", weightSpinner);
        gbc.gridy++;
        addRow(main, gbc, "Рост (см):", heightSpinner);
        gbc.gridy++;
        addRow(main, gbc, "Активность:", activityCombo);
        gbc.gridy++;
        addRow(main, gbc, "Цель:", goalCombo);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttons.setBackground(new Color(40, 40, 45));

        saveButton = button("💾 Сохранить", new Color(70, 130, 180));
        saveButton.addActionListener(e -> onSave());

        cancelButton = button("❌ Отмена", new Color(100, 100, 100));
        cancelButton.addActionListener(e -> dispose());

        buttons.add(saveButton);
        buttons.add(cancelButton);

        add(main, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, String label, JComponent field) {
        gbc.gridx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        field.setPreferredSize(new Dimension(260, 34));
        panel.add(field, gbc);
    }

    private JTextField textField(String text) {
        JTextField field = new JTextField(text == null ? "" : text, 20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(new Color(60, 60, 65));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 85), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    private <T> JComboBox<T> combo(T[] items) {
        JComboBox<T> box = new JComboBox<>(items);
        box.setBackground(new Color(60, 60, 65));
        box.setForeground(Color.WHITE);
        box.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        box.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 85), 1));
        return box;
    }

    private JSpinner spinnerInt(int value, int min, int max, int step) {
        SpinnerNumberModel m = new SpinnerNumberModel(value, min, max, step);
        JSpinner s = new JSpinner(m);
        styleSpinner(s);
        return s;
    }

    private JSpinner spinnerDouble(double value, double min, double max, double step) {
        SpinnerNumberModel m = new SpinnerNumberModel(value, min, max, step);
        JSpinner s = new JSpinner(m);
        styleSpinner(s);
        return s;
    }

    private void styleSpinner(JSpinner spinner) {
        JSpinner.DefaultEditor ed = (JSpinner.DefaultEditor) spinner.getEditor();
        ed.getTextField().setBackground(new Color(60, 60, 65));
        ed.getTextField().setForeground(Color.WHITE);
        ed.getTextField().setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ed.getTextField().setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        spinner.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 85), 1));
    }

    private JButton button(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setPreferredSize(new Dimension(170, 42));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void setLoading(boolean loading) {
        saveButton.setEnabled(!loading);
        cancelButton.setEnabled(!loading);
        saveButton.setText(loading ? "⏳ Сохраняем..." : "💾 Сохранить");
    }

    private void onSave() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();

        if (name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Имя и Email не могут быть пустыми", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Введите корректный Email", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        UserUpdateRequest req = new UserUpdateRequest();
        req.name = name;
        req.email = email;
        req.gender = (String) genderCombo.getSelectedItem();
        req.age = ((Number) ageSpinner.getValue()).intValue();
        req.weight = ((Number) weightSpinner.getValue()).doubleValue();
        req.height = ((Number) heightSpinner.getValue()).doubleValue();
        req.activityLevel = (Double) activityCombo.getSelectedItem();
        req.goal = (String) goalCombo.getSelectedItem();

        setLoading(true);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                UserDto updated = frame.ctx().profile.updateMe(req);
                frame.ctx().session.set(frame.ctx().session.getToken(), updated);
                return null;
            }

            @Override
            protected void done() {
                setLoading(false);
                try {
                    get();
                    saved = true;
                    JOptionPane.showMessageDialog(EditProfileDialog.this, "✅ Профиль обновлён", "Успех", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } catch (Exception ex) {
                    Throwable root = ex.getCause() != null ? ex.getCause() : ex;
                    if (root instanceof ApiException ae) {
                        JOptionPane.showMessageDialog(EditProfileDialog.this, ae.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(EditProfileDialog.this, "Ошибка: " + root.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        };

        worker.execute();
    }
}
