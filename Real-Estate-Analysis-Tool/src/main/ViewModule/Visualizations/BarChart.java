package main.ViewModule.Visualizations;

import main.ModelModule.DataConnector_Storage.RowOfHousingData;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class BarChart {
    private final List<Map<String, List<RowOfHousingData>>> data;
    private final Map<String, List<Double>> forecastData;

    public BarChart(List<Map<String, List<RowOfHousingData>>> data,Map<String, List<Double>> forecastData ) {
        this.data = data;
        this.forecastData = forecastData;
    }

    private String getCategoryLabel(Date date, String timeInterval) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;

        switch (timeInterval) {
            case "monthly" -> {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM-yy", Locale.ENGLISH);
                return dateFormat.format(date);
            }
            case "every 6 months" -> {
                int halfYear = (month <= 6) ? 1 : 2;
                return String.format("%d-H%d", year, halfYear);
            }
            case "yearly" -> {
                return Integer.toString(year);
            }
            default -> throw new IllegalArgumentException("Invalid time interval: " + timeInterval);
        }
    }

    private CategoryDataset createDataSet(List<Map<String, List<RowOfHousingData>>> data, Map<String, List<Double>> forecastData, String timeInterval) throws ParseException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map<String, List<RowOfHousingData>> map : data) {
            for (Map.Entry<String, List<RowOfHousingData>> entry : map.entrySet()) {
                String region = entry.getKey();
                List<RowOfHousingData> rows = entry.getValue();
                for (RowOfHousingData row : rows) {
                    String dateString = row.getRefDate();
                    double priceIndex = (row.getValue() != null && !row.getValue().isEmpty()) ? Double.parseDouble(row.getValue()) : 0.0;
                    Date date = new SimpleDateFormat("MMM-yy", Locale.ENGLISH).parse(dateString);
                    String category = getCategoryLabel(date, timeInterval);
                    dataset.addValue(priceIndex, region, category);
                }

                if (forecastData != null && forecastData.containsKey(region)) {
                    List<Double> forecastValues = forecastData.get(region);
                    int index = 0;
                    for (Double forecastValue : forecastValues) {
                        String category = String.format("%s_Forecast%d", region, index++);
                        dataset.addValue(forecastValue, region, category);
                    }
                }
            }
        }

        return dataset;
    }
    public JFreeChart getBarChart(String timeInterval) throws ParseException {
        CategoryDataset dataset = createDataSet(data,forecastData, timeInterval);
        JFreeChart chart = ChartFactory.createBarChart(
                "Price Index Values of Regions Over Time",
                "Date",
                "Price Index",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                false,
                false
        );

        if (timeInterval.equals("yearly")) {
            CategoryPlot plot = (CategoryPlot) chart.getPlot();
            CategoryAxis axis = plot.getDomainAxis();
            axis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 10));
            axis.setTickLabelsVisible(true);
        } else if (timeInterval.equals("every 6 months")) {
            CategoryPlot plot = (CategoryPlot) chart.getPlot();
            CategoryAxis axis = plot.getDomainAxis();
            axis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 10));
            axis.setTickLabelsVisible(true);
        }

        return chart;
    }
}
