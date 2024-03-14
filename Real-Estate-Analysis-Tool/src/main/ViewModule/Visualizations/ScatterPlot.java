package main.ViewModule.Visualizations;

import main.ModelModule.DataConnector_Storage.RowOfHousingData;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.Month;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Double.parseDouble;

public class ScatterPlot {

    private final List<Map<String, List<RowOfHousingData>>> data;
    private final Map<String, List<Double>> forecastResults;

    public ScatterPlot(List<Map<String, List<RowOfHousingData>>> data, Map<String, List<Double>> forecastResults) {
        this.data = data;
        this.forecastResults = forecastResults;
    }

    private ArrayList<XYSeries> generateData(String timeInterval, Map<String, List<Double>> forecastData) throws ParseException {
        HashMap<String, XYSeries> dataMap = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM-yy", Locale.ENGLISH);

        for (Map<String, List<RowOfHousingData>> map : data) {
            for (Map.Entry<String, List<RowOfHousingData>> entry : map.entrySet()) {
                String region = entry.getKey();
                List<RowOfHousingData> rows = entry.getValue();
                for (RowOfHousingData row : rows) {
                    String dateString = row.getRefDate();
                    double priceIndex = (row.getValue() != null && !row.getValue().isEmpty()) ? Double.parseDouble(row.getValue()) : 0.0;
                    Date date = dateFormat.parse(dateString);
                    RegularTimePeriod timePeriod = getTimePeriod(date, timeInterval);
                    if (!dataMap.containsKey(region)) {
                        dataMap.put(region, new XYSeries(region));
                    }
                    dataMap.get(region).add(timePeriod.getMiddleMillisecond(), priceIndex);
                }
            }
        }

        // Add the forecasted data to the dataMap
        if (forecastData != null){
            for (Map.Entry<String, List<Double>> entry : forecastData.entrySet()) {
                String region = entry.getKey();
                XYSeries series = dataMap.get(region);
                RegularTimePeriod nextTimePeriod = getTimePeriod(new Date((long) series.getMaxX()), timeInterval).next();
                for (double predictedValue : entry.getValue()) {
                    series.add(nextTimePeriod.getMiddleMillisecond(), predictedValue);
                    nextTimePeriod = nextTimePeriod.next();
                }
            }
        }
        return new ArrayList<>(dataMap.values());
    }

    private Date getMaxDate(List<RowOfHousingData> rows) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM-yy", Locale.ENGLISH);
        Date maxDate = null;

        for (RowOfHousingData row : rows) {
            String dateString = row.getRefDate();
            Date date = dateFormat.parse(dateString);

            if (maxDate == null || date.after(maxDate)) {
                maxDate = date;
            }
        }

        return maxDate;
    }

    private XYDataset createDataSet(ArrayList<XYSeries> xySeriesArrayList) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        for (XYSeries xySeries : xySeriesArrayList) {
            dataset.addSeries(xySeries);
        }
        return dataset;
    }

    public JFreeChart getScatterPlot(String timeInterval) throws ParseException {
        ArrayList<XYSeries> data = generateData(timeInterval,forecastResults);
        JFreeChart chart = ChartFactory.createScatterPlot(
                "Price Index Values of Regions Over Time",
                "Date",
                "Price Index",
                createDataSet(data),
                PlotOrientation.VERTICAL,
                true,
                false,
                false
        );

        XYPlot plot = (XYPlot) chart.getPlot();
        DateAxis axis = new DateAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("MMM-yy",Locale.ENGLISH));
        plot.setDomainAxis(axis);

        return chart;
    }

    private RegularTimePeriod getTimePeriod(Date date, String timeInterval) {
        return getRegularTimePeriod(date, timeInterval);
    }

    static RegularTimePeriod getRegularTimePeriod(Date date, String timeInterval) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;

        switch (timeInterval) {
            case "monthly" -> {
                return new Month(date);
            }
            case "every 6 months" -> {
                int halfYear = (month <= 6) ? 1 : 2;
                int monthOfYear = ((halfYear - 1) * 6) + ((month <= 6) ? month : month - 6);
                return new Month(monthOfYear, year);
            }
            case "yearly" -> {
                return new Month(1, year);
            }
            default -> throw new IllegalArgumentException("Invalid time interval: " + timeInterval);
        }
    }
}
