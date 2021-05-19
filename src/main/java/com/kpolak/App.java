package com.kpolak;

import com.kpolak.model.Dicom;
import com.kpolak.reader.DicomReader;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;


public class App extends Application {

    int frameNum = 0;
    public void start(Stage stage) {
        DicomReader dcm = new DicomReader();
        Dicom dicom = dcm.readDicomFromFile("C:\\Users\\P1\\Downloads\\EnhancedCT\\EnhancedCT_Anon.dcm");

        ImageView imageView = new ImageView();
        imageView.setX(10);
        imageView.setY(10);
        imageView.setFitWidth(575);
        imageView.setPreserveRatio(true);
        //Setting the Scene object
        Group root = new Group(imageView);
        Scene scene = new Scene(root, 1200, 1000);
        scene.setOnMouseClicked(e -> {
            frameNum = frameNum + 1;
            BufferedImage buffer = dicom.getFrames().get(frameNum);
            imageView.setImage(SwingFXUtils.toFXImage(buffer, null));

        });
        stage.setTitle("Displaying Image");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
