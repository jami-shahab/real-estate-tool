package main.ModelModule.DataConnector_Storage;

import main.Controller.TimeSeries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface DbInterface {
	Map<String, List<RowOfHousingData>> readDataBase(TimeSeries t)throws Exception;
	ArrayList<String> getAllYears() throws Exception;
	ArrayList<String> getAllMonths() throws Exception;
	Map<String, List<String>> getAllLocations() throws Exception;

	double[] getValuesFormGeoAndRange(TimeSeries timeSeries) throws Exception;
	List<Map<String, List<RowOfHousingData>>> multipleQueries(List<TimeSeries> tsList) throws Exception;
}
