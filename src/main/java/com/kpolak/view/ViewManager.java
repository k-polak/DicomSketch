package com.kpolak.view;

import com.kpolak.model.Dicom;
import com.kpolak.reader.DicomReader;
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
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ViewManager {
    MainDisplay mainDisplay;
    Scene scene;
    Stage stage;
    DicomReader dicomReader;
    BorderPane borderPane;
    LeftSideThumbnailContainer leftSideThumbnailContainer;

    public ViewManager(Stage stage) {
        this.stage = stage;
        init();
    }


    public Scene getScene() {
        return scene;
    }

    public void init() {
        dicomReader = new DicomReader();
        borderPane = new BorderPane();
        scene = new Scene(borderPane, 1200, 1000);
        leftSideThumbnailContainer = new LeftSideThumbnailContainer(dicomReader, this);

        borderPane.setBottom(getBottomButtons());
        borderPane.setLeft(leftSideThumbnailContainer);
        borderPane.setCenter(getEmptyMainWindow());
        borderPane.setTop(createMenu());
    }

    private HBox getBottomButtons() {
        HBox bottom = new HBox();
        bottom.setAlignment(Pos.CENTER);
        bottom.getChildren().addAll(getButtons());
        return bottom;
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
        MenuBar menuBar = new MenuBar();
        VBox vBox = new VBox(menuBar);
        Menu menu = new Menu("File");

        menu.getItems().add(getOpenDicomMenuItem());
        menu.getItems().add(getOpenDicomDirMenuItem());

        menuBar.getMenus().add(menu);
        return vBox;
    }

    private MenuItem getOpenDicomMenuItem() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter dicomExtensionFilter = new FileChooser.ExtensionFilter("DICOM file", "*.dcm", "*.dicom");
        fileChooser.getExtensionFilters().add(dicomExtensionFilter);

        MenuItem openDicom = new MenuItem("Open DICOM");
        openDicom.setOnAction(event -> {
            File selectedFile = fileChooser.showOpenDialog(stage);
            handleSingleFileChosen(selectedFile);
        });
        return openDicom;
    }

    private MenuItem getOpenDicomDirMenuItem() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        MenuItem openDicomDir = new MenuItem("Open DICOM folder");

        openDicomDir.setOnAction(event -> {
            File selectedDir = directoryChooser.showDialog(stage);
            handleOpenDicomDir(selectedDir);
        });
        return openDicomDir;
    }

    private void handleSingleFileChosen(File dicomFile) {
        if (dicomFile != null) {
            Dicom selectedDicom = dicomReader.readDicomFromFile(dicomFile.getAbsolutePath());
            leftSideThumbnailContainer.buildThumbnailContainer();
            displayDicom(selectedDicom);
        }
    }

    private void handleOpenDicomDir(File dir) {
        if (dir != null) {
            boolean isFirstLoad = dicomReader.getRootNode().flatTree().isEmpty();
            dicomReader.readDicomFilesInDirectory(dir.getAbsolutePath());
            leftSideThumbnailContainer.buildThumbnailContainer();
            if (isFirstLoad) {
                displayDicom(dicomReader.getRootNode().flatTree().get(0));
            }
        }
    }

    private StackPane getEmptyMainWindow() {
        StackPane stackPane = new StackPane();
        Text text = new Text("Please select file to display");
        stackPane.getChildren().add(text);
        return stackPane;
    }
}
