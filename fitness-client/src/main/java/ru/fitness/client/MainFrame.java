package ru.fitness.client;

import javax.swing.*;

public class MainFrame extends JFrame {

    private final AppContext ctx;

    public MainFrame(AppContext ctx) {
        super("GreatFitness — Fitness Tracker");
        this.ctx = ctx;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);
    }

    public AppContext ctx() {
        return ctx;
    }

    public void setPanel(JPanel panel) {
        setContentPane(panel);
        revalidate();
        repaint();
    }
}
