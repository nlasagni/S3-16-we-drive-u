package com.wedriveu.backoffice.view;

import com.wedriveu.shared.rabbitmq.message.VehicleCounter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * @author Stefano Bernagozzi
 */
public class BackOfficeView extends JFrame{
    private int maxX = 1000;
    private int maxY = 500;
    private JLabel textLabel;
    private JTextField textField;
    private JButton button;
    private final int width = 175;
    private String backOffice = "Backoffice";
    private String labelDefaultText = "Loading cars Counters";
    private String textFieldDefaultText = "backoffice1";
    private String buttonText = "Register backoffice";

    public BackOfficeView() {
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        //Create and set up the window.
        setTitle(backOffice);
        setSize(maxX, maxY);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        textLabel = new JLabel(labelDefaultText);
        textLabel.setPreferredSize(new Dimension(width, 120));
        getContentPane().add(textLabel, BorderLayout.PAGE_START);

        textField = new JTextField(textFieldDefaultText);
        textField.setPreferredSize(new Dimension(width, 50));
        getContentPane().add(textField, BorderLayout.CENTER);

        button = new JButton(buttonText);
        button.setPreferredSize(new Dimension(width, 50));
        getContentPane().add(button, BorderLayout.PAGE_END);

        //Display the window.
        pack();
        setVisible(true);
    }

    public void updateText(VehicleCounter vehicleCounter) {
        textLabel.setText("<html><body>" +
                "Available: " + vehicleCounter.getAvailable() + "<br>" +
                "Broken: " + vehicleCounter.getBroken() + "<br>" +
                "Booked: " + vehicleCounter.getBooked() + "<br>" +
                "Recharging: " + vehicleCounter.getRecharging() + "<br>" +
                "Network Issues: " + vehicleCounter.getNetworkIssues() + "<br>" +
                "</body></html>");
    }

    public String getTextBoxContent() {
        return textField.getText();
    }

    public void disableButtonAndText() {
        textField.setVisible(false);
        button.setVisible(false);
    }

    public void addButtonListener(ActionListener actionListener) {
        button.addActionListener(actionListener);
    }

}
