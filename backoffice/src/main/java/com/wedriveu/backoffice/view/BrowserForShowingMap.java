package com.wedriveu.backoffice.view;

import com.wedriveu.backoffice.util.ConstantsBackOffice;
import com.wedriveu.shared.util.Log;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * a browser for showing a google map with a javascript code inside
 *
 * @author Stefano Bernagozzi
 */
class BrowserForShowingMap extends Pane {

    WebView webView = new WebView();
    WebEngine webEngine = webView.getEngine();

    /**
     * starts a new browser with google maps with the javascript code  included in the parameter
     * @param javascriptCode the javascript code for the web page
     */
    public BrowserForShowingMap(String javascriptCode) {
        String webPage = String.format(
                ConstantsBackOffice.WebPage.HTML_WEB_PAGE_CODE ,
                javascriptCode);
        webEngine.loadContent(webPage);
        webEngine.setOnAlert(e -> Log.error(this.getClass().getSimpleName(), e.toString()));
        getChildren().add(webView);
    }
}
