package com.wedriveu.backoffice.view;

import com.wedriveu.backoffice.util.Constants;
import com.wedriveu.services.shared.model.Booking;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.util.List;

/**
 * @author Stefano Bernagozzi
 */
class Browser extends Pane {

    WebView webView = new WebView();
    WebEngine webEngine = webView.getEngine();

    public Browser(String javascriptCode) {
        String webPage = Constants.HTML_WEB_PAGE_BEFORE_JAVASCRIPT_SCRIPT +
                javascriptCode +
                Constants.HTML_WEB_PAGE_AFTER_JAVASCRIPT_SCRIPT;
        //TODO
        System.out.println(webPage);
        webEngine.loadContent(webPage);
        webEngine.setOnAlert(e -> System.out.println(e.toString()));
        getChildren().add(webView);
    }
}
