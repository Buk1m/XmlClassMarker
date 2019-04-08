package models;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainWindowModel extends BaseModel {

    private static final String fxmlMainWindowFileName = "fxml/MainWindow.fxml";
    private Stage stage;

    public MainWindowModel(Stage primaryStage) {
        stage = primaryStage;
        createScene(primaryStage, fxmlMainWindowFileName);
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
