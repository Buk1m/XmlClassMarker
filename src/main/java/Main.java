import javafx.application.Application;
import javafx.stage.Stage;
import models.MainWindowModel;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        new MainWindowModel(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
