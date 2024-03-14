package main.ViewModule.Visualizations;

import main.Controller.TimeSeries;
import main.ModelModule.DataConnector_Storage.DbInterface;
import main.ModelModule.DataConnector_Storage.RowOfHousingData;
import main.ModelModule.DataConnector_Storage.SqlAccessProxy;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Report {

    private List<Map<String, List<RowOfHousingData>>> data;

    public Report(List<Map<String, List<RowOfHousingData>>> data) {
        this.data = data;
    }


    private String generateReport(List<Map<String, List<RowOfHousingData>>> dataList) {
        StringBuilder report = new StringBuilder();
        report.append("Report of the Time Series Loaded\n");
        report.append("\n----------------------------------\n");

        // Iterate through each data map in the list
        for (Map<String, List<RowOfHousingData>> dataMap : dataList) {
            // Iterate through each region
            for (Map.Entry<String, List<RowOfHousingData>> entry : dataMap.entrySet()) {
                String region = entry.getKey();
                List<RowOfHousingData> housingData = entry.getValue();

                // Initialize values
                double minNHPI = Double.MAX_VALUE;
                double maxNHPI = Double.MIN_VALUE;
                double sumNHPI = 0;
                String minDate = "", maxDate = "", avgDate = "";

                // Iterate through the data and calculate min, max, and sum
                for (RowOfHousingData rowData : housingData) {
                    double value = rowData.getValue().isEmpty() ? 0.0 : Double.parseDouble(rowData.getValue());
                    String date = rowData.getRefDate();
                    sumNHPI += value;
                    System.out.print(value +",");
                    if (value > 0 && value < minNHPI) {
                        minNHPI = value;
                        minDate = date;
                    }

                    if (value >= maxNHPI) {
                        maxNHPI = value;
                        maxDate = date;
                    }
                }

                System.out.println();
                System.out.println("MinVal: " + minNHPI);
                System.out.println("MaxCal: " + maxNHPI);

                // Calculate the average NHPI
                double avgNHPI = sumNHPI / housingData.size();

                // Format the region's data
                DecimalFormat df = new DecimalFormat("#.##");
                report.append("Region: ").append(region).append("\n")
                        .append("Average NHPI: ").append(df.format(avgNHPI)).append("\n")
                        .append("Min NHPI: ").append(df.format(minNHPI)).append(" on ").append(minDate).append("\n")
                        .append("Max NHPI: ").append(df.format(maxNHPI)).append(" on ").append(maxDate).append("\n")
                        .append("-----------------------\n");
            }
        }

        return report.toString();
    }

    public JComponent createReport() {
        JTextArea report = new JTextArea();
        report.setEditable(false);
        report.setPreferredSize(new Dimension(400, 300));
        report.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        report.setBackground(Color.white);
        String reportMessage, reportMessage2;

        reportMessage = generateReport(data);

        report.setText(reportMessage);
        return new JScrollPane(report);
    }

//    public static void main(String[] args) throws Exception {
//
//
//        DbInterface db = new SqlAccessProxy();
//        Map<String, List<RowOfHousingData>> data = db.readDataBase(new TimeSeries("Ottawa-Gatineau, Quebec part, Ontario/Quebec", "Jan-81", "Jan-22"));
//        List<Map<String, List<RowOfHousingData>>> dataList = new ArrayList<>();
//
//        dataList.add(data);
//        generateReport(dataList);
//
//    }


}
