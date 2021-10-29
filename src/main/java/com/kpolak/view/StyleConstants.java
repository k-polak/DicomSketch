package com.kpolak.view;

class StyleConstants {
    private StyleConstants() {
    }

    static final String BACKGROUND_COLOR = "-fx-background-color: #77797d;";
    static final String MENU_BACKGROUND = "-fx-background-color: #ffffff;";
    static final String BORDER_COLOR = "-fx-background-color: #1e1e1f;";
    static final String BUTTON_STYLE = "-fx-background-color: " +
            "        #1e1e1f," +
            "        linear-gradient(#38424b 0%, #1f2429 20%, #191d22 100%)," +
            "        linear-gradient(#20262b, #191d22)," +
            "        radial-gradient(center 50% 0%, radius 100%, rgba(114,131,148,0.9), rgba(255,255,255,0));" +
            "    -fx-background-radius: 5,4,3,5;" +
            "    -fx-background-insets: 0,1,2,0;" +
            "    -fx-text-fill: white;" +
            "    -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );" +
            "    -fx-font-family: Arial;" +
            "    -fx-text-fill: linear-gradient(white, #d0d0d0);" +
            "    -fx-font-size: 14px;" +
            "    -fx-padding: 10 20 10 20";
    static final String MAIN_DISPLAY_CONTAINER_STYLE = BACKGROUND_COLOR +
            "-fx-background-radius: 5;" +
            "-fx-padding: 10;" +
            "-fx-border-style: solid inside;" +
            "-fx-border-width: 6;" +
            "-fx-border-radius: 6;" +
            "-fx-border-color: #1e1e1f;";

    static final String LEFT_SIDE_CONTAINER_STYLE = BACKGROUND_COLOR +
            "-fx-background-radius: 5;" +
            "-fx-padding: 10;" +
            "-fx-border-style: solid inside;" +
            "-fx-border-width: 6;" +
            "-fx-border-radius: 2;" +
            "-fx-border-color: #1e1e1f;";
}
