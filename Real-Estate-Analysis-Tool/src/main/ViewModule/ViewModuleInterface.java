package main.ViewModule;
import main.Controller.TimeSeries;
import main.ModelModule.DataConnector_Storage.RowOfHousingData;

import java.util.List;
import java.util.Map;

public interface ViewModuleInterface {
    String performStatisticalTest(String testType, List<TimeSeries> ts);
    Map<String, List<String>> getAllLocations();

    List<Map<String, List<RowOfHousingData>>> loadData(List<TimeSeries> timeSeries);

    Map<String, List<Double>> performForecast(List<TimeSeries> timeSeries, int numberOfMonths);

}
