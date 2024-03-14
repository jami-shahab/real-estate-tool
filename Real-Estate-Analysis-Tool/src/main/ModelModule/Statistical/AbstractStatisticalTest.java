package main.ModelModule.Statistical;

import main.ModelModule.DataConnector_Storage.DbInterface;
import main.Controller.TimeSeries;

import java.util.List;

public abstract class AbstractStatisticalTest {

    protected double[][] data;
    protected DbInterface dbInterface;
    protected List<TimeSeries> timeSeriesList;

    public AbstractStatisticalTest(DbInterface dbInterface) {
        this.dbInterface = dbInterface;
    }

    public void setData(List<TimeSeries> timeSeriesList) throws Exception {
        int numGroups = timeSeriesList.size();
        this.timeSeriesList = timeSeriesList;
        data = new double[numGroups][];

        for (int i = 0; i < numGroups; i++) {
            data[i] = dbInterface.getValuesFormGeoAndRange(timeSeriesList.get(i));
        }
    }
    public abstract double performTest();

    @Override
    public abstract String toString();
}
