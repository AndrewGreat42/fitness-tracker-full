package ru.fitness.client.view;

import ru.fitness.client.MainFrame;
import ru.fitness.client.api.ApiException;
import ru.fitness.common.dto.KbjuTargetsDto;
import ru.fitness.common.dto.UserDto;

import javax.swing.*;
import java.awt.*;

public class ProfilePanel extends BasePanel {

    private JPanel contentHolder;
    private UserDto user;
    private KbjuTargetsDto targets;

    public ProfilePanel(MainFrame frame) {
        super(frame);
        this.user = parentFrame.ctx().session.getUser();
        initialize();
        loadFresh();
    }

    @Override
    protected void initialize() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(createTitleLabel("Мой профиль"), BorderLayout.NORTH);

        contentHolder = new JPanel(new BorderLayout());
        contentHolder.setOpaque(false);
        contentHolder.add(new JLabel("Загрузка...", SwingConstants.CENTER), BorderLayout.CENTER);
        add(contentHolder, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttons.setOpaque(false);

        JButton editBtn = createStyledButton("✏️ Редактировать", PRIMARY_COLOR, 190, 45);
        editBtn.addActionListener(e -> onEdit());

        JButton backBtn = createStyledButton("← Назад", SECONDARY_COLOR, 190, 45);
        backBtn.addActionListener(e -> parentFrame.setPanel(new DashboardPanel(parentFrame)));

        buttons.add(editBtn);
        buttons.add(backBtn);
        add(buttons, BorderLayout.SOUTH);
    }

    private void loadFresh() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                user = parentFrame.ctx().profile.me();
                targets = parentFrame.ctx().nutrition.targets();
                parentFrame.ctx().session.set(parentFrame.ctx().session.getToken(), user);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    render();
                } catch (Exception ex) {
                    Throwable root = ex.getCause() != null ? ex.getCause() : ex;
                    if (root instanceof ApiException ae) {
                        showError(ae.getMessage());
                    } else {
                        showError("Ошибка: " + root.getMessage());
                    }
                }
            }
        };
        worker.execute();
    }

    private void render() {
        contentHolder.removeAll();

        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;


        JPanel avatar = new JPanel(new BorderLayout(10, 10));
        avatar.setBackground(new Color(50, 50, 55));
        avatar.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel icon = new JLabel("👤", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        icon.setForeground(ACCENT_COLOR);

        JPanel info = new JPanel(new GridLayout(2, 1, 2, 2));
        info.setOpaque(false);

        JLabel name = new JLabel(user.name, SwingConstants.CENTER);
        name.setFont(new Font("Segoe UI", Font.BOLD, 20));
        name.setForeground(TEXT_COLOR);

        JLabel email = new JLabel(user.email, SwingConstants.CENTER);
        email.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        email.setForeground(Color.GRAY);

        info.add(name);
        info.add(email);

        avatar.add(icon, BorderLayout.WEST);
        avatar.add(info, BorderLayout.CENTER);

        grid.add(avatar, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 0;

        grid.add(createCardPanel("Личная информация", buildInfoGrid()), gbc);

        gbc.gridx = 1;
        grid.add(createCardPanel("Цели", buildTargetsPanel()), gbc);

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);

        contentHolder.add(scroll, BorderLayout.CENTER);
        contentHolder.revalidate();
        contentHolder.repaint();
    }

    private JPanel buildInfoGrid() {
        JPanel p = new JPanel(new GridLayout(0, 2, 10, 10));
        p.setOpaque(false);

        addInfoRow(p, "Пол:", safe(user.gender));
        addInfoRow(p, "Возраст:", user.age + " лет");
        addInfoRow(p, "Вес:", String.format("%.1f кг", user.weight));
        addInfoRow(p, "Рост:", String.format("%.1f см", user.height));
        addInfoRow(p, "Активность:", activityText(user.activityLevel));
        addInfoRow(p, "Цель:", goalText(user.goal));

        double h = user.height / 100.0;
        double bmi = (h > 0) ? user.weight / (h * h) : 0;
        addInfoRow(p, "ИМТ:", String.format("%.1f (%s)", bmi, bmiCategory(bmi)));

        return p;
    }

    private JPanel buildTargetsPanel() {
        JPanel p = new JPanel(new GridLayout(0, 1, 10, 10));
        p.setOpaque(false);

        if (targets == null) {
            JLabel l = new JLabel("Нет данных", SwingConstants.CENTER);
            l.setForeground(TEXT_COLOR);
            p.add(l);
            return p;
        }

        p.add(goalItem("🔥", "Калории", targets.calories + " ккал/день"));
        p.add(goalItem("🥩", "Белки", String.format("%.0f г/день", targets.protein)));
        p.add(goalItem("🥑", "Жиры", String.format("%.0f г/день", targets.fat)));
        p.add(goalItem("🍚", "Углеводы", String.format("%.0f г/день", targets.carbs)));

        return p;
    }

    private JPanel goalItem(String emoji, String title, String value) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        JLabel e = new JLabel(emoji);
        e.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));

        JPanel text = new JPanel(new GridLayout(2, 1));
        text.setOpaque(false);

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 14));
        t.setForeground(ACCENT_COLOR);

        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        v.setForeground(Color.LIGHT_GRAY);

        text.add(t);
        text.add(v);

        row.add(e, BorderLayout.WEST);
        row.add(text, BorderLayout.CENTER);
        return row;
    }

    private void addInfoRow(JPanel panel, String label, String value) {
        JLabel l1 = new JLabel(label);
        l1.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l1.setForeground(Color.LIGHT_GRAY);

        JLabel l2 = new JLabel(value);
        l2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l2.setForeground(TEXT_COLOR);

        panel.add(l1);
        panel.add(l2);
    }

    private void onEdit() {
        if (user == null) return;

        EditProfileDialog dlg = new EditProfileDialog(parentFrame, user);
        dlg.setVisible(true);

        if (dlg.isSaved()) {
            loadFresh();
        }
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    private String activityText(double level) {
        if (level <= 1.2) return "Сидячий";
        if (level <= 1.375) return "Лёгкая";
        if (level <= 1.55) return "Умеренная";
        if (level <= 1.725) return "Высокая";
        return "Очень высокая";
    }

    private String goalText(String goal) {
        if (goal == null) return "";
        return switch (goal.toLowerCase()) {
            case "loss" -> "Похудение";
            case "maintenance" -> "Поддержание";
            case "gain" -> "Набор массы";
            default -> goal;
        };
    }

    private String bmiCategory(double bmi) {
        if (bmi <= 0) return "-";
        if (bmi < 18.5) return "Недостаток";
        if (bmi < 25) return "Норма";
        if (bmi < 30) return "Избыток";
        return "Ожирение";
    }
}
