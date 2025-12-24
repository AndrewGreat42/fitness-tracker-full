package ru.fitness.client;

import com.formdev.flatlaf.FlatDarkLaf;
import ru.fitness.client.view.LoginPanel;

import javax.swing.*;

public class MainApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatDarkLaf.setup();
            try {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } catch (Exception ignored) {
            }

            AppContext ctx = new AppContext(new AppConfig());
            MainFrame frame = new MainFrame(ctx);
            frame.setPanel(new LoginPanel(frame));
            frame.setVisible(true);
        });
    }
}
