package com.wedriveu.backoffice.view;

import com.wedriveu.shared.rabbitmq.message.VehicleCounter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * @author Stefano Bernagozzi
 */
public class BackOfficeView extends JFrame {
    private int maxX = 1000;
    private int maxY = 500;
    private JLabel textLabel;
    private JTextField textFieldBackofficeId;
    private JButton buttonRegistration;
    private JButton buttonBookings;
    private final int width = 175;
    private String backOffice = "Backoffice";
    private String labelDefaultText = "Loading cars Counters";
    private String textFieldDefaultText = "backoffice1";
    private String buttonRegistrationText = "Register backoffice";
    private String buttonBookingsText = "Get bookings positions";

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

        textFieldBackofficeId = new JTextField(textFieldDefaultText);
        textFieldBackofficeId.setPreferredSize(new Dimension(width, 50));
        getContentPane().add(textFieldBackofficeId, BorderLayout.CENTER);

        buttonRegistration = new JButton(buttonRegistrationText);
        buttonRegistration.setPreferredSize(new Dimension(width, 50));
        getContentPane().add(buttonRegistration, BorderLayout.PAGE_END);

        buttonBookings = new JButton(buttonBookingsText);
        buttonBookings.setPreferredSize(new Dimension(width, 50));

        //Display the window.
        pack();
        setVisible(true);
    }

    public void updateText(VehicleCounter vehicleCounter) {
        textLabel.setText("<html><body>" +
                "Available: " + vehicleCounter.getAvailable() + "<br>" +
                "Broken: " + vehicleCounter.getBrokenStolen() + "<br>" +
                "Booked: " + vehicleCounter.getBooked() + "<br>" +
                "Recharging: " + vehicleCounter.getRecharging() + "<br>" +
                "Network Issues: " + vehicleCounter.getNetworkIssues() + "<br>" +
                "</body></html>");
    }

    public String getTextBoxContent() {
        return textFieldBackofficeId.getText();
    }

    public void disableButtonAndText() {
        getContentPane().remove(buttonRegistration);
        getContentPane().remove(textFieldBackofficeId);
        getContentPane().add(buttonBookings, BorderLayout.PAGE_END);
        getContentPane().revalidate();
        getContentPane().repaint();
    }

    public void addButtonListener(ActionListener actionListener) {
        buttonRegistration.addActionListener(actionListener);
    }

}
