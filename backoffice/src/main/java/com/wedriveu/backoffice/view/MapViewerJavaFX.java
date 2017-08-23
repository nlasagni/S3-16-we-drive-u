package com.wedriveu.backoffice.view;

/**
 * @author Stefano Bernagozzi
 */
import com.wedriveu.services.shared.model.Booking;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;

public class MapViewerJavaFX extends Application {
    Browser browser;
    private Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        Parameters parameters = getParameters ();
        this.stage = stage;
        browser = new Browser(parameters.getRaw().get(0));
        Scene scene = new Scene(browser);
        stage.setScene(scene);
        stage.setWidth(630);
        stage.setHeight(630);
        stage.show();
    }

}