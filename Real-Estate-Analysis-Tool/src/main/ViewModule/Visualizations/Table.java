package main.ViewModule.Visualizations;

import main.ModelModule.DataConnector_Storage.RowOfHousingData;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Table {
    private final String[] columns;
    private final List<List<String>> data;

    private Table(String[] columns, List<List<String>> data) {
        this.columns = columns;
        this.data = data;
    }

    // helper method to get data for the table
    public static Table generateTableData(List<Map<String, List<RowOfHousingData>>> data) {
        String[] columns = {"REF_DATE", "GEO", "NHPI VALUE"};
        List<List<String>> tableData = new ArrayList<>();

        for (Map<String, List<RowOfHousingData>> map : data) {
            for (Map.Entry<String, List<RowOfHousingData>> entry : map.entrySet()) {
                String region = entry.getKey();
                List<RowOfHousingData> rows = entry.getValue();
                for (RowOfHousingData row : rows) {
                    String refDate = row.getRefDate();
                    String value = row.getValue();
                    List<String> rowData = new ArrayList<>();
                    rowData.add(refDate);
                    rowData.add(region);
                    rowData.add(value);
                    tableData.add(rowData);
                }
            }
        }

        return new Table(columns, tableData);
    }


    public JTable createJTable() {
        Object[][] tableData = data.stream()
                .map(row -> row.toArray(String[]::new))
                .toArray(String[][]::new);
        return new JTable(tableData, columns);
    }

}
