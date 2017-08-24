package com.wedriveu.backoffice.view;

import com.wedriveu.backoffice.util.ConstantsBackoffice;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * @author Stefano Bernagozzi
 */
class Browser extends Pane {

    WebView webView = new WebView();
    WebEngine webEngine = webView.getEngine();

    public Browser(String javascriptCode) {
        String webPage = ConstantsBackoffice.WebPage.HTML_WEB_PAGE_BEFORE_JAVASCRIPT_SCRIPT +
                javascriptCode +
                ConstantsBackoffice.WebPage.HTML_WEB_PAGE_AFTER_JAVASCRIPT_SCRIPT;
        webEngine.loadContent(webPage);
        webEngine.setOnAlert(e -> System.out.println(e.toString()));
        getChildren().add(webView);
    }
}
