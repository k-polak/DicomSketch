package com.kpolak.view;

import com.kpolak.model.Dicom;
import com.kpolak.reader.DicomReader;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Arrays;
import java.util.List;


public class Main extends Application {
    MainDisplay mainDisplay;
    Scene scene;
    Stage stage;
    DicomReader dicomReader;
    BorderPane borderPane;
    LeftSideThumbnailContainer leftSideThumbnailContainer;

    public void start(Stage stage) {
        this.stage = stage;
        dicomReader = new DicomReader();
//        dicom = dcm.readDicomFromFile("C:\\Users\\P1\\Downloads\\EnhancedCT\\EnhancedCT_Anon.dcm");
//        dicom = dcm.readDicomFromFile("C:\\Users\\P1\\Desktop\\praca_inzynierska\\zdjecia3\\manifest-1608266677008\\MIDRC-RICORD-1A\\MIDRC-RICORD-1A-419639-000082\\08-02-2002-CT CHEST WITHOUT CONTRAST-04614\\604.000000-COR 3X3-11320\\1-042.dcm");

//        dicom = dcm.readDicomFromFile("C:\\Users\\P1\\Desktop\\praca_inzynierska\\zdjecia\\manifest-1557326747206\\LCTSC\\LCTSC-Test-S1-101\\03-03-2004-08186\\79262\\1-001.dcm");
        //dicom = dcm.readDicomFromFile("C:\\Users\\P1\\Desktop\\praca_inzynierska\\zdjecia3\\manifest-1608266677008\\MIDRC-RICORD-1A\\MIDRC-RICORD-1A-419639-000082\\08-02-2002-CT CHEST WITHOUT CONTRAST-04614\\605.000000-SAG 3X3-10651\\1-001.dcm");
//        dicom = dcm.readDicomFromFile("C:\\Users\\P1\\Desktop\\praca_inzynierska\\zdjecia3\\manifest-1608266677008\\MIDRC-RICORD-1A\\MIDRC-RICORD-1A-419639-000082\\08-02-2002-CT CHEST WITHOUT CONTRAST-04614\\605.000000-SAG 3X3-10651\\1-002.dcm");

//        dicomReader.readDicomFilesInDirectory("C:\\Users\\P1\\Desktop\\praca_inzynierska\\zdjecia3\\manifest-1608266677008\\MIDRC-RICORD-1A\\MIDRC-RICORD-1A-419639-000082\\08-02-2002-CT CHEST WITHOUT CONTRAST-04614\\604.000000-COR 3X3-11320");
//        dicom = dicomReader.getRootNode().flatTree().get(0);


        borderPane = new BorderPane();
        scene = new Scene(borderPane, 1200, 1000);

        HBox bottom = new HBox();
        bottom.setAlignment(Pos.CENTER);
        bottom.getChildren().addAll(getButtons());
        borderPane.setBottom(bottom);
        BorderPane.setAlignment(bottom, Pos.CENTER);

        leftSideThumbnailContainer = new LeftSideThumbnailContainer(dicomReader, this);

        borderPane.setLeft(leftSideThumbnailContainer);
        borderPane.setTop(createMenu());
        stage.setTitle("Displaying Image");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        //-Xmx1G -Djava.library.path=C:\Users\P1\Downloads\dcm_opencv_krzychu
        launch(args);
    }

    public void displayDicom(Dicom dicom) {
        mainDisplay = new MainDisplay(dicom);

        Pane root = (Pane) mainDisplay.getRoot();
        root.setBackground((new Background(
                new BackgroundFill(Color.rgb(0, 0, 50), CornerRadii.EMPTY, Insets.EMPTY))));
        StackPane stack = new StackPane(root);
        stack.setBackground((new Background(
                new BackgroundFill(Color.rgb(50, 0, 50), CornerRadii.EMPTY, Insets.EMPTY))));
        borderPane.setCenter(stack);
        BorderPane.setAlignment(stack, Pos.CENTER);
    }


    private List<Button> getButtons() {
        Button nextFrameButton = new Button("Next");
        nextFrameButton.setOnMouseClicked(e -> mainDisplay.nextFrame());

        Button previousFrameButton = new Button("Previous");
        previousFrameButton.setOnMouseClicked(e -> mainDisplay.previousFrame());
        return Arrays.asList(previousFrameButton, nextFrameButton);
    }


    private VBox createMenu() {
        FileChooser fileChooser = new FileChooser();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        FileChooser.ExtensionFilter dicomExtensionFilter = new FileChooser.ExtensionFilter("DICOM file", "*.dcm", "*.dicom");
        fileChooser.getExtensionFilters().add(dicomExtensionFilter);
        MenuBar menuBar = new MenuBar();
        VBox vBox = new VBox(menuBar);

        Menu menu = new Menu("File");
        MenuItem openDicom = new MenuItem("Open DICOM");
        MenuItem openDicomFolder = new MenuItem("Open DICOM folder");

        openDicom.setOnAction(event -> {
            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                Dicom selectedDicom = dicomReader.readDicomFromFile(selectedFile.getAbsolutePath());
                leftSideThumbnailContainer.buildThumbnailContainer();
                displayDicom(selectedDicom);
            }

        });

        openDicomFolder.setOnAction(event -> {
            File selectedDir = directoryChooser.showDialog(stage);
            System.out.println("Opening dicoms at: " + selectedDir.getAbsolutePath());
            if (selectedDir != null) {
                boolean isFirstLoad = dicomReader.getRootNode().flatTree().isEmpty();
                dicomReader.readDicomFilesInDirectory(selectedDir.getAbsolutePath());
                leftSideThumbnailContainer.buildThumbnailContainer();
                if (isFirstLoad) {
                    displayDicom(dicomReader.getRootNode().flatTree().get(0));
                }
            }

        });

        menu.getItems().add(openDicom);
        menu.getItems().add(openDicomFolder);

        menuBar.getMenus().add(menu);
        return vBox;
    }
}
