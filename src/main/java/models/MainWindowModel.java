package models;

import data.MarkerDto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.Getter;

@Getter
public class MainWindowModel extends BaseModel {

    private static final String fxmlMainWindowFileName = "fxml/MainWindow.fxml";

    private ObservableList<MarkerDto> markers = FXCollections.observableArrayList();
    private Stage stage;

    public MainWindowModel(Stage primaryStage) {
        stage = primaryStage;
        createScene(primaryStage, fxmlMainWindowFileName);

        initMarkerButtons();
    }

    private void initMarkerButtons() {
        markers.add(new MarkerDto("CLEAR", "Wyczyść", Color.WHITE, "CTRL-Q"));
    }

    void configureWindow(Stage stage, Scene scene) {
        stage.setTitle("Xml marker");
        stage.setWidth(800);
        stage.setHeight(475);
        stage.setMinWidth(700);
        stage.setMinHeight(450);

        stage.show();
    }
}
