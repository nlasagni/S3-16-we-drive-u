package com.wedriveu.backoffice.util;

/**
 * @author Stefano Bernagozzi
 */
public interface Constants {
    String HTML_WEB_PAGE_BEFORE_JAVASCRIPT_SCRIPT = "<!DOCTYPE html>" +
            "<html>" +
            "  <head>" +
            "    <style>" +
            "       #map {" +
            "        height: 600px;" +
            "        width: 600px;" +
            "       }" +
            "    </style>" +
            "  </head>" +
            "  <body>" +
            "    <div id=\"map\"></div>" +
            "    <script>" +
            "      function initMap() {" +
            "        var cesena = {lat: 44.1391000, lng: 12.2431500};" +
            "        var map = new google.maps.Map(document.getElementById('map'), {" +
            "          zoom: 4," +
            "          center: cesena" +
            "        });" ;
    String HTML_WEB_PAGE_AFTER_JAVASCRIPT_SCRIPT =
            "}" +
            "    </script>" +
            "    <script async defer" +
            "    src=\"https://maps.googleapis.com/maps/api/js?key=AIzaSyBDwD2anM-m3WG705nwIrvyMuYGoWpLlBY&callback=initMap\">" +
            "    </script>" +
            "  </body>" +
            "</html>";

}
