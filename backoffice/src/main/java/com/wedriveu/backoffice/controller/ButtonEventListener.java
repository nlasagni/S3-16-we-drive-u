package com.wedriveu.backoffice.controller;

import com.wedriveu.backoffice.model.BackOfficeModel;
import com.wedriveu.backoffice.view.GraphicViewer;
import io.vertx.core.Future;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventListener;
/**
 * @author Stefano Bernagozzi
 */
public class ButtonEventListener implements ActionListener{
    BackOfficeModel backOfficeModel;
    GraphicViewer graphicViewer;

    public ButtonEventListener(BackOfficeModel backOfficeModel, GraphicViewer graphicViewer){
        this.backOfficeModel = backOfficeModel;
        this.graphicViewer = graphicViewer;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        backOfficeModel.setBackofficeID(graphicViewer.getTextBoxContent());
        graphicViewer.disableButtonAndText();
    }

}