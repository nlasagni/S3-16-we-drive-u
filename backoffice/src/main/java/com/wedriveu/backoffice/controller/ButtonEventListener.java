package com.wedriveu.backoffice.controller;

import com.wedriveu.backoffice.model.BackOfficeModel;
import com.wedriveu.backoffice.view.BackOfficeView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The button event listener for the registration of the backoffice
 *
 * @author Stefano Bernagozzi
 */
public class ButtonEventListener implements ActionListener {
    BackOfficeModel backOfficeModel;
    BackOfficeView backOfficeView;

    /**
     * the button event listener for the registration button
     *
     * @param backOfficeModel an instance of the backoffice model for updating it
     * @param backOfficeView an instance of the backoffice view for updating it
     */
    
    public ButtonEventListener(BackOfficeModel backOfficeModel, BackOfficeView backOfficeView) {
        this.backOfficeModel = backOfficeModel;
        this.backOfficeView = backOfficeView;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        backOfficeModel.setBackofficeID(backOfficeView.getTextBoxContent());
        backOfficeView.disableButtonAndText();
    }

}