package main.ViewModule.Visualizations;

import main.ModelModule.DataConnector_Storage.RowOfHousingData;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import java.util.List;
import java.util.Map;

public class PieChart {

    private final List<Map<String, List<RowOfHousingData>>> data;

    public PieChart(List<Map<String, List<RowOfHousingData>>> data) {
        this.data = data;
    }

    private DefaultPieDataset createDataSet() {
        DefaultPieDataset dataset = new DefaultPieDataset();

        for (Map<String, List<RowOfHousingData>> map : data) {
            for (Map.Entry<String, List<RowOfHousingData>> entry : map.entrySet()) {
                String region = entry.getKey();
                double sum = 0;
                int count = 0;
                for (RowOfHousingData row : entry.getValue()) {
                    double priceIndex = (row.getValue() != null && !row.getValue().isEmpty()) ? Double.parseDouble(row.getValue()) : 0.0;
                    sum += priceIndex;
                    count++;
                }
                if (count > 0) {
                    double mean = sum / count;
                    dataset.setValue(region, mean);
                }
            }
        }

        return dataset;
    }

    public JFreeChart getPieChart() {
        DefaultPieDataset dataset = createDataSet();
        JFreeChart chart = ChartFactory.createPieChart(
                "Mean Price Index Values by Region",
                dataset,
                true,
                true,
                false
        );
        return chart;
    }

}
