package com.wedriveu.backoffice.view;

import com.wedriveu.backoffice.util.ConstantsBackOffice;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.*;

/**
 *class for showing a google map into a swing frame
 *
 * @author Stefano Bernagozzi
 */
public class MapViewerJavaFX {
    BrowserForShowingMap browserForShowingMap;
    private Stage stage;

    private static void initAndShowGUI(String javascriptCode) {
        JFrame frame = new JFrame(ConstantsBackOffice.BROWSER_TITLE);
        final JFXPanel fxPanel = new JFXPanel();
        frame.add(fxPanel);
        frame.setSize(630,680);
        frame.setVisible(true);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initFX(fxPanel, javascriptCode);
            }
        });
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
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                initAndShowGUI(javascriptCode);
            }
        });
    }
}