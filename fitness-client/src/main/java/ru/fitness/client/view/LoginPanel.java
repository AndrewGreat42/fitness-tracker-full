package ru.fitness.client.view;

import ru.fitness.client.MainFrame;
import ru.fitness.client.api.ApiException;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends BasePanel {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginBtn;
    private JButton registerBtn;

    public LoginPanel(MainFrame frame) {
        super(frame);
        initialize();
    }

    @Override
    protected void initialize() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(createTitleLabel("Вход в GreatFitness"), gbc);

        gbc.gridy = 1;
        JLabel iconLabel = new JLabel("️", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        iconLabel.setForeground(ACCENT_COLOR);
        add(iconLabel, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.fill = GridBagConstraints.NONE;

        gbc.gridy = 2;
        gbc.gridx = 0;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(TEXT_COLOR);
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        add(emailLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        emailField = createStyledTextField(20);
        emailField.setPreferredSize(new Dimension(220, 34));
        add(emailField, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        JLabel passwordLabel = new JLabel("Пароль:");
        passwordLabel.setForeground(TEXT_COLOR);
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        passwordField = createStyledPasswordField(20);
        passwordField.setPreferredSize(new Dimension(220, 34));
        add(passwordField, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginBtn = createStyledButton("🚪 Войти", PRIMARY_COLOR, 320, 50);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginBtn.addActionListener(e -> onLogin());
        add(loginBtn, gbc);

        gbc.gridy = 5;
        registerBtn = createStyledButton("📝 Создать аккаунт", SECONDARY_COLOR, 320, 45);
        registerBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        registerBtn.addActionListener(e -> parentFrame.setPanel(new RegisterPanel(parentFrame)));
        add(registerBtn, gbc);

        gbc.gridy = 6;
        JLabel hint = new JLabel("Ещё нет аккаунта? Нажмите кнопку выше", SwingConstants.CENTER);
        hint.setForeground(Color.GRAY);
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        add(hint, gbc);


        passwordField.addActionListener(e -> onLogin());
    }

    private void setLoading(boolean loading) {
        loginBtn.setEnabled(!loading);
        registerBtn.setEnabled(!loading);
        emailField.setEnabled(!loading);
        passwordField.setEnabled(!loading);
        loginBtn.setText(loading ? "⏳ Входим..." : "🚪 Войти");
    }

    private void onLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Заполните все поля");
            return;
        }

        setLoading(true);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                var resp = parentFrame.ctx().auth.login(email, password);
                parentFrame.ctx().session.set(resp.token, resp.user);
                return null;
            }

            @Override
            protected void done() {
                setLoading(false);
                try {
                    get();
                    showInfo("Добро пожаловать, " + parentFrame.ctx().session.getUser().name + "! 🎉");
                    parentFrame.setPanel(new DashboardPanel(parentFrame));
                } catch (Exception ex) {
                    Throwable root = ex.getCause() != null ? ex.getCause() : ex;
                    if (root instanceof ApiException ae) {
                        showError(ae.getMessage());
                    } else {
                        showError("Ошибка входа: " + root.getMessage());
                    }
                }
            }
        };

        worker.execute();
    }
}
