package com.kpolak.view;

import javafx.application.Application;
import javafx.stage.Stage;


public class Main extends Application {

    public void start(Stage stage) {
        ViewManager viewManager = new ViewManager(stage);

        stage.setTitle("Displaying Image");
        stage.setScene(viewManager.getScene());
        stage.show();
    }

    public static void main(String[] args) {
        //-Xmx1G -Djava.library.path=C:\Users\P1\Downloads\dcm_opencv_krzychu
        launch(args);
    }
}
