package main.ViewModule;

import main.Controller.TimeSeries;
import main.ModelModule.DataConnector_Storage.DbInterface;
import main.ModelModule.DataConnector_Storage.RowOfHousingData;
import main.ModelModule.DataConnector_Storage.SqlAccessProxy;
import main.ViewModule.Visualizations.Report;
import main.ViewModule.Visualizations.VisualizationFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.*;
import java.util.List;

public class MainView extends TemplateAbstractFrame implements ListListener {
    private static MainView instance;
    private List<ListListener> listeners;
    private Vector<TimeSeries> listOfTimeSeries;
    private List<TimeSeries> timeSeriesList;
    private JPanel rootPanel;
    private JPanel logoPanel;
    private JPanel mainPanel;
    private JPanel northPanel;
    private JPanel southPanel;
    private JPanel centerPanel;
    private JButton btnStatistic;
    private JButton btnForecast;
    private JList<String> stringJList;
    private JComboBox comboAvailableView;
    private JButton btnAddView;
    private JButton btnDeleteView;
    private JButton btnDeleteTimeSeries;
    private JButton btnAddTimeSeries;
    private JButton btnLoad;
    private JComboBox timeIntervalComboBox;
    private JButton btnToggleReportTable;
    private List<TimeSeries> timeSeries;
    private final DbInterface db;
    private VisualizationFactory factory;
    private List<Map<String, List<RowOfHousingData>>> data;

    private boolean dataLoaded = false;

    Map<String, JComponent> chartMap;
    private boolean isTableDisplayed;

    public MainView() {
        super("Estate-Analytics");
        this.db = new SqlAccessProxy();
        listOfTimeSeries = new Vector<>();
        timeSeriesList = new ArrayList<>();
        listeners = new ArrayList<>();
        timeSeries = new ArrayList<>();
        chartMap = new HashMap<>();
        enableBtnStatisticAndForecast();
        btnToggleReportTable.setEnabled(dataLoaded);
        logoPanel.add(getLogoLabel());
        setSize(900, 600);
        getContentPane().add(rootPanel);
        pack();
        centerPanel.setLayout(new GridLayout(0, 2));
        btnAddTimeSeries.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAddTimeSeriesClicked();
            }
        });
        btnDeleteTimeSeries.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onDeleteTimeSeriesClicked();
            }
        });
        btnStatistic.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Use the existing instance of StatisticView
                StatisticView.getInstance(MainView.getInstance()).updateList(listOfTimeSeries);
                StatisticView.getInstance(MainView.getInstance()).setVisible(true);
                instance.dispose();
            }
        });

        btnForecast.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Use the existing instance of StatisticView
                ForeCastView.getInstance(MainView.this).updateList(listOfTimeSeries);
                ForeCastView.getInstance(MainView.this).setVisible(true);
            }
        });
        btnLoad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (listOfTimeSeries.isEmpty()) {
                    showError("Please add time series data first.");
                    return;
                }
                data = viewModuleInterface.loadData(listOfTimeSeries);
                factory = new VisualizationFactory(data, forecastData);
                dataLoaded = true;
                isTableDisplayed = true;

                // Add table view to map
                String tableKey = "Table";
                JOptionPane.showMessageDialog(MainView.this, "Data Loaded Successfully.", "Success  ✔ ", JOptionPane.INFORMATION_MESSAGE);
                // Clear the central panel
                centerPanel.removeAll();
                JComponent tablePanel = null;
                // Add the JScrollPane to the center panel
                try {
                    tablePanel = factory.createVisualization("Table", "", forecastData);
                    centerPanel.add(tablePanel);
                } catch (ParseException ex) {
                    throw new RuntimeException(ex);
                }
                chartMap = new HashMap<>();
                chartMap.put(tableKey, tablePanel);
                centerPanel.revalidate();
                centerPanel.repaint();
                btnToggleReportTable.setEnabled(dataLoaded);
            }
        });
        btnAddView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String selectedChartType = (String) comboAvailableView.getSelectedItem();
                String timeInterval = (String) timeIntervalComboBox.getSelectedItem();

                if (!dataLoaded) {
                    JOptionPane.showMessageDialog(MainView.this,
                            "Please load data before adding charts.",
                            "Data not loaded",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                try {
                    assert selectedChartType != null;
                    // Create chart with unique key and add to center panel and chart map

                    //check if visual exist
                    if (chartMap.containsKey(selectedChartType)) {
                        JOptionPane.showMessageDialog(MainView.this,
                                "This visualization already exists.",
                                "Duplicate visualization",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    // Check if max visualizations reached
                    if (chartMap.size() >= 4) {
                        JOptionPane.showMessageDialog(MainView.this,
                                "You have reached the maximum number of visualizations.",
                                "Maximum visualizations reached",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }


                    JComponent chartPanel = factory.createVisualization(selectedChartType, timeInterval, forecastData);
                    chartPanel.setPreferredSize(new Dimension(400, 300));
                    chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
                    centerPanel.add(chartPanel);
                    centerPanel.revalidate();
                    centerPanel.repaint();
                    chartMap.put(selectedChartType, chartPanel);
                } catch (ParseException ex) {
                    // handle parse exception
                }

            }
        });
        btnDeleteView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String selectedChartType = (String) comboAvailableView.getSelectedItem();
                String timeInterval = (String) timeIntervalComboBox.getSelectedItem();
                //prevent user from deleting the table
                if (selectedChartType.equals("Table")) {
                    JOptionPane.showMessageDialog(getInstance(), "You Can't delete the table", "Table is Default!", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                // Remove chart with unique key from center panel and chart map
                JComponent chartPanel = chartMap.get(selectedChartType);
                if (chartPanel != null) {
                    centerPanel.remove(chartPanel);
                    chartMap.remove(selectedChartType);
                    centerPanel.revalidate();
                    centerPanel.repaint();
                }
            }
        });
        btnToggleReportTable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Toggle the isTableDisplayed flag
                isTableDisplayed = !isTableDisplayed;

                // Get the first component in the center panel
                JComponent firstComponent = (JComponent) centerPanel.getComponent(0);

                // Remove the first component
                centerPanel.remove(firstComponent);

                // Create the new visualization based on the isTableDisplayed flag
                JComponent newComponent = null;
                try {
                    if (isTableDisplayed) {
                        newComponent = factory.createVisualization("Table", "", forecastData);
                        btnToggleReportTable.setText("Switch to Report");
                    } else {
                        newComponent = factory.createVisualization("Report", "", forecastData);
                        btnToggleReportTable.setText("Switch to Table");
                    }
                } catch (ParseException ex) {
                    // Handle parse exception
                }

                // Add the new component to the center panel at the first position
                centerPanel.add(newComponent, 0);

                // Refresh the center panel
                centerPanel.revalidate();
                centerPanel.repaint();
            }
        });
    }

    private void enableBtnStatisticAndForecast() {
        btnStatistic.setEnabled(listOfTimeSeries.size() >= 2);
        btnForecast.setEnabled(listOfTimeSeries.size() >= 1);
    }


    private void onDeleteTimeSeriesClicked() {

        int[] indices = stringJList.getSelectedIndices();

        if (indices.length == 0) {
            JOptionPane.showMessageDialog(getInstance(), "You did not select any TimeSeries to remove.", "Error!", JOptionPane.ERROR_MESSAGE);
        } else {
            // Remove the selected items in reverse order to avoid index issues
            for (int i = indices.length - 1; i >= 0; i--) {
                timeSeriesCache.remove(listOfTimeSeries.get(i));
                listOfTimeSeries.remove(indices[i]);
            }
            Vector<String> s = stringifyTimeSeries(listOfTimeSeries);
            stringJList.setListData(s);
            enableBtnStatisticAndForecast();
        }
        //remove the Table if there is no data
        if (listOfTimeSeries.size() == 0) {
            // Clear the central panel
            centerPanel.removeAll();
            centerPanel.revalidate();
            centerPanel.repaint();
            btnToggleReportTable.setEnabled(false);
            btnToggleReportTable.setText("Switch to Report");
            isTableDisplayed = false;
            dataLoaded = false;
        }

    }

    private Vector<String> stringifyTimeSeries(Vector<TimeSeries> timeSeriesVector) {
        Vector<String> s = new Vector<>();
        for (TimeSeries ts : listOfTimeSeries)
            s.add(ts.toString());
        return s;
    }

    // Method called when the "Add Time Series" button is clicked
    private void onAddTimeSeriesClicked() {
        AddTimeSeries.getInstance(MainView.getInstance()).setVisible(true);
        enableBtnStatisticAndForecast();
    }

    @Override
    public void onListChanged(TimeSeries list) {
        // Update the list of TimeSeries and set it as the data for the JList
        timeSeries = new ArrayList<>();
        timeSeries.add(list);
        this.listOfTimeSeries.addAll(timeSeries);
        Vector<String> s = stringifyTimeSeries(listOfTimeSeries);
        stringJList.setListData(s);
        enableBtnStatisticAndForecast();
    }

    public static MainView getInstance() {
        if (instance == null)
            instance = new MainView();

        return instance;
    }

    public static void main(String[] args) {

        JFrame frame = MainView.getInstance();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // open in full screen
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setContentPane(instance.rootPanel);
        frame.setVisible(true);

    }


    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        rootPanel = new JPanel();
        rootPanel.setLayout(new BorderLayout(0, 0));
        rootPanel.setPreferredSize(new Dimension(900, 600));
        logoPanel = new JPanel();
        logoPanel.setLayout(new GridBagLayout());
        rootPanel.add(logoPanel, BorderLayout.NORTH);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        logoPanel.add(panel1, gbc);
        final JLabel label1 = new JLabel();
        label1.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label1, gbc);
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 0));
        rootPanel.add(mainPanel, BorderLayout.CENTER);
        northPanel = new JPanel();
        northPanel.setLayout(new GridBagLayout());
        mainPanel.add(northPanel, BorderLayout.NORTH);
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setPreferredSize(new Dimension(-1, 100));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 8;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 10, 5);
        northPanel.add(scrollPane1, gbc);
        stringJList = new JList();
        stringJList.setFixedCellHeight(-1);
        stringJList.setMaximumSize(new Dimension(-1, -1));
        stringJList.setMinimumSize(new Dimension(-1, -1));
        stringJList.setPreferredSize(new Dimension(-1, 900));
        stringJList.setValueIsAdjusting(false);
        scrollPane1.setViewportView(stringJList);
        btnAddTimeSeries = new JButton();
        btnAddTimeSeries.setText("Add Time Series");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 5, 0, 0);
        northPanel.add(btnAddTimeSeries, gbc);
        btnDeleteTimeSeries = new JButton();
        btnDeleteTimeSeries.setText("Delete Time Series");
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 5);
        northPanel.add(btnDeleteTimeSeries, gbc);
        comboAvailableView = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("Pie Chart");
        defaultComboBoxModel1.addElement("Line Chart");
        defaultComboBoxModel1.addElement("Bar Chart");
        defaultComboBoxModel1.addElement("Scatter Chart");
        comboAvailableView.setModel(defaultComboBoxModel1);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 5, 0, 0);
        northPanel.add(comboAvailableView, gbc);
        final JLabel label2 = new JLabel();
        label2.setHorizontalAlignment(0);
        label2.setHorizontalTextPosition(0);
        label2.setText("Availabe View");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 0);
        northPanel.add(label2, gbc);
        btnLoad = new JButton();
        btnLoad.setText("Load");
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 5);
        northPanel.add(btnLoad, gbc);
        timeIntervalComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        defaultComboBoxModel2.addElement("monthly");
        defaultComboBoxModel2.addElement("yearly");
        defaultComboBoxModel2.addElement("every 6 months");
        timeIntervalComboBox.setModel(defaultComboBoxModel2);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        northPanel.add(timeIntervalComboBox, gbc);
        final JLabel label3 = new JLabel();
        label3.setHorizontalAlignment(0);
        label3.setHorizontalTextPosition(0);
        label3.setText("Granularity");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 0);
        northPanel.add(label3, gbc);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 5, 0, 0);
        northPanel.add(panel2, gbc);
        btnAddView = new JButton();
        btnAddView.setText("+");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(btnAddView, gbc);
        btnDeleteView = new JButton();
        btnDeleteView.setText("-");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(btnDeleteView, gbc);
        btnToggleReportTable = new JButton();
        btnToggleReportTable.setText("Switch to Report");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        northPanel.add(btnToggleReportTable, gbc);
        southPanel = new JPanel();
        southPanel.setLayout(new GridBagLayout());
        mainPanel.add(southPanel, BorderLayout.SOUTH);
        btnStatistic = new JButton();
        btnStatistic.setText("Statistical Analytics");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        southPanel.add(btnStatistic, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        southPanel.add(spacer1, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        southPanel.add(spacer2, gbc);
        btnForecast = new JButton();
        btnForecast.setText("Forecasting");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        southPanel.add(btnForecast, gbc);
        centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        mainPanel.add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }


}
