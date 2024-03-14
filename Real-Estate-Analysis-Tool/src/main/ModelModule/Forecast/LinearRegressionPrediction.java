package main.ModelModule.Forecast;

import main.ModelModule.DataConnector_Storage.RowOfHousingData;
import weka.classifiers.evaluation.NumericPrediction;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.timeseries.WekaForecaster;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.supervised.attribute.TSLagMaker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinearRegressionPrediction {
    public static Map<String, List<Double>> performTimeSeriesForecast(List<Map<String, List<RowOfHousingData>>> inputData,
                                                                      int numberOfMonths) throws Exception {

        Map<String, List<Double>> forecastResults = new HashMap<>();

        for (Map<String, List<RowOfHousingData>> regionData : inputData) {
            for (Map.Entry<String, List<RowOfHousingData>> entry : regionData.entrySet()) {
                String region = entry.getKey();
                List<Double> nhpiData = new ArrayList<>();
                for (RowOfHousingData row : entry.getValue()) {
                    double val  = row.getValue().isEmpty() ? 0.0 : Double.parseDouble(row.getValue());
                    nhpiData.add(val);
                }

                List<List<NumericPrediction>> forecast = performRegionForecast(nhpiData, numberOfMonths);

                List<Double> forecastDoubles = new ArrayList<>();
                for (List<NumericPrediction> predictionList : forecast) {
                    NumericPrediction prediction = predictionList.get(0);
                    forecastDoubles.add(prediction.predicted());
                }

                forecastResults.put(region, forecastDoubles);
            }
        }

        return forecastResults;
    }
    private static List<List<NumericPrediction>> performRegionForecast(
            List<Double> inputData, int numberOfMonths) throws Exception {
        Instances data = convertInputDataToInstances(inputData);

        WekaForecaster forecaster = new WekaForecaster();
        forecaster.setFieldsToForecast("NHPI");
        forecaster.setBaseForecaster(new LinearRegression());

        TSLagMaker lagMaker = forecaster.getTSLagMaker();
        lagMaker.setMinLag(1);
        lagMaker.setMaxLag(1);

        forecaster.buildForecaster(data, System.out);
        forecaster.primeForecaster(data);

        return forecaster.forecast(numberOfMonths, System.out);
    }

    private static Instances convertInputDataToInstances(List<Double> inputData) {
        ArrayList<Attribute> attributes = new ArrayList<>();
        Attribute timeAttribute = new Attribute("time");
        attributes.add(timeAttribute);
        Attribute valueAttribute = new Attribute("NHPI");
        attributes.add(valueAttribute);

        Instances data = new Instances("NHPI_data", attributes, inputData.size());

        for (int i = 0; i < inputData.size(); i++) {
            Instance instance = new DenseInstance(2);
            instance.setValue(timeAttribute, i);
            instance.setValue(valueAttribute, inputData.get(i));
            data.add(instance);
        }

        return data;
    }
}
