package main.ModelModule.Statistical;

import main.ModelModule.DataConnector_Storage.DbInterface;
import org.apache.commons.math3.stat.*;
import org.apache.commons.math3.stat.inference.TestUtils;

    public class IndependentTTest extends AbstractStatisticalTest {

        public IndependentTTest(DbInterface dbInterface) {
            super(dbInterface);
        }

        @Override
        public double performTest() {
            return TestUtils.tTest(data[0], data[1]);
        }
        @Override
        public String toString() {
            double significanceLevel = 0.05;
            double pValue = performTest();
            StringBuilder summary = new StringBuilder("**Independent t-test results:**\n\n");

            for (int i = 0; i < data.length; i++) {
                summary.append(String.format("%s (mean = %.2f)\n", timeSeriesList.get(i).toString(), StatUtils.mean(data[i])));
            }

            summary.append(String.format("\nt-test p-value = %.18f\n", pValue));


            if (pValue < significanceLevel) {
                summary.append(String.format("\nAs the p-value is less than the chosen significance level (α = %.2f), we reject the null hypothesis. There is a significant difference between the mean values of the groups.\n", significanceLevel));
            } else {
                summary.append(String.format("\nAs the p-value is greater than or equal to the chosen significance level (α = %.2f), we fail to reject the null hypothesis. There is no significant difference between the mean values of the groups.\n", significanceLevel));
            }

            return summary.toString();
        }

    }

