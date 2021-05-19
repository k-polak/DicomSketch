package com.kpolak.view;

import com.kpolak.model.Dicom;
import com.kpolak.reader.DicomReader;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;


public class Main extends Application {

    int currentFrame = 0;
    Dicom dicom;
    ImageView imageView;
    MainDisplay mainDisplay;

    public void start(Stage stage) {
        DicomReader dcm = new DicomReader();
        dicom = dcm.readDicomFromFile("C:\\Users\\P1\\Downloads\\EnhancedCT\\EnhancedCT_Anon.dcm");
        mainDisplay = new MainDisplay(dicom);

//        imageView = new ImageView();
//        imageView.setX(10);
//        imageView.setY(10);
//        imageView.setFitWidth(575);
//        imageView.setPreserveRatio(true);
        //Setting the Scene object
        Pane root = (Pane) mainDisplay.getRoot();



        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(root);
        BorderPane.setAlignment(root, Pos.CENTER);


        HBox bottom = new HBox();
        bottom.setAlignment(Pos.CENTER);
        bottom.getChildren().addAll(getButtons());
        borderPane.setBottom(bottom);
        BorderPane.setAlignment(bottom, Pos.CENTER);


        Scene scene = new Scene(borderPane, 1200, 1000);
        stage.setTitle("Displaying Image");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }


    private List<Button> getButtons() {
        Button nextFrameButton = new Button("Next");
        nextFrameButton.setOnMouseClicked(e -> mainDisplay.nextFrame());

        Button previousFrameButton = new Button("Previous");
        previousFrameButton.setOnMouseClicked(e -> mainDisplay.previousFrame());
        return Arrays.asList(previousFrameButton, nextFrameButton);
    }
}
