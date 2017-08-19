package com.wedriveu.backoffice.controller;

import com.wedriveu.backoffice.model.BackOfficeModel;
import com.wedriveu.backoffice.view.BackOfficeView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Stefano Bernagozzi
 */
public class ButtonEventListener implements ActionListener{
    BackOfficeModel backOfficeModel;
    BackOfficeView backOfficeView;

    public ButtonEventListener(BackOfficeModel backOfficeModel, BackOfficeView backOfficeView){
        this.backOfficeModel = backOfficeModel;
        this.backOfficeView = backOfficeView;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        backOfficeModel.setBackofficeID(backOfficeView.getTextBoxContent());
        backOfficeView.disableButtonAndText();
    }

}