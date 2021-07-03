package com.kpolak.view;

import com.kpolak.model.Dicom;
import com.kpolak.reader.DicomReader;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
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
//        dicom = dcm.readDicomFromFile("C:\\Users\\P1\\Downloads\\EnhancedCT\\EnhancedCT_Anon.dcm");
//        dicom = dcm.readDicomFromFile("C:\\Users\\P1\\Desktop\\praca_inzynierska\\zdjecia3\\manifest-1608266677008\\MIDRC-RICORD-1A\\MIDRC-RICORD-1A-419639-000082\\08-02-2002-CT CHEST WITHOUT CONTRAST-04614\\604.000000-COR 3X3-11320\\1-042.dcm");

//        dicom = dcm.readDicomFromFile("C:\\Users\\P1\\Desktop\\praca_inzynierska\\zdjecia\\manifest-1557326747206\\LCTSC\\LCTSC-Test-S1-101\\03-03-2004-08186\\79262\\1-001.dcm");
        //dicom = dcm.readDicomFromFile("C:\\Users\\P1\\Desktop\\praca_inzynierska\\zdjecia3\\manifest-1608266677008\\MIDRC-RICORD-1A\\MIDRC-RICORD-1A-419639-000082\\08-02-2002-CT CHEST WITHOUT CONTRAST-04614\\605.000000-SAG 3X3-10651\\1-001.dcm");
//        dicom = dcm.readDicomFromFile("C:\\Users\\P1\\Desktop\\praca_inzynierska\\zdjecia3\\manifest-1608266677008\\MIDRC-RICORD-1A\\MIDRC-RICORD-1A-419639-000082\\08-02-2002-CT CHEST WITHOUT CONTRAST-04614\\605.000000-SAG 3X3-10651\\1-002.dcm");

        dcm.readDicomFilesInDirectory("C:\\Users\\P1\\Desktop\\praca_inzynierska\\zdjecia3\\manifest-1608266677008\\MIDRC-RICORD-1A\\MIDRC-RICORD-1A-419639-000082\\08-02-2002-CT CHEST WITHOUT CONTRAST-04614\\604.000000-COR 3X3-11320");
        dicom = dcm.getRootNode().flatTree().get(0);


        mainDisplay = new MainDisplay(dicom);

        Pane root = (Pane) mainDisplay.getRoot();
        root.setBackground((new Background(
                new BackgroundFill(Color.rgb(0, 0, 50), CornerRadii.EMPTY, Insets.EMPTY))));
        StackPane stack = new StackPane(root);
        stack.setBackground((new Background(
                new BackgroundFill(Color.rgb(50, 0, 50), CornerRadii.EMPTY, Insets.EMPTY))));
        AnchorPane anchorPane = new AnchorPane();

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(stack);
        BorderPane.setAlignment(stack, Pos.CENTER);


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
        //-Xmx1G -Djava.library.path=C:\Users\P1\Downloads\dcm_opencv_krzychu
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
