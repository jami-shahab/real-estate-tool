package main.ModelModule.DataConnector_Storage;
import main.Controller.TimeSeries;

import java.sql.*;
import java.util.*;


public class MySQLAccess implements DbInterface{

    private static MySQLAccess instance;
    private Connection connect = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    private final String URL = "jdbc:mysql://localhost:3306/eecs3311?";
    private final String USERNAME = "root";
    private final String PASSWORD = "root1234";


    private MySQLAccess() {
    }

    public static MySQLAccess getInstance() {
        if (instance == null) {
            instance = new MySQLAccess();
        }
        return instance;
    }

    @Override
    public Map<String, List<RowOfHousingData>> readDataBase(TimeSeries timeSeries) throws Exception {
        try {
            // This will load the MySQL driver, each DB has its own driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            connect = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            // Set up the connection with the DB with location, user, password
            statement = connect.createStatement();

            String query = "SELECT * FROM `18100205` WHERE GEO = ?";

            if (!timeSeries.getFromDate().isEmpty() && !timeSeries.getToDate().isEmpty()) {
                query += " AND STR_TO_DATE(Concat('01-', REF_DATE), '%d-%b-%y') BETWEEN STR_TO_DATE(?, '%d-%b-%y') AND STR_TO_DATE(?, '%d-%b-%y')";
            }else if (!timeSeries.getFromDate().isEmpty()) {
                query += " AND STR_TO_DATE(Concat('01-', REF_DATE), '%d-%b-%y') >= STR_TO_DATE(?, '%d-%b-%y')";
            } else if (!timeSeries.getToDate().isEmpty()) {
                query += " AND STR_TO_DATE(Concat('01-', REF_DATE), '%d-%b-%y') <= STR_TO_DATE(?, '%d-%b-%y')";
            }

            query += "  AND `New housing price indexes` LIKE '%Total%' ";

                PreparedStatement ps = connect.prepareStatement(query);
                ps.setString(1, timeSeries.getGeo());

                if (!timeSeries.getFromDate().isEmpty() && !timeSeries.getToDate().isEmpty()) {
                    ps.setString(2, "01-" + timeSeries.getFromDate());
                    ps.setString(3, "01-" + timeSeries.getToDate());
                } else if (!timeSeries.getFromDate().isEmpty()) {

                    ps.setString(2, "01-" +timeSeries.getFromDate());

                } else if(!timeSeries.getToDate().isEmpty()) {
                    ps.setString(2, "01-" + timeSeries.getToDate());
                }

            resultSet = ps.executeQuery();
            return getHousingDataMap(resultSet);

            } catch(Exception e){
                throw e;
            }finally {

                close();
            }
        }



    public Map<String, List<RowOfHousingData>> getHousingDataMap(ResultSet resultSet) throws Exception {
        Map<String, List<RowOfHousingData>> housingDataMap = new HashMap<>();

            while (resultSet.next()) {
                String refDate = resultSet.getString("REF_DATE");
                String geo = resultSet.getString("GEO");
                String indexType = resultSet.getString("New housing price indexes");
                String value = resultSet.getString("VALUE");
            RowOfHousingData data = new RowOfHousingData(refDate, geo, indexType, value);
            if (!housingDataMap.containsKey(geo)) {
                housingDataMap.put(geo, new ArrayList<>());
            }
            housingDataMap.get(geo).add(data);

        }
            return housingDataMap;
        }

        private void close() {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }

                if (statement != null) {
                    statement.close();
                }

