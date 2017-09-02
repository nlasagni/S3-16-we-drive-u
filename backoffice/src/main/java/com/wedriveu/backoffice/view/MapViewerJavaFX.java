package com.wedriveu.backoffice.view;

import com.wedriveu.backoffice.util.ConstantsBackOffice;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;

import javax.swing.*;

/**
 *class for showing a google map into a swing frame
 *
 * @author Stefano Bernagozzi
 */
public class MapViewerJavaFX {
    private static final int FRAME_WIDTH = 630;
    private static final int FRAME_HEIGHT = 700;


    private static void initAndShowGUI(String javascriptCode) {
        JFrame frame = new JFrame(ConstantsBackOffice.BROWSER_TITLE);
        final JFXPanel fxPanel = new JFXPanel();
        frame.add(fxPanel);
        frame.setSize(FRAME_WIDTH,FRAME_HEIGHT);
        frame.setVisible(true);

        Platform.runLater(() -> initFX(fxPanel, javascriptCode));
    }

    private static void initFX(JFXPanel fxPanel, String javascriptCode) {
        Scene scene = new Scene(
                new BrowserForShowingMap(javascriptCode));
        fxPanel.setScene(scene);
    }

    /**
     * create a new google map html page with the javscript code inside and puts it on a new frame
     *
     * @param javascriptCode the javascript code that the user wants to insert in the html page
     */
    public static void run(String javascriptCode) {
        SwingUtilities.invokeLater(() -> initAndShowGUI(javascriptCode));
    }
}