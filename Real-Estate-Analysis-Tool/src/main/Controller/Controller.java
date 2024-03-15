package main.Controller;

import main.ModelModule.DataConnector_Storage.DbInterface;
import main.ModelModule.DataConnector_Storage.MySQLAccess;
import main.ModelModule.DataConnector_Storage.RowOfHousingData;
import main.ModelModule.Forecast.LinearRegressionPrediction;
import main.ModelModule.Statistical.AbstractStatisticalTest;
import main.ModelModule.Statistical.OneWayAnovaTest;
import main.ModelModule.Statistical.IndependentTTest;
import main.ViewModule.MainView;
import main.ViewModule.ViewModuleInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Controller implements ViewModuleInterface {

        private final String [] STAT_TEST = {"T-Test", "One Way ANOVA"};
        private DbInterface dbInterface;

        private MainView mainView;


        public Controller() {
            // Initialize model and view components
            dbInterface = MySQLAccess.getInstance();;

        }


        @Override
        public String performStatisticalTest(String testType, List<TimeSeries> timeSeries) {

                AbstractStatisticalTest test = null;

                if (STAT_TEST[0].equals(testType)) {
                        test = new IndependentTTest(dbInterface);
                } else if (STAT_TEST[1].equals(testType)) {
                        test = new OneWayAnovaTest(dbInterface);
                }

                if (test != null) {
                        try {
                                test.setData(timeSeries);
                        } catch (Exception e) {
                                e.printStackTrace();
                        }
                }


                return test != null ? test.toString() : null;

        }

        @Override
        public Map<String, List<String>> getAllLocations() {
                try {
                        return dbInterface.getAllLocations();
                } catch (Exception e) {
                        throw new RuntimeException(e);
                }
        }

        @Override
        public List<Map<String, List<RowOfHousingData>>> loadData(List<TimeSeries> timeSeries) {
                try {
                        return dbInterface.multipleQueries(timeSeries);
                } catch (Exception e) {
                        throw new RuntimeException(e);
                }
        }

        @Override
        public Map<String, List<Double>> performForecast( List<TimeSeries> timeSeries, int numberOfMonths) {

                List<Map<String, List<RowOfHousingData>>> inputData = new ArrayList<>();
                Map<String, List<Double>> prediction= new HashMap<>();
                try {
                        inputData = dbInterface.multipleQueries(timeSeries);
                } catch (Exception e) {
                        throw new RuntimeException(e);
                }


                try {
                        prediction= LinearRegressionPrediction.performTimeSeriesForecast(inputData,numberOfMonths);
                } catch (Exception e) {
                        throw new RuntimeException(e);
                }
                return prediction;
        }
}
