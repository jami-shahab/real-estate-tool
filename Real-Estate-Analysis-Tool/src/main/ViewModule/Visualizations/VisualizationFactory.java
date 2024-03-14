package main.ViewModule.Visualizations;

import main.ModelModule.DataConnector_Storage.RowOfHousingData;
import org.jfree.chart.ChartPanel;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VisualizationFactory {
    private List<Map<String, List<RowOfHousingData>>> data;
    private Map<String, List<Double>> forecastData;
    private boolean isTableDisplayed;

    public VisualizationFactory(List<Map<String, List<RowOfHousingData>>> data,Map<String, List<Double>> forecastData) {
        this.data = new ArrayList<>(data);
        this.forecastData = forecastData;
        this.isTableDisplayed = false;
    }


    public JComponent createVisualization(String visualizationType, String timeInterval,Map<String, List<Double>> forecastData) throws ParseException {
        switch (visualizationType) {
            case "Bar Chart" -> {
                BarChart barChart = new BarChart(data,forecastData);
                ChartPanel chartPanel = new ChartPanel(barChart.getBarChart(timeInterval));
                setChartPanelProperties(chartPanel);
                return chartPanel;
            }
            case "Line Chart" -> {
                LineChart lineChart = new LineChart(data);
                ChartPanel chartPanel = new ChartPanel(lineChart.getLineChart(timeInterval,forecastData));
                setChartPanelProperties(chartPanel);
                return chartPanel;
            }
            case "Pie Chart" -> {
                PieChart pieChart = new PieChart(data);
                ChartPanel chartPanel = new ChartPanel(pieChart.getPieChart());
                setChartPanelProperties(chartPanel);
                return chartPanel;
            }
            case "Scatter Chart" -> {
                ScatterPlot scatterPlot = new ScatterPlot(data,forecastData);
                ChartPanel chartPanel = new ChartPanel(scatterPlot.getScatterPlot(timeInterval));
                setChartPanelProperties(chartPanel);
                return chartPanel;
            }
            case "Table" -> {
                Table table = Table.generateTableData(data);
                JScrollPane scrollPane = new JScrollPane(table.createJTable());
                setScrollPaneProperties(scrollPane);
                return scrollPane;
            }
            case "Report" -> {
                Report r = new Report(data);
                return r.createReport();
            }
            default -> throw new IllegalArgumentException("Invalid visualization type: " + visualizationType);
        }
    }



    private void setChartPanelProperties(ChartPanel chartPanel) {
        chartPanel.setPreferredSize(new Dimension(400, 300));
        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    }

    private void setScrollPaneProperties(JScrollPane scrollPane) {
        scrollPane.setPreferredSize(new Dimension(400, 300));
        scrollPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    }
}
