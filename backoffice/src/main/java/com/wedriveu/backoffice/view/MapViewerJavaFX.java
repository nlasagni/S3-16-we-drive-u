package com.wedriveu.backoffice.view;

/**
 * @author Stefano Bernagozzi
 */
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MapViewerJavaFX extends Application {
    BrowserForShowingMap browserForShowingMap;
    private Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        Parameters parameters = getParameters ();
        this.stage = stage;
        browserForShowingMap = new BrowserForShowingMap(parameters.getRaw().get(0));
        Scene scene = new Scene(browserForShowingMap);
        stage.setScene(scene);
        stage.setWidth(630);
        stage.setHeight(630);
        stage.show();
    }

}