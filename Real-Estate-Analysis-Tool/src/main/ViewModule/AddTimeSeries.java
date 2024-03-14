package main.ViewModule;

import com.toedter.calendar.JDateChooser;
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class AddTimeSeries extends TemplateAbstractFrame implements ListListener {
    private JPanel rootPanel;
    private JPanel titlePanel;
    private JComboBox<String> comboListOfGeo;
    private JComboBox<String> comboListOf;
    private JCheckBox activateCityCheckBox;
    private JButton btnAdd;
    private JButton btnClose;
    private JPanel calendarPanel;
    private JPanel toDatePanel;
    private JPanel FromDatePanel;

    private static AddTimeSeries instance;

    private final MainView parentFrame;
    private JDateChooser dateFrom = new JDateChooser();
    private JDateChooser dateTo = new JDateChooser();
    private DbInterface db = MySQLAccess.getInstance();
    private Map<String, List<String>> map;
    private List<ListListener> listeners;
    private TimeSeries listOfTimeSeries;


    public AddTimeSeries(MainView parentFrame) {
        super("Add - Time series");

        this.parentFrame = parentFrame;
        listeners = new ArrayList<>();
        //set the map to the values
        map = viewModuleInterface.getAllLocations();
        //reset The dates;
        resetDates();


        dateFrom.setDateFormatString("MMM-yy");
        dateTo.setDateFormatString("MMM-yy");
        this.FromDatePanel.add(dateFrom);
        this.toDatePanel.add(dateTo);

        configureDateFromListener();


        // Disable the value JComboBox initially
        comboListOf.setEnabled(false);


        getContentPane().add(rootPanel);
        setSize(550, 400);
        pack();

        addListListener(parentFrame);
        // Populate the JComboBox with keys from the map
        comboListOfGeo.setModel(new DefaultComboBoxModel<>(map.keySet().toArray(new String[0])));

        // WindowListener to handle the window closing event
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // When the window is closing, set the parent frame visible
                parentFrame.setVisible(true);
                dispose(); // Dispose of the StatisticView
            }
        });
        comboListOfGeo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateValueComboBox();
            }
        });

        activateCityCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Enable the value JComboBox when the checkbox is checked
                comboListOf.setEnabled(activateCityCheckBox.isSelected());
                updateValueComboBox();
            }
        });
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTimeSeries();
            }
        });
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // When the button is clicked, set the parent frame visible
                parentFrame.setVisible(true);
                dispose(); // Dispose of the AddTimeSeries
            }
        });
    }

    private String formatDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM-yy", Locale.ENGLISH);
        return formatter.format(date);
    }


    private void setMinAndMaxDate() throws ParseException {
        String sDate1 = "Jan-1981";
        String sDate2 = "Dec-2022";
        SimpleDateFormat formatter1 = new SimpleDateFormat("MMM-yy", Locale.ENGLISH);
        Date minDate = formatter1.parse(sDate1);
        Date maxDate = formatter1.parse(sDate2);

        this.dateTo.setMaxSelectableDate(maxDate);
        this.dateTo.setMinSelectableDate(minDate);
        this.dateFrom.setMinSelectableDate(minDate);
        this.dateFrom.setMaxSelectableDate(maxDate);

        // Set the initial dates
        this.dateFrom.setDate(minDate);
        this.dateTo.setDate(maxDate);
    }

    private void resetDates() {
        try {
            setMinAndMaxDate();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }


    private void addTimeSeries() {
        String geo = (String) comboListOfGeo.getSelectedItem();
        String city = null;
        if (activateCityCheckBox.isSelected()) {
            city = (String) comboListOf.getSelectedItem();
        }
        String fromDate = null;
        if (dateFrom.getDate() != null) {
            fromDate = formatDate(dateFrom.getDate());
        }
        String toDate = null;
        if (dateTo.getDate() != null) {
            toDate = formatDate(dateTo.getDate());
        }
        String location = (city == null || city.isEmpty()) ? geo : city + ", " + geo;
        TimeSeries newTimeSeries = new TimeSeries(location, fromDate, toDate);

        if (timeSeriesCache.contains(newTimeSeries)) {
            JOptionPane.showMessageDialog(rootPanel, "Time series already exists in the list.", "Warning!", JOptionPane.WARNING_MESSAGE);
        } else {
            timeSeriesCache.add(newTimeSeries);
            this.listOfTimeSeries = newTimeSeries;
            notifyListListeners();
            JOptionPane.showMessageDialog(rootPanel, "Time series added successfully.", "Success  ✔ ", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            resetDates(); // Reset the date fields to their initial values
        }
    }


    private void configureDateFromListener() {
        dateFrom.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("date".equals(evt.getPropertyName())) {
                    Date fromDate = dateFrom.getDate();
                    if (fromDate != null) {
                        dateTo.setMinSelectableDate(fromDate);
                    }
                }
            }
        });
    }

    // Override the processWindowEvent method to handle the window closing event
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            // When the window is closing, set the parent frame visible
            parentFrame.setVisible(true);
            dispose(); // Dispose of the AddTimeSeries
        }
    }

    public static AddTimeSeries getInstance(MainView parentFrame) {
        if (instance == null)
            instance = new AddTimeSeries(parentFrame);

        return instance;
    }

    private void clearFields() {
        comboListOfGeo.setSelectedIndex(0);
        activateCityCheckBox.setSelected(false);
        // Enable the value JComboBox when the checkbox is checked
        comboListOf.setEnabled(activateCityCheckBox.isSelected());
        dateFrom.setDate(null);
        dateTo.setDate(null);
    }

    private void updateValueComboBox() {
        String selectedKey = (String) comboListOfGeo.getSelectedItem();
        List<String> values = map.get(selectedKey);
        comboListOf.removeAllItems();
        if (activateCityCheckBox.isSelected()) {
            for (String value : values) {
                comboListOf.addItem(value);
            }
        }
    }


    @Override
    public void onListChanged(TimeSeries list) {

    }

    // Add a new listener to the list of listeners
    public void addListListener(ListListener listener) {
        listeners.add(listener);
    }

    // Notify all listeners that the list has changed
    public void notifyListListeners() {
        for (ListListener listener : listeners) {
            listener.onListChanged(listOfTimeSeries);
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
        rootPanel.setPreferredSize(new Dimension(450, 300));
        titlePanel = new JPanel();
        titlePanel.setLayout(new BorderLayout(0, 0));
        rootPanel.add(titlePanel, BorderLayout.NORTH);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        panel1.setPreferredSize(new Dimension(350, 200));
        rootPanel.add(panel1, BorderLayout.CENTER);
        calendarPanel = new JPanel();
        calendarPanel.setLayout(new BorderLayout(0, 5));
        calendarPanel.setPreferredSize(new Dimension(225, 188));
        panel1.add(calendarPanel, BorderLayout.EAST);
        calendarPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        calendarPanel.add(panel2, BorderLayout.NORTH);
        FromDatePanel = new JPanel();
        FromDatePanel.setLayout(new BorderLayout(0, 0));
        FromDatePanel.setMinimumSize(new Dimension(150, 50));
        FromDatePanel.setOpaque(true);
        FromDatePanel.setPreferredSize(new Dimension(200, 75));
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(50, 0, 0, 0);
        panel2.add(FromDatePanel, gbc);
        FromDatePanel.setBorder(BorderFactory.createTitledBorder(null, "From Date", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        calendarPanel.add(panel3, BorderLayout.SOUTH);
        toDatePanel = new JPanel();
        toDatePanel.setLayout(new BorderLayout(0, 0));
        toDatePanel.setMinimumSize(new Dimension(150, 50));
        toDatePanel.setPreferredSize(new Dimension(200, 75));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 50, 0);
        panel3.add(toDatePanel, gbc);
        toDatePanel.setBorder(BorderFactory.createTitledBorder(null, "To Date", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridBagLayout());
        panel1.add(panel4, BorderLayout.SOUTH);
        btnAdd = new JButton();
        btnAdd.setText("Add");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(btnAdd, gbc);
        btnClose = new JButton();
        btnClose.setText("Close");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(btnClose, gbc);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridBagLayout());
        panel1.add(panel5, BorderLayout.CENTER);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel5.add(panel6, gbc);
        comboListOfGeo = new JComboBox();
        comboListOfGeo.setPreferredSize(new Dimension(150, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(comboListOfGeo, gbc);
        final JLabel label1 = new JLabel();
        label1.setText("Choose Location");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 5, 0, 0);
        panel6.add(label1, gbc);
        comboListOf = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(comboListOf, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Choose City");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(7, 5, 0, 0);
        panel6.add(label2, gbc);
        activateCityCheckBox = new JCheckBox();
        activateCityCheckBox.setText("add city");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 7, 0);
        panel6.add(activateCityCheckBox, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }


}
