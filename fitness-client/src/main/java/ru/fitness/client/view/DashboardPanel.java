package ru.fitness.client.view;

import ru.fitness.client.MainFrame;
import ru.fitness.client.api.ApiException;
import ru.fitness.common.dto.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DashboardPanel extends BasePanel {

    private JLabel welcomeLabel;

    private JLabel targetsLabel;
    private JLabel consumedLabel;
    private JProgressBar caloriesBar;

    private JTextArea recentMealsArea;
    private JPanel chartContainer;

    private JButton refreshBtn;
    private JButton addMealBtn;
    private JButton updateWeightBtn;
    private JButton profileBtn;
    private JButton logoutBtn;

    private KbjuTargetsDto targets;
    private MealSummaryDto today;

    public DashboardPanel(MainFrame frame) {
        super(frame);
        initialize();
        loadAll();
    }

    @Override
    protected void initialize() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));


        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        UserDto u = parentFrame.ctx().session.getUser();
        welcomeLabel = new JLabel("Привет, " + (u != null ? u.name : "User") + " 👋");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcomeLabel.setForeground(TEXT_COLOR);
        top.add(welcomeLabel, BorderLayout.WEST);

        JPanel topBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        topBtns.setOpaque(false);

        refreshBtn = createStyledButton("🔄 Обновить", SECONDARY_COLOR, 150, 40);
        refreshBtn.addActionListener(e -> loadAll());

        profileBtn = createStyledButton("👤 Профиль", SECONDARY_COLOR, 140, 40);
        profileBtn.addActionListener(e -> parentFrame.setPanel(new ProfilePanel(parentFrame)));

        logoutBtn = createStyledButton("🚪 Выход", new Color(150, 60, 60), 130, 40);
        logoutBtn.addActionListener(e -> {
            parentFrame.ctx().session.clear();
            parentFrame.setPanel(new LoginPanel(parentFrame));
        });

        topBtns.add(refreshBtn);
        topBtns.add(profileBtn);
        topBtns.add(logoutBtn);
        top.add(topBtns, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);


        JPanel grid = new JPanel(new GridLayout(1, 2, 15, 15));
        grid.setOpaque(false);


        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));


        JPanel nutritionContent = new JPanel();
        nutritionContent.setOpaque(false);
        nutritionContent.setLayout(new BoxLayout(nutritionContent, BoxLayout.Y_AXIS));

        targetsLabel = new JLabel("Цель: ...");
        targetsLabel.setForeground(TEXT_COLOR);
        targetsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        consumedLabel = new JLabel("Съедено сегодня: ...");
        consumedLabel.setForeground(TEXT_COLOR);
        consumedLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        caloriesBar = new JProgressBar();
        caloriesBar.setStringPainted(true);
        caloriesBar.setForeground(ACCENT_COLOR);
        caloriesBar.setBackground(new Color(70, 70, 75));
        caloriesBar.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        nutritionContent.add(targetsLabel);
        nutritionContent.add(Box.createRigidArea(new Dimension(0, 6)));
        nutritionContent.add(consumedLabel);
        nutritionContent.add(Box.createRigidArea(new Dimension(0, 10)));
        nutritionContent.add(caloriesBar);

        left.add(createCardPanel("КБЖУ", nutritionContent));
        left.add(Box.createRigidArea(new Dimension(0, 15)));


        JPanel actions = new JPanel(new GridLayout(3, 1, 10, 10));
        actions.setOpaque(false);
        addMealBtn = createStyledButton("➕ Добавить приём пищи", PRIMARY_COLOR, 320, 42);
        addMealBtn.addActionListener(e -> showAddMealDialog());

        updateWeightBtn = createStyledButton("⚖️ Обновить вес", PRIMARY_COLOR.darker(), 320, 42);
        updateWeightBtn.addActionListener(e -> showUpdateWeightDialog());

        JButton statsBtn = createStyledButton("📈 Вес за 7 дней", SECONDARY_COLOR, 320, 42);
        statsBtn.addActionListener(e -> loadWeightChart());

        actions.add(addMealBtn);
        actions.add(updateWeightBtn);
        actions.add(statsBtn);

        left.add(createCardPanel("Действия", actions));
        left.add(Box.createRigidArea(new Dimension(0, 15)));


        recentMealsArea = new JTextArea(8, 28);
        recentMealsArea.setEditable(false);
        recentMealsArea.setOpaque(false);
        recentMealsArea.setForeground(TEXT_COLOR);
        recentMealsArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        recentMealsArea.setText("Загрузка...");

        JScrollPane scroll = new JScrollPane(recentMealsArea);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        left.add(createCardPanel("Последние приёмы пищи", scroll));


        JPanel right = new JPanel(new BorderLayout());
        right.setOpaque(false);

        chartContainer = new JPanel(new BorderLayout());
        chartContainer.setOpaque(false);
        chartContainer.add(new JLabel("Нажмите 'Вес за 7 дней'", SwingConstants.CENTER), BorderLayout.CENTER);

        right.add(createCardPanel("График", chartContainer), BorderLayout.CENTER);

        grid.add(left);
        grid.add(right);

        add(grid, BorderLayout.CENTER);
    }

    private void setLoading(boolean loading) {
        refreshBtn.setEnabled(!loading);
        addMealBtn.setEnabled(!loading);
        updateWeightBtn.setEnabled(!loading);
        profileBtn.setEnabled(!loading);
        logoutBtn.setEnabled(!loading);
        refreshBtn.setText(loading ? "⏳ ..." : "🔄 Обновить");
    }

    private void loadAll() {
        setLoading(true);
        recentMealsArea.setText("Загрузка...\n");

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private List<MealDto> recent;

            @Override
            protected Void doInBackground() {
                targets = parentFrame.ctx().nutrition.targets();
                today = parentFrame.ctx().meals.todaySummary();
                recent = parentFrame.ctx().meals.recent(8);
                // обновим user в сессии (на случай изменения веса/профиля)
                var me = parentFrame.ctx().profile.me();
                parentFrame.ctx().session.set(parentFrame.ctx().session.getToken(), me);
                return null;
            }

            @Override
            protected void done() {
                setLoading(false);
                try {
                    get();
                    updateNutritionUI();
                    updateRecentMealsUI(recent);
                } catch (Exception ex) {
                    Throwable root = ex.getCause() != null ? ex.getCause() : ex;
                    if (root instanceof ApiException ae) {
                        if (ae.getStatus() == 401) {
                            showError("Сессия истекла. Войдите снова.");
                            parentFrame.ctx().session.clear();
                            parentFrame.setPanel(new LoginPanel(parentFrame));
                            return;
                        }
                        showError(ae.getMessage());
                    } else {
                        showError("Ошибка загрузки данных: " + root.getMessage());
                    }
                }
            }
        };

        worker.execute();
    }

    private void updateNutritionUI() {
        if (targets == null || today == null) {
            targetsLabel.setText("Цель: -");
            consumedLabel.setText("Съедено сегодня: -");
            caloriesBar.setValue(0);
            caloriesBar.setString("-");
            return;
        }

        targetsLabel.setText(String.format("Цель: %d ккал | Б %.1f | Ж %.1f | У %.1f",
                targets.calories, targets.protein, targets.fat, targets.carbs));

        consumedLabel.setText(String.format("Съедено сегодня: %d ккал | Б %.1f | Ж %.1f | У %.1f",
                today.calories, today.protein, today.fat, today.carbs));

        int max = Math.max(1, targets.calories);
        caloriesBar.setMaximum(max);
        caloriesBar.setValue(Math.min(today.calories, max));
        caloriesBar.setString(today.calories + " / " + max + " ккал");
    }

    private void updateRecentMealsUI(List<MealDto> recent) {
        StringBuilder sb = new StringBuilder();
        if (recent == null || recent.isEmpty()) {
            sb.append("Пока нет приёмов пищи.\nНажмите 'Добавить приём пищи'.");
        } else {
            for (MealDto m : recent) {
                sb.append(String.format("• %-18s %4dkcal  Б%.1f Ж%.1f У%.1f\n",
                        trim(m.description, 18), m.calories, m.protein, m.fat, m.carbs));
            }
        }
        recentMealsArea.setText(sb.toString());
    }

    private String trim(String s, int max) {
        if (s == null) return "";
        String t = s.trim();
        return t.length() <= max ? t : t.substring(0, max - 1) + "…";
    }

    private void showAddMealDialog() {
        JTextField desc = createStyledTextField(16);
        JTextField cal = createStyledTextField(8);
        JTextField p = createStyledTextField(8);
        JTextField f = createStyledTextField(8);
        JTextField c = createStyledTextField(8);

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.setBackground(PANEL_BG);
        panel.add(new JLabel("Описание:")); panel.add(desc);
        panel.add(new JLabel("Калории:")); panel.add(cal);
        panel.add(new JLabel("Белки:")); panel.add(p);
        panel.add(new JLabel("Жиры:")); panel.add(f);
        panel.add(new JLabel("Углеводы:")); panel.add(c);

        for (Component comp : panel.getComponents()) {
            if (comp instanceof JLabel l) {
                l.setForeground(TEXT_COLOR);
                l.setFont(new Font("Segoe UI", Font.BOLD, 13));
            }
        }

        int res = JOptionPane.showConfirmDialog(parentFrame, panel, "Добавить приём пищи",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (res != JOptionPane.OK_OPTION) return;

        MealCreateRequest req = new MealCreateRequest();
        req.description = desc.getText().trim();

        try {
            req.calories = Integer.parseInt(cal.getText().trim());
            req.protein = Double.parseDouble(p.getText().trim());
            req.fat = Double.parseDouble(f.getText().trim());
            req.carbs = Double.parseDouble(c.getText().trim());
        } catch (NumberFormatException ex) {
            showError("Некорректные числа в полях БЖУ/ккал");
            return;
        }

        setLoading(true);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                parentFrame.ctx().meals.add(req);
                return null;
            }

            @Override
            protected void done() {
                setLoading(false);
                try {
                    get();
                    loadAll();
                    showInfo("✅ Приём пищи добавлен!");
                } catch (Exception ex) {
                    Throwable root = ex.getCause() != null ? ex.getCause() : ex;
                    if (root instanceof ApiException ae) showError(ae.getMessage());
                    else showError("Ошибка: " + root.getMessage());
                }
            }
        };

        worker.execute();
    }

    private void showUpdateWeightDialog() {
        String value = JOptionPane.showInputDialog(parentFrame, "Введите новый вес (кг):", "Обновить вес",
                JOptionPane.PLAIN_MESSAGE);
        if (value == null) return;

        double w;
        try {
            w = Double.parseDouble(value.trim());
            if (w <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            showError("Некорректный вес");
            return;
        }

        setLoading(true);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                parentFrame.ctx().weights.add(w);
                return null;
            }

            @Override
            protected void done() {
                setLoading(false);
                try {
                    get();
                    loadAll();
                    showInfo("✅ Вес обновлён");
                } catch (Exception ex) {
                    Throwable root = ex.getCause() != null ? ex.getCause() : ex;
                    if (root instanceof ApiException ae) showError(ae.getMessage());
                    else showError("Ошибка: " + root.getMessage());
                }
            }
        };

        worker.execute();
    }

    private void loadWeightChart() {
        setLoading(true);
        chartContainer.removeAll();
        chartContainer.add(new JLabel("Загрузка графика...", SwingConstants.CENTER), BorderLayout.CENTER);
        chartContainer.revalidate();
        chartContainer.repaint();

        SwingWorker<org.knowm.xchart.XYChart, Void> worker = new SwingWorker<>() {
            private boolean noData = false;

            @Override
            protected org.knowm.xchart.XYChart doInBackground() {
                List<WeightEntryDto> list = parentFrame.ctx().weights.history(7);
                if (list == null || list.isEmpty()) {
                    noData = true;
                    return null;
                }
                // Build chart OFF the EDT to avoid UI freezes on large datasets.
                return WeightChartBuilder.build(list);
            }

            @Override
            protected void done() {
                setLoading(false);
                chartContainer.removeAll();
                try {
                    if (noData) {
                        chartContainer.add(new JLabel("Нет данных по весу", SwingConstants.CENTER), BorderLayout.CENTER);
                    } else {
                        org.knowm.xchart.XYChart chart = get();
                        if (chart == null) {
                            chartContainer.add(new JLabel("Нет данных по весу", SwingConstants.CENTER), BorderLayout.CENTER);
                        } else {
                            chartContainer.add(new WeightChartPanel(chart), BorderLayout.CENTER);
                        }
                    }
                } catch (Exception ex) {
                    Throwable root = ex.getCause() != null ? ex.getCause() : ex;
                    chartContainer.add(new JLabel("Ошибка загрузки графика", SwingConstants.CENTER), BorderLayout.CENTER);
                    if (root instanceof ApiException ae) showError(ae.getMessage());
                    else showError("Ошибка: " + root.getMessage());
                } finally {
                    chartContainer.revalidate();
                    chartContainer.repaint();
                }
            }
        };

        worker.execute();
    }
}
