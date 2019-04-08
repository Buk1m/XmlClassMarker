package models;

import java.net.URL;
import java.util.Optional;

import controllers.ModelBindable;
import exceptions.FxmlNotFoundException;
import exceptions.IncorrectModelControllerBindingException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseModel {

    void createScene(Stage stage, String fxmlFileName) {
        Optional<URL> resource = getFXMLResourceFileHandle(fxmlFileName);

        if (resource.isPresent()) {
            Optional<Parent> root = loadFXMLResource(resource.get());

            if (root.isPresent()) {
                Image applicationIcon = new Image("icons/marker.png");
                stage.getIcons().add(applicationIcon);

                Scene scene = new Scene(root.get());
                stage.setScene(scene);

                configureWindow(stage, scene);
            } else {
                log.warn("Resource " + fxmlFileName + " not found");
            }
        }
    }

    Optional<URL> getFXMLResourceFileHandle(String fxmlFileName) {
        URL resource = getClass().getClassLoader().getResource(fxmlFileName);
        if (resource == null) {
            log.error("Resource not found", new FxmlNotFoundException(fxmlFileName));
            return Optional.empty();
        }

        return Optional.of(resource);
    }

    Optional<Parent> loadFXMLResource(URL resource) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(resource);
            Parent root = fxmlLoader.load();

            ModelBindable<BaseModel> controller = fxmlLoader.getController();
            if (controller == null) {
                throw new IncorrectModelControllerBindingException();
            }

            controller.setService(this);

            return Optional.ofNullable(root);
        } catch (Exception e) {
            log.error("Loading file failed!", e);
            return Optional.empty();
        }
    }

    abstract void configureWindow(Stage stage, Scene scene);
}
