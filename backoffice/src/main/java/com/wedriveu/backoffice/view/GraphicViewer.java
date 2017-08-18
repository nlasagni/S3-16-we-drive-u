package com.wedriveu.backoffice.view;

import com.wedriveu.shared.rabbitmq.message.VehicleCounter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * @author Stefano Bernagozzi
 */
public class GraphicViewer extends JFrame{
    private int maxX = 1000;
    private int maxY = 500;
    private JLabel textLabel;
    private JTextField textField;
    private JButton button;

    public GraphicViewer() {
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        //Create and set up the window.
        setTitle("FrameDemo");
        setSize(maxX, maxY);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        textLabel = new JLabel("Loading cars Counters");
        textLabel.setPreferredSize(new Dimension(175, 120));
        getContentPane().add(textLabel, BorderLayout.PAGE_START);

        textField = new JTextField("backoffice1");
        textField.setPreferredSize(new Dimension(175, 50));
        getContentPane().add(textField, BorderLayout.CENTER);

        button = new JButton("Register backoffice");
        button.setPreferredSize(new Dimension(175, 50));
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
                "Stolen: " + vehicleCounter.getStolen() + "<br>" +
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
