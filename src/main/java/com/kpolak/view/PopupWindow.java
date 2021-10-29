package com.kpolak.view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

class PopupWindow {
    private static final int POPUP_WIDTH = 400;
    private static final int POPUP_HEIGHT = 200;

    private Scene scene;

    public PopupWindow(Scene scene) {
        this.scene = scene;
    }

    public void showPopup(String message) {
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.initOwner(scene.getWindow());
        stage.setScene(new Scene(buildBorderPane(stage, message), POPUP_WIDTH, POPUP_HEIGHT));
        stage.setResizable(false);
        stage.show();
    }

    private BorderPane buildBorderPane(Stage stage, String message) {
        BorderPane borderPane = new BorderPane();
        StackPane stackPane = new StackPane();
        HBox hbox = new HBox();
        Button closeButton = new Button("Ok");

        borderPane.setStyle(StyleConstants.BACKGROUND_COLOR);

        Text textMessage = new Text(message);
        textMessage.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 20));
        textMessage.setFill(Color.WHITE);

        stackPane.getChildren().add(textMessage);

        hbox.getChildren().add(closeButton);
        hbox.setAlignment(Pos.CENTER);

        borderPane.setCenter(stackPane);
        borderPane.setBottom(hbox);

        closeButton.setOnMouseClicked(e -> stage.close());
        closeButton.setFocusTraversable(false);
        closeButton.setStyle(StyleConstants.BUTTON_STYLE);
        return borderPane;
    }
}
