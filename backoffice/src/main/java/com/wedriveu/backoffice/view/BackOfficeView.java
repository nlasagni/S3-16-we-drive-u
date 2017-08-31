package com.wedriveu.backoffice.view;

import com.wedriveu.shared.rabbitmq.message.VehicleCounter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * the view of the backoffice, it sets the graphical user interface
 *
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

    /**
     * creates a new GUI for the backoffice
     */
    public BackOfficeView() {
        createAndShowGUI();
    }

    private void createAndShowGUI() {
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

    /**
     * updates the text in the GUI with the new vehicle counter
     *
     * @param vehicleCounter the new vehicle counter
     */
    public void updateText(VehicleCounter vehicleCounter) {
        textLabel.setText("<html><body>" +
                "Available: " + vehicleCounter.getAvailable() + "<br>" +
                "Broken: " + vehicleCounter.getBrokenStolen() + "<br>" +
                "Booked: " + vehicleCounter.getBooked() + "<br>" +
                "Recharging: " + vehicleCounter.getRecharging() + "<br>" +
                "Network Issues: " + vehicleCounter.getNetworkIssues() + "<br>" +
                "</body></html>");
    }

    /**
     * gets the backoffice id included in the text box
     *
     * @return a string containing the backoffice id
     */
    public String getTextBoxContent() {
        return textFieldBackofficeId.getText();
    }

    /**
     * disables and remove the button and text box of registration and puts a new button for showing vehicles
     */
    public void disableButtonAndText() {
        getContentPane().remove(buttonRegistration);
        getContentPane().remove(textFieldBackofficeId);
        getContentPane().revalidate();
        getContentPane().repaint();
    }

    /**
     * adds a button listener for the registration button
     *
     * @param actionListener An action listener for the registration button
     */
    public void addButtonListener(ActionListener actionListener) {
        buttonRegistration.addActionListener(actionListener);
    }

    /**
     * adds the action listener to the get bookings button
     *
     * @param actionListener the action listener
     */
    public void addButtonBookingListener(ActionListener actionListener) {
        getContentPane().add(buttonBookings, BorderLayout.PAGE_END);
        buttonBookings.addActionListener(actionListener);
        getContentPane().revalidate();
        getContentPane().repaint();
    }
}
