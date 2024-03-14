package main.ViewModule;

import main.Controller.Controller;
import main.Controller.TimeSeries;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TemplateAbstractFrame extends JFrame {
    protected static Map<String, List<Double>> forecastData;
    protected ViewModuleInterface viewModuleInterface;
    protected static Set<TimeSeries> timeSeriesCache = new HashSet<>();//Will help checking for already added time series


    public TemplateAbstractFrame(String title) {
        super(title);
        // Set the Nimbus look and feel
        setNimbusLookAndFeel();
        viewModuleInterface = new Controller();
    }

    // Method to set the Nimbus look and feel
    private void setNimbusLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Nimbus look and feel not found");
        }
    }

    private void addLogo() {
        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new BorderLayout());

        logoPanel.add(getLogoLabel(), BorderLayout.NORTH);
        add(logoPanel, BorderLayout.WEST);
    }


    protected void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    protected static JLabel getLogoLabel() {
        Font font = new Font("Arial", Font.BOLD, 24);
        String logoText = "<html><font size=\"6\" color=\"black\"><b>Estate Analytics</b></font><br/><font size=\"4\" color=\"gray\">Your partner in Real Estate Analysis</font></html>";
        JLabel logoTextLabel = new JLabel(logoText);
        logoTextLabel.setFont(font);
        return logoTextLabel;
    }

}