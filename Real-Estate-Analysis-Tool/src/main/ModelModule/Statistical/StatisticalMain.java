package main.ModelModule.Statistical;

import main.ModelModule.DataConnector_Storage.DbInterface;
import main.ModelModule.DataConnector_Storage.MySQLAccess;
import main.Controller.TimeSeries;

import java.util.List;


public class StatisticalMain {

    public static void main(String[] args) {

        DbInterface dbInterface = MySQLAccess.getInstance();;
        // Create TimeSeries objects
        TimeSeries ts1 = new TimeSeries("Canada", "Jan-00", "Feb-03");
        TimeSeries ts2 = new TimeSeries("Canada", "Jan-04", "Feb-07");
        TimeSeries ts3 = new TimeSeries("Canada", "Jan-07", "Feb-10");

        // Independent t-test
        IndependentTTest tTest = new IndependentTTest(dbInterface);
        try {
            tTest.setData(List.of(new TimeSeries[]{ts1, ts2}));
            System.out.println(tTest);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // One-Way ANOVA test
        OneWayAnovaTest anovaTest = new OneWayAnovaTest(dbInterface);
        try {
            anovaTest.setData(List.of(new TimeSeries[]{ts1, ts2, ts3}));
            System.out.println(anovaTest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
