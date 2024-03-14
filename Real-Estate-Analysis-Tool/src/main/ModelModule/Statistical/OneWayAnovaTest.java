package main.ModelModule.Statistical;

import main.ModelModule.DataConnector_Storage.DbInterface;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.inference.OneWayAnova;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OneWayAnovaTest extends AbstractStatisticalTest {

    public OneWayAnovaTest(DbInterface dbInterface) {
        super(dbInterface);
    }

    @Override
    public double performTest() {
        OneWayAnova oneWayAnova = new OneWayAnova();
        List<double[]> dataList = new ArrayList<>(Arrays.asList(data));
        return oneWayAnova.anovaPValue(dataList);
    }

    @Override
    public String toString() {
        double significanceLevel = 0.05;
        double pValue = performTest();
        StringBuilder summary = new StringBuilder("**One-Way ANOVA test results:**\n\n");

        for (int i = 0; i < data.length; i++) {
            summary.append(String.format("%s (mean = %.2f)\n", timeSeriesList.get(i).toString(), StatUtils.mean(data[i])));
        }

        summary.append(String.format("\nF-test p-value = %.18f\n", pValue));

        if (pValue < significanceLevel) {
            summary.append(String.format("\nAs the p-value is less than the chosen significance level (α = %.2f), we reject the null hypothesis. There is a significant difference between the mean values of the groups.\n", significanceLevel));
        } else {
            summary.append(String.format("\nAs the p-value is greater than or equal to the chosen significance level (α = %.2f), we fail to reject the null hypothesis. There is no significant difference between the mean values of the groups.\n", significanceLevel));
        }

        return summary.toString();
    }


}
