package main.ViewModule;

import main.Controller.Controller;
import main.Controller.TimeSeries;
import main.ModelModule.DataConnector_Storage.DbInterface;
import main.ModelModule.DataConnector_Storage.MySQLAccess;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;

public class StatisticView extends TemplateAbstractFrame {

    private DbInterface db = MySQLAccess.getInstance();
    private static StatisticView instance;
    private JPanel statRootPanel;
    private JPanel logoPanel;
    private JPanel panel;
    private JButton btnLoadTest;
    private JPanel outputPanel;
    private JComboBox statisticCombo;
    private JButton btnCloseStatistical;
    private JPanel checkBoxPanel;
    private JTextPane txtOutPutArea;
    // Field to keep track of the parent frame
    private MainView parentFrame;
    // map to keep track of selected TimeSeries objects and their corresponding JCheckBox objects
    private Map<TimeSeries, JCheckBox> checkBoxMap;

    private List<TimeSeries> timeSeriesList;

    public StatisticView(MainView parentFrame) {

        super("Statistical View");
        this.parentFrame = parentFrame; // Set the parent frame
        timeSeriesList = new ArrayList<>();
        logoPanel.add(getLogoLabel());
        checkBoxMap = new HashMap<>();
        getContentPane().add(statRootPanel);
        pack();
        setSize(900, 500);

        // Add a WindowListener to handle the window closing event
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // When the window is closing, set the parent frame visible

                parentFrame.setVisible(true);
                dispose(); // Dispose of the StatisticView
            }
        });
        btnCloseStatistical.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // When the button is clicked, set the parent frame visible
                txtOutPutArea.setText("");
                parentFrame.setVisible(true);
                dispose(); // Dispose of the StatisticView
            }
        });
        btnLoadTest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<TimeSeries> selectedTimeSeriesList = getTimeSeriesList();
                String selectedTest = (String) statisticCombo.getSelectedItem();

                if (!isValidSelection(selectedTest, selectedTimeSeriesList.size())) {
                    return;
                }
                //getting the result from the controller
                String st = viewModuleInterface.performStatisticalTest(selectedTest, selectedTimeSeriesList);
                txtOutPutArea.setText(st); // Set the text of txtOutputArea to the result string

            }
        });
    }


    public static StatisticView getInstance(MainView parentFrame) {
        if (instance == null)
            instance = new StatisticView(parentFrame);

        return instance;
    }

    // Method called to update the list in the StatisticView
    public void updateList(Vector<TimeSeries> list) {
        //add all the time series sent from main
        timeSeriesList.addAll(list);

        // Remove all components from the checkBoxPane
        checkBoxPanel.removeAll();
        // Set a fixed size for the checkboxes
        Dimension checkBoxSize = new Dimension(600, 30);

        // Add a new JCheckBox for each item in the list
        for (TimeSeries item : list) {
            JCheckBox checkBox = new JCheckBox(item.toString());
            checkBox.setPreferredSize(checkBoxSize);
            checkBoxPanel.add(checkBox);
            // Add the TimeSeries object and its corresponding JCheckBox object to the map
            checkBoxMap.put(item, checkBox);
        }

    }

    private boolean isValidSelection(String selectedTest, int numSelected) {
        if (numSelected < 2) {
            showError("Please select at least two time series.");
            return false;
        }
        if (selectedTest.equals("Student-T-Test") && numSelected != 2) {
            showError("Please select exactly two time series for t-test.");
            return false;
        }
        if (selectedTest.equals("One Way ANOVA") && numSelected < 3) {
            showError("Please select at least three time series for ANOVA.");
            return false;
        }
        return true;
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

    // Override the processWindowEvent method to handle the window closing event
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            // When the window is closing, set the parent frame visible
            txtOutPutArea.setText("");
            parentFrame.setVisible(true);
            dispose(); // Dispose of the StatisticView
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
        statRootPanel = new JPanel();
        statRootPanel.setLayout(new BorderLayout(0, 0));
        statRootPanel.setInheritsPopupMenu(false);
        statRootPanel.setMaximumSize(new Dimension(1000, 850));
        statRootPanel.setMinimumSize(new Dimension(900, 500));
        statRootPanel.setPreferredSize(new Dimension(900, 500));
        logoPanel = new JPanel();
        logoPanel.setLayout(new GridBagLayout());
        statRootPanel.add(logoPanel, BorderLayout.NORTH);
        final JLabel label1 = new JLabel();
        label1.setHorizontalAlignment(2);
        label1.setHorizontalTextPosition(2);
        label1.setText("");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        logoPanel.add(label1, gbc);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        panel1.setMaximumSize(new Dimension(4, 4));
        panel1.setPreferredSize(new Dimension(600, 50));
        statRootPanel.add(panel1, BorderLayout.WEST);
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setMinimumSize(new Dimension(600, 200));
        scrollPane1.setPreferredSize(new Dimension(600, 260));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(scrollPane1, gbc);
        scrollPane1.setBorder(BorderFactory.createTitledBorder(null, "Time Series", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        checkBoxPanel = new JPanel();
        checkBoxPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        checkBoxPanel.setPreferredSize(new Dimension(650, 600));
        scrollPane1.setViewportView(checkBoxPanel);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        panel2.setAlignmentX(1.0f);
        panel2.setAlignmentY(1.0f);
        panel2.setPreferredSize(new Dimension(200, 0));
        statRootPanel.add(panel2, BorderLayout.EAST);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(spacer1, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel2.add(spacer2, gbc);
        btnLoadTest = new JButton();
        btnLoadTest.setText("Run Statistic Test");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(btnLoadTest, gbc);
        statisticCombo = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("Student-T-Test");
        defaultComboBoxModel1.addElement("One Way ANOVA");
        statisticCombo.setModel(defaultComboBoxModel1);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(statisticCombo, gbc);
        btnCloseStatistical = new JButton();
        btnCloseStatistical.setText("Close");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(btnCloseStatistical, gbc);
        outputPanel = new JPanel();
        outputPanel.setLayout(new BorderLayout(0, 0));
        outputPanel.setMinimumSize(new Dimension(300, 200));
        outputPanel.setName("Statistical Test Output");
        outputPanel.setPreferredSize(new Dimension(300, 200));
        statRootPanel.add(outputPanel, BorderLayout.SOUTH);
        outputPanel.setBorder(BorderFactory.createTitledBorder(null, "Output", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JScrollPane scrollPane2 = new JScrollPane();
        scrollPane2.setMinimumSize(new Dimension(10, 10));
        scrollPane2.setPreferredSize(new Dimension(300, 180));
        outputPanel.add(scrollPane2, BorderLayout.CENTER);
        txtOutPutArea = new JTextPane();
        txtOutPutArea.setEditable(false);
        txtOutPutArea.setMargin(new Insets(3, 3, 15, 3));
        txtOutPutArea.setMinimumSize(new Dimension(300, 900));
        txtOutPutArea.setOpaque(true);
        txtOutPutArea.setPreferredSize(new Dimension(300, 900));
        scrollPane2.setViewportView(txtOutPutArea);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return statRootPanel;
    }

}
