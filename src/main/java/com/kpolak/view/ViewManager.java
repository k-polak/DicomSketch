package com.kpolak.view;

import com.kpolak.model.dicom.Dicom;
import com.kpolak.reader.DicomReader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ViewManager {
    MainDisplay currentMainDisplay;
    Scene scene;
    Stage stage;
    DicomReader dicomReader;
    BorderPane borderPane;
    LeftSideThumbnailContainer leftSideThumbnailContainer;
    List<MainDisplay> mainDisplays;
    PopupWindow popupWindow;

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
        popupWindow = new PopupWindow(scene);
        leftSideThumbnailContainer = new LeftSideThumbnailContainer(dicomReader, this);
        mainDisplays = new ArrayList<>();

        borderPane.setLeft(leftSideThumbnailContainer);
        borderPane.setCenter(getEmptyMainWindow());
        borderPane.setTop(createMenu());
        borderPane.setFocusTraversable(false);
        borderPane.setStyle(StyleConstants.BACKGROUND_COLOR);
        registerKeyActions();
    }

    private void registerKeyActions() {
        scene.setOnKeyPressed(this::handleKeyPressed);
    }

    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.DELETE) {
            currentMainDisplay.handleDelete();
        }

        if (event.getCode() == KeyCode.C) {
            currentMainDisplay.fitWindow();
        }
    }

    private HBox getBottomButtons() {
        HBox bottom = new HBox();
        bottom.setAlignment(Pos.CENTER);
        bottom.getChildren().addAll(getButtons());
        bottom.setStyle(StyleConstants.BACKGROUND_COLOR);
        bottom.setSpacing(10.0);
        bottom.setPadding(new Insets(0,0,0, 135));
        return bottom;
    }

    public MainDisplay updateOrCreateMainDisplay(Dicom newDicom) {
        Optional<MainDisplay> mainDisplay = getMainDisplayByDicom(newDicom);
        mainDisplay.ifPresent(display -> display.loadAnotherFrame(newDicom));
        return mainDisplay.orElseGet(() -> createMainDisplay(newDicom));
    }

    public MainDisplay createMainDisplay(Dicom newDicom) {
        MainDisplay mainDisplay = new MainDisplay(newDicom, popupWindow);
        mainDisplays.add(mainDisplay);
        return mainDisplay;
    }

    private Optional<MainDisplay> getMainDisplayByDicom(Dicom dicom) {
        return mainDisplays.stream()
                .filter(display -> display.getDicom().getPatient().equals(dicom.getPatient()) &&
                        display.getDicom().getSeries().equals(dicom.getSeries()) &&
                        display.getDicom().getStudy().equals(dicom.getStudy()))
                .findFirst();
    }

    public void thumbnailSelected(DicomThumbnail thumbnail) {
        MainDisplay mainDisplay = mainDisplays.stream()
                .filter(display -> display.getDicom().getPatient().equals(thumbnail.getPatient()) &&
                        display.getDicom().getSeries().equals(thumbnail.getSeries()) &&
                        display.getDicom().getStudy().equals(thumbnail.getStudy()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Couldn't find main window associated with clicked thumbnail"));
        setCurrentMainDisplay(mainDisplay);
    }

    private void setCurrentMainDisplay(MainDisplay display) {
        currentMainDisplay = display;
        display.setBackground((new Background(
                new BackgroundFill(Color.rgb(0, 0, 0), CornerRadii.EMPTY, Insets.EMPTY))));
        StackPane stack = new StackPane(display);
        stack.setFocusTraversable(false);
        stack.setStyle(StyleConstants.MAIN_DISPLAY_CONTAINER_STYLE);
        borderPane.setCenter(stack);
        BorderPane.setAlignment(stack, Pos.CENTER);
    }

    private List<Button> getButtons() {
        Button nextFrameButton = new Button("Next");
        nextFrameButton.setFocusTraversable(false);
        nextFrameButton.setOnMouseClicked(e -> currentMainDisplay.nextFrame());

        Button previousFrameButton = new Button("Previous");
        previousFrameButton.setFocusTraversable(false);
        previousFrameButton.setOnMouseClicked(e -> currentMainDisplay.previousFrame());

        Button exportCurvesButton = new Button("Export curves");
        exportCurvesButton.setFocusTraversable(false);
        exportCurvesButton.setOnMouseClicked(e -> currentMainDisplay.exportCurves());

        List<Button> buttons = Arrays.asList(previousFrameButton, nextFrameButton, exportCurvesButton);
        buttons.forEach(button -> button.setStyle(StyleConstants.BUTTON_STYLE));
        return buttons;
    }

    private HBox createMenu() {
        MenuBar menuBar = new MenuBar();
        HBox menuBarContainer = new HBox(menuBar);
        HBox.setHgrow(menuBar, Priority.ALWAYS);

        menuBar.setStyle(StyleConstants.MENU_BACKGROUND);
        Menu menu = new Menu("File");

        menu.getItems().add(getOpenDicomMenuItem());
        menu.getItems().add(getOpenDicomDirMenuItem());

        menuBar.getMenus().add(menu);
        return menuBarContainer;
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
            boolean isFirstLoad = dicomReader.getRootNode().flatTree().isEmpty();
            Dicom selectedDicom = dicomReader.readDicomFromFile(dicomFile.getAbsolutePath());
//            TODO: Do not rebuild thumbnail container if it isn't necessary
            leftSideThumbnailContainer.buildThumbnailContainer();
            if(isFirstLoad) {
                borderPane.setBottom(getBottomButtons());
            }
            MainDisplay mainDisplay = updateOrCreateMainDisplay(selectedDicom);
            setCurrentMainDisplay(mainDisplay);
        }
    }

    private void handleOpenDicomDir(File dir) {
        if (dir != null) {
            boolean isFirstLoad = dicomReader.getRootNode().flatTree().isEmpty();
            dicomReader.readDicomFilesInDirectory(dir.getAbsolutePath());
            leftSideThumbnailContainer.buildThumbnailContainer();
            createMainWindowsForNewDicoms();

            if (isFirstLoad) {
                Optional<MainDisplay> mainDisplay = getMainDisplayByDicom(dicomReader.getRootNode().flatTree().get(0));
                if (mainDisplay.isPresent()) {
                    borderPane.setBottom(getBottomButtons());
                    setCurrentMainDisplay(mainDisplay.get());
                } else {
                    throw new RuntimeException("Couldn't find main display after first load");
                }
            }
        }
    }

    private void createMainWindowsForNewDicoms() {
        dicomReader.getRootNode().flatTree().stream()
                .filter(dicom -> !getMainDisplayByDicom(dicom).isPresent())
                .forEach(this::createMainDisplay);
    }

    private StackPane getEmptyMainWindow() {
        StackPane stackPane = new StackPane();
        Text text = new Text("Please select file to display");
        text.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 20));
        text.setFill(Color.WHITE);
        stackPane.getChildren().add(text);
        return stackPane;
    }
}
