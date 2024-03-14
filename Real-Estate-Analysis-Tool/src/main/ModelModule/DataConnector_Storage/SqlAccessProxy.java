package main.ModelModule.DataConnector_Storage;

import main.Controller.TimeSeries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlAccessProxy implements DbInterface{

	private static DbInterface db;
	private Map<String, Map<String, List<RowOfHousingData>>> cache;
	
	
	public SqlAccessProxy() {
        db = MySQLAccess.getInstance();
        this.cache = new HashMap<>();
    }


	@Override
	public Map<String, List<RowOfHousingData>> readDataBase(TimeSeries t) throws Exception {
		
		String key = t.toString();

		if(cache.containsKey(key)) {
//			System.out.println("found and retrieved");
			return cache.get(key);
		}
		Map<String, List<RowOfHousingData>> data = db.readDataBase(t);
		cache.put(key, data);
		return data;
	}

	@Override
	public ArrayList<String> getAllYears() throws Exception {
		return db.getAllYears();
	}

	@Override
	public ArrayList<String> getAllMonths() throws Exception {
		return db.getAllMonths();
	}

	@Override
	public Map<String, List<String>> getAllLocations() throws Exception {
		return db.getAllLocations();
	}

	@Override
	public double[] getValuesFormGeoAndRange(TimeSeries timeSeries) throws Exception {
		return db.getValuesFormGeoAndRange(timeSeries);
	}

	@Override
	public List<Map<String, List<RowOfHousingData>>> multipleQueries(List<TimeSeries> tsList) throws Exception {
		
		List<Map<String, List<RowOfHousingData>>> list = new ArrayList<Map<String, List<RowOfHousingData>>>();

		for(int i = 0; i < tsList.size(); i++) {
			Map<String, List<RowOfHousingData>> currentQuery = this.readDataBase(tsList.get(i));
			list.add(currentQuery);
		}
		
		return list;
	}

}