                if (connect != null) {
                    connect.close();
                }
            } catch (Exception e) {

            }
        }
        private double[] getValuesFromRange(List<RowOfHousingData> list) {
            double [] result = new double[list.size()];
            int count = 0;
            for (RowOfHousingData hpd: list) {

                String value = hpd.getValue();
                double parsedValue = value.isEmpty() ? 0.0 : Double.parseDouble(value);
                result[count++] = parsedValue;

            }

            return result;
        }

        @Override
        public double[] getValuesFormGeoAndRange(TimeSeries ts) throws Exception {

            Map<String, List<RowOfHousingData>> map = readDataBase(ts);

            List<RowOfHousingData> list = new ArrayList<>();

            for (Map.Entry<String, List<RowOfHousingData>> entry: map.entrySet()) {

                list.addAll(entry.getValue());

            }

            return getValuesFromRange(list);
        }

        @Override
        public ArrayList<String> getAllYears() throws Exception {

            ArrayList<String> years = new ArrayList<String>();

            try {
                // This will load the MySQL driver, each DB has its own driver
                Class.forName("com.mysql.cj.jdbc.Driver");

                connect = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                // Set up the connection with the DB with location, user, password
                statement = connect.createStatement();

                // Result set get the result of the SQL query
                resultSet = statement.executeQuery("SELECT DISTINCT year(STR_TO_DATE(CONCAT('02-',REF_DATE), '%d-%b-%y')) from `18100205`"
                        + "UNION "
                        + "SELECT DISTINCT year(STR_TO_DATE(CONCAT('02-',REF_DATE), '%d-%y-%b')) from `18100205`;");

                while(resultSet.next()) {
                    years.add(resultSet.getString(1));
                }
                return years;

            } catch (Exception e) {
                throw e;
            }
        }

        @Override
        public ArrayList<String> getAllMonths() throws Exception {
            ArrayList<String> months = new ArrayList<String>();

            try {
                // This will load the MySQL driver, each DB has its own driver
                Class.forName("com.mysql.cj.jdbc.Driver");

                connect = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                // Set up the connection with the DB with location, user, password
                statement = connect.createStatement();

                // Result set get the result of the SQL query
                resultSet = statement.executeQuery("SELECT DISTINCT MONTH(STR_TO_DATE(CONCAT('02-',REF_DATE), '%d-%b-%y')) from `18100205`"
                        + "UNION "
                        + "SELECT DISTINCT MONTH(STR_TO_DATE(CONCAT('02-',REF_DATE), '%d-%y-%b')) from `18100205`;");

                while(resultSet.next()) {
                    months.add(resultSet.getString(1));
                }
                return months;

            } catch (Exception e) {
                throw e;
            }

        }

    @Override
    public Map<String, List<String>> getAllLocations() throws Exception {
        Map<String, List<String>> result = new HashMap<>();
        List<String> allLocation = getListOfLocations();
        for (String s : allLocation) {
            int lastCommaIndex = s.lastIndexOf(",");
            String key = lastCommaIndex == -1 ? s : s.substring(lastCommaIndex + 1).trim();
            String value = lastCommaIndex == -1 ? "" : s.substring(0, lastCommaIndex).trim();
            if (!result.containsKey(key)) {
                result.put(key, new ArrayList<>());
            }
            if (!value.isEmpty()) {
                result.get(key).add(value);
            }
        }
        return result;
    }

        private ArrayList<String> getListOfLocations() throws Exception {
            ArrayList<String> locations = new ArrayList<String>();

            try {
                // This will load the MySQL driver, each DB has its own driver
                Class.forName("com.mysql.cj.jdbc.Driver");

                connect = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                // Set up the connection with the DB with location, user, password
                statement = connect.createStatement();

                // Result set get the result of the SQL query
                resultSet = statement.executeQuery("SELECT DISTINCT GEO from `18100205`;");

                while(resultSet.next()) {
                    locations.add(resultSet.getString(1));
                }
                return locations;

            } catch (Exception e) {
                throw e;
            }

        }


        @Override
        public List<Map<String, List<RowOfHousingData>>> multipleQueries(List<TimeSeries> tsList) throws Exception {

            List<Map<String, List<RowOfHousingData>>> list = new ArrayList<Map<String, List<RowOfHousingData>>>();

            for(int i = 0; i < tsList.size(); i++) {
                Map<String, List<RowOfHousingData>> currentQuery = readDataBase(tsList.get(i));
                list.add(currentQuery);
            }

            return list;
        }
    }