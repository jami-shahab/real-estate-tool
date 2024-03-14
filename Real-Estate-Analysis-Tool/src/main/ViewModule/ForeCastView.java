package main.ViewModule;

import main.Controller.TimeSeries;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;

public class ForeCastView extends TemplateAbstractFrame {

    private JPanel rootPanel;
    private JPanel logoPanel;
    private JPanel mainPanel;
    private JPanel timeSeriesPanel;
    private JButton btnRun;
    private JButton closeButton;
    private JTextPane txtOutput;
    private JComboBox comboMonths;
    private JButton btnClearForeCast;
    private final MainView parentFrame;
    private static ForeCastView instance;
    private List<TimeSeries> timeSeriesList;
    private Map<TimeSeries, JCheckBox> checkBoxMap;


    public ForeCastView(MainView parentFrame) {
        super("Forecast View");
        this.parentFrame = parentFrame;
        logoPanel.add(getLogoLabel());
        this.timeSeriesList = new ArrayList<>();
        this.checkBoxMap = new HashMap<>();
        getContentPane().add(rootPanel);
        pack();
        setSize(900, 500);

        // Add a WindowListener to handle the window closing event
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                goToParent();
            }
        });
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goToParent();
            }
        });
        btnRun.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<TimeSeries> selectedTimeSeriesList = getTimeSeriesList();
                String selectedTest = (String) comboMonths.getSelectedItem();
                assert selectedTest != null;
                int numberOfMonths = Integer.parseInt(selectedTest);
                if (!isValidSelection(selectedTimeSeriesList.size())) {
                    return;
                }

                forecastData = viewModuleInterface.performForecast(selectedTimeSeriesList, numberOfMonths);

                txtOutput.setText(printForeCastData(forecastData, numberOfMonths));


            }
        });
        btnClearForeCast.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtOutput.setText("");
                forecastData = new HashMap<>();
            }
        });
    }


    private String printForeCastData(Map<String, List<Double>> forecastResults, int numOfMonth) {
        StringBuilder output = new StringBuilder();

        // Print forecast results
        for (Map.Entry<String, List<Double>> entry : forecastResults.entrySet()) {
            String region = entry.getKey();
            List<Double> forecast = entry.getValue();
            output.append("Forecast for ").append(region).append(" for the next ").append(numOfMonth).append(": \n\n");
            for (int i = 0; i < forecast.size(); i++) {
                double prediction = forecast.get(i);
                output.append(String.format("Month %d: %.6f%n", i + 1, prediction));
            }
            output.append("\n");
        }

        return output.toString();
    }


    private List<TimeSeries> getTimeSeriesList() {
        List<TimeSeries> selectedTimeSeriesList = new ArrayList<>();
        // Loop through the map and check which JCheckBox objects are selected
        for (Map.Entry<TimeSeries, JCheckBox> entry : checkBoxMap.entrySet()) {
            if (entry.getValue().isSelected()) {
                // Add the corresponding TimeSeries object to the selectedTimeSeriesList
                selectedTimeSeriesList.add(entry.getKey());
            }
        }

        return selectedTimeSeriesList;
    }

    private void goToParent() {

        // When the button is clicked, set the parent frame visible
        parentFrame.setVisible(true);
        dispose(); // Dispose of the frame

    }

    public static ForeCastView getInstance(MainView parentFrame) {
        if (instance == null)
            instance = new ForeCastView(parentFrame);

        return instance;
    }


    private boolean isValidSelection(int numSelected) {
        if (numSelected < 1) {
            showError("Please select at least One time series.");
            return false;
        }
        return true;
    }

    public void updateList(Vector<TimeSeries> list) {

        //add all the time series sent from main
        timeSeriesList.addAll(list);
        // Remove all components from the checkBoxPane
        timeSeriesPanel.removeAll();
        // Set a fixed size for the checkboxes
        Dimension checkBoxSize = new Dimension(600, 30);

        // Add a new JCheckBox for each item in the list
        for (TimeSeries item : list) {
            JCheckBox checkBox = new JCheckBox(item.toString());
            checkBox.setPreferredSize(checkBoxSize);
            timeSeriesPanel.add(checkBox);
            checkBoxMap.put(item, checkBox);
        }

    }

    // Override the processWindowEvent method to handle the window closing event
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            goToParent();
        }
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
        rootPanel.setMaximumSize(new Dimension(1000, 850));
        rootPanel.setMinimumSize(new Dimension(900, 500));
        rootPanel.setPreferredSize(new Dimension(900, 500));
        logoPanel = new JPanel();
        logoPanel.setLayout(new GridBagLayout());
        logoPanel.setAlignmentX(0.5f);
        logoPanel.setAlignmentY(0.5f);
        logoPanel.setMinimumSize(new Dimension(15, 15));
        logoPanel.setPreferredSize(new Dimension(50, 75));
        logoPanel.setRequestFocusEnabled(true);
        rootPanel.add(logoPanel, BorderLayout.NORTH);
        final JLabel label1 = new JLabel();
        label1.setHorizontalAlignment(2);
        label1.setHorizontalTextPosition(2);
        label1.setText("");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        logoPanel.add(label1, gbc);
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 5));
        mainPanel.setPreferredSize(new Dimension(375, 300));
        rootPanel.add(mainPanel, BorderLayout.CENTER);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        panel1.setMinimumSize(new Dimension(92, 134));
        panel1.setPreferredSize(new Dimension(150, 134));
        mainPanel.add(panel1, BorderLayout.EAST);
        final JLabel label2 = new JLabel();
        label2.setText("Month");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label2, gbc);
        comboMonths = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("3");
        defaultComboBoxModel1.addElement("6");
        defaultComboBoxModel1.addElement("9");
        defaultComboBoxModel1.addElement("12");
        comboMonths.setModel(defaultComboBoxModel1);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(comboMonths, gbc);
        btnRun = new JButton();
        btnRun.setText("Run");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(btnRun, gbc);
        closeButton = new JButton();
        closeButton.setText("Close");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(closeButton, gbc);
        btnClearForeCast = new JButton();
        btnClearForeCast.setText("Clear");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(btnClearForeCast, gbc);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        mainPanel.add(panel2, BorderLayout.CENTER);
        final JScrollPane scrollPane1 = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(scrollPane1, gbc);
        timeSeriesPanel = new JPanel();
        timeSeriesPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        timeSeriesPanel.setMaximumSize(new Dimension(32767, 32767));
        timeSeriesPanel.setMinimumSize(new Dimension(14, 14));
        timeSeriesPanel.setOpaque(false);
        timeSeriesPanel.setPreferredSize(new Dimension(600, 900));
        scrollPane1.setViewportView(timeSeriesPanel);
        timeSeriesPanel.setBorder(BorderFactory.createTitledBorder(null, "Time Series", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        panel3.setMinimumSize(new Dimension(300, 200));
        panel3.setPreferredSize(new Dimension(300, 200));
        rootPanel.add(panel3, BorderLayout.SOUTH);
        final JScrollPane scrollPane2 = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel3.add(scrollPane2, gbc);
        txtOutput = new JTextPane();
        txtOutput.setEditable(false);
        txtOutput.setMargin(new Insets(3, 3, 3, 10));
        txtOutput.setMinimumSize(new Dimension(300, 900));
        txtOutput.setPreferredSize(new Dimension(300, 900));
        scrollPane2.setViewportView(txtOutput);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }

}
