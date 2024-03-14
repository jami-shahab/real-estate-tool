package main.ViewModule.Visualizations;

import main.ModelModule.DataConnector_Storage.RowOfHousingData;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.*;
import org.jfree.data.xy.XYDataset;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static main.ViewModule.Visualizations.ScatterPlot.getRegularTimePeriod;


public class LineChart {
    private final List<Map<String, List<RowOfHousingData>>> data;

    public LineChart(List<Map<String, List<RowOfHousingData>>> data) {
        this.data = data;
    }

    private RegularTimePeriod getTimePeriod(Date date, String timeInterval) {
        return getRegularTimePeriod(date, timeInterval);
    }
    private XYDataset createDataSet(List<Map<String, List<RowOfHousingData>>> data, String timeInterval, Map<String, List<Double>> forecastData) throws ParseException {
        TimeSeriesCollection dataset = new TimeSeriesCollection();

        for (Map<String, List<RowOfHousingData>> map : data) {
            for (Map.Entry<String, List<RowOfHousingData>> entry : map.entrySet()) {
                String region = entry.getKey();
                TimeSeries timeSeries = new TimeSeries(region);
                List<RowOfHousingData> rows = entry.getValue();
                for (RowOfHousingData row : rows) {
                    String dateString = row.getRefDate();
                    double priceIndex = (row.getValue() != null && !row.getValue().isEmpty()) ? Double.parseDouble(row.getValue()) : 0.0;
                    Date date = new SimpleDateFormat("MMM-yy", Locale.ENGLISH).parse(dateString);
                    RegularTimePeriod timePeriod = getTimePeriod(date, timeInterval);
                    timeSeries.addOrUpdate(timePeriod, priceIndex);
                }

                // add the forecast data to the dataset
                if (forecastData != null && forecastData.containsKey(region))  {
                    List<Double> forecastValues = forecastData.get(region);
                    RegularTimePeriod lastPeriod = timeSeries.getTimePeriod(timeSeries.getItemCount() - 1);
                    for (int i = 0; i < forecastValues.size(); i++) {
                        double forecastValue = forecastValues.get(i);
                        RegularTimePeriod forecastPeriod = lastPeriod.next();
                        timeSeries.addOrUpdate(forecastPeriod, forecastValue);
                        lastPeriod = forecastPeriod;
                    }
                }

                dataset.addSeries(timeSeries);
            }
        }

        return dataset;
    }

    public JFreeChart getLineChart(String timeInterval, Map<String, List<Double>> forecastData) throws ParseException {
        XYDataset dataset = createDataSet(data, timeInterval, forecastData);
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Price Index Values of Regions Over Time",
                "Date",
                "Price Index",
                dataset,
                true,
                false,
                false
        );

        // set the date axis format and range axis settings
        XYPlot plot = (XYPlot) chart.getPlot();
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("MMM-yy"));
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setAutoRangeIncludesZero(false);

        return chart;
    }
}
