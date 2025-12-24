package ru.fitness.client.view;

import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.markers.SeriesMarkers;
import ru.fitness.common.dto.WeightEntryDto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;


public final class WeightChartBuilder {

    private WeightChartBuilder() {}

    private static final DateTimeFormatter ISO_DT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static XYChart build(List<WeightEntryDto> entries) {
        XYChart chart = new XYChartBuilder()
                .width(800)
                .height(420)
                .title("Вес за период")
                .xAxisTitle("Дата")
                .yAxisTitle("кг")
                .build();

        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setPlotGridLinesVisible(true);
        chart.getStyler().setMarkerSize(6);
        chart.getStyler().setDatePattern("dd.MM"); // подписи оси X как даты

        if (entries == null || entries.isEmpty()) {
            chart.addSeries("Вес", Collections.singletonList(new Date()), Collections.singletonList(0.0));
            return chart;
        }


        entries.sort(Comparator.comparing(e -> safeParseDate(e.loggedAt)));

        List<Date> x = new ArrayList<>(entries.size());
        List<Double> y = new ArrayList<>(entries.size());

        for (WeightEntryDto e : entries) {
            Date d = safeParseDate(e.loggedAt);
            if (d == null) continue;

            x.add(d);
            y.add(e.weight);
        }

        if (x.isEmpty()) {
            chart.addSeries("Вес", Collections.singletonList(new Date()), Collections.singletonList(0.0));
            return chart;
        }

        var series = chart.addSeries("Вес", x, y);
        series.setMarker(SeriesMarkers.CIRCLE);

        return chart;
    }

    private static Date safeParseDate(String iso) {
        if (iso == null || iso.isBlank()) return null;
        try {
            LocalDateTime ldt = LocalDateTime.parse(iso, ISO_DT);
            return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
        } catch (Exception ex) {
            return null;
        }
    }
}
