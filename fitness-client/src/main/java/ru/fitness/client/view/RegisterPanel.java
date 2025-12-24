package ru.fitness.client.view;

import ru.fitness.client.MainFrame;
import ru.fitness.client.api.ApiException;
import ru.fitness.common.dto.AuthRegisterRequest;

import javax.swing.*;
import java.awt.*;

public class RegisterPanel extends BasePanel {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField nameField;
    private JComboBox<String> genderBox;
    private JTextField ageField;
    private JTextField weightField;
    private JTextField heightField;
    private JComboBox<Double> activityBox;
    private JComboBox<String> goalBox;

    private JButton registerBtn;
    private JButton backBtn;

    public RegisterPanel(MainFrame frame) {
        super(frame);
        initialize();
    }

    @Override
    protected void initialize() {
        setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        setLayout(new BorderLayout());

        add(createTitleLabel("Регистрация"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 0;

        emailField = createStyledTextField(20);
        passwordField = createStyledPasswordField(20);
        nameField = createStyledTextField(20);

        genderBox = new JComboBox<>(new String[]{"male", "female"});
        styleCombo(genderBox);

        ageField = createStyledTextField(8);
        weightField = createStyledTextField(8);
        heightField = createStyledTextField(8);

        activityBox = new JComboBox<>(new Double[]{1.2, 1.375, 1.55, 1.725, 1.9});
        styleCombo(activityBox);

        // храним value в combo (loss/maintenance/gain), а показываем красиво
        goalBox = new JComboBox<>(new String[]{"loss", "maintenance", "gain"});
        styleCombo(goalBox);

        addRow(form, gbc, "Email:", emailField);
        addRow(form, gbc, "Пароль:", passwordField);
        addRow(form, gbc, "Имя:", nameField);
        addRow(form, gbc, "Пол:", genderBox);
        addRow(form, gbc, "Возраст:", ageField);
        addRow(form, gbc, "Вес (кг):", weightField);
        addRow(form, gbc, "Рост (см):", heightField);
        addRow(form, gbc, "Активность:", activityBox);
        addRow(form, gbc, "Цель:", goalBox);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        center.add(form);
        add(center, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));

        registerBtn = createStyledButton("✅ Зарегистрироваться", PRIMARY_COLOR, 360, 48);
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerBtn.addActionListener(e -> onRegister());

        backBtn = createStyledButton("⬅️ Уже есть аккаунт? Войти", SECONDARY_COLOR, 360, 44);
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.addActionListener(e -> parentFrame.setPanel(new LoginPanel(parentFrame)));

        buttons.add(registerBtn);
        buttons.add(Box.createRigidArea(new Dimension(0, 10)));
        buttons.add(backBtn);

        add(buttons, BorderLayout.SOUTH);
    }

    private void styleCombo(JComboBox<?> box) {
        box.setBackground(new Color(60, 60, 65));
        box.setForeground(TEXT_COLOR);
        box.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        box.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 85), 1));
        box.setPreferredSize(new Dimension(240, 34));
    }

    private void addRow(JPanel p, GridBagConstraints gbc, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setForeground(TEXT_COLOR);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        p.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        field.setPreferredSize(new Dimension(260, 34));
        p.add(field, gbc);

        gbc.gridy++;
    }

    private void setLoading(boolean loading) {
        registerBtn.setEnabled(!loading);
        backBtn.setEnabled(!loading);
        registerBtn.setText(loading ? "⏳ Регистрируем..." : "✅ Зарегистрироваться");
    }

    private void onRegister() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String name = nameField.getText().trim();

        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            showError("Заполните Email, пароль и имя");
            return;
        }

        int age;
        double weight;
        double height;
        try {
            age = Integer.parseInt(ageField.getText().trim());
            weight = Double.parseDouble(weightField.getText().trim());
            height = Double.parseDouble(heightField.getText().trim());
        } catch (NumberFormatException ex) {
            showError("Некорректные числовые данные (возраст/вес/рост)");
            return;
        }

        AuthRegisterRequest req = new AuthRegisterRequest();
        req.email = email;
        req.password = password;
        req.name = name;
        req.gender = (String) genderBox.getSelectedItem();
        req.age = age;
        req.weight = weight;
        req.height = height;
        req.activityLevel = (Double) activityBox.getSelectedItem();
        req.goal = (String) goalBox.getSelectedItem();

        setLoading(true);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                var resp = parentFrame.ctx().auth.register(req);
                parentFrame.ctx().session.set(resp.token, resp.user);
                return null;
            }

            @Override
            protected void done() {
                setLoading(false);
                try {
                    get();
                    showInfo("✅ Регистрация успешна!\nДобро пожаловать, " + parentFrame.ctx().session.getUser().name + "!");
                    parentFrame.setPanel(new DashboardPanel(parentFrame));
                } catch (Exception ex) {
                    Throwable root = ex.getCause() != null ? ex.getCause() : ex;
                    if (root instanceof ApiException ae) {
                        showError(ae.getMessage());
                    } else {
                        showError("Ошибка регистрации: " + root.getMessage());
                    }
                }
            }
        };

        worker.execute();
    }
}
