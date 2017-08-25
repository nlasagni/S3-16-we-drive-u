package com.wedriveu.backoffice.view;

import com.wedriveu.backoffice.util.ConstantsBackOffice;
import com.wedriveu.shared.util.Log;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * @author Stefano Bernagozzi
 */
class BrowserForShowingMap extends Pane {

    WebView webView = new WebView();
    WebEngine webEngine = webView.getEngine();

    public BrowserForShowingMap(String javascriptCode) {
        String webPage = ConstantsBackOffice.WebPage.HTML_WEB_PAGE_BEFORE_JAVASCRIPT_SCRIPT +
                javascriptCode +
                ConstantsBackOffice.WebPage.HTML_WEB_PAGE_AFTER_JAVASCRIPT_SCRIPT;
        webEngine.loadContent(webPage);
        webEngine.setOnAlert(e -> Log.error(this.getClass().getSimpleName(), e.toString()));
        getChildren().add(webView);
    }
}
