package ru.fitness.client.view;

import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import ru.fitness.common.dto.WeightEntryDto;

import javax.swing.*;
import java.awt.*;
import java.util.List;


public class WeightChartPanel extends JPanel {

    private final XChartPanel<XYChart> chartPanel;

    public WeightChartPanel(XYChart chart) {
        setLayout(new BorderLayout());
        setBackground(new Color(50, 50, 55));

        this.chartPanel = new XChartPanel<>(chart);
        this.chartPanel.setBackground(new Color(50, 50, 55));
        add(chartPanel, BorderLayout.CENTER);
    }

    /**
     * Backward-compatible constructor.
     * Note: builds the chart on the calling thread. Prefer building off-EDT with WeightChartBuilder.
     */
    public WeightChartPanel(List<WeightEntryDto> entries) {
        this(WeightChartBuilder.build(entries));
    }
}
