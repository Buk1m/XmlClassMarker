package controllers;

import java.io.File;
import java.util.List;

import controllers.view.MarkerButtonView;
import data.MarkerDto;
import javafx.collections.ListChangeListener.Change;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import models.MainWindowModel;
import org.apache.commons.lang3.NotImplementedException;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

public class MainWindow implements ModelBindable<MainWindowModel> {
    private MainWindowModel service;
    public VBox MarkerButtonBox;
    public TextArea TextFieldArea;
    public TextField XmlFilePathField;
    public Button XmlFilePathButton;
    public TextField SaveFolderField;
    public Button SaveFolderButton;
    public Button SaveButton;
    public Spinner IdSpinner;
    public Button LoadButton;
    public Button LoadXmlFileButton;

    @Override
    public void setService(MainWindowModel service) {
        this.service = service;

        connectObservables();
    }

    private void connectObservables() {
        service.getMarkers().addListener(this::onMarkersChanged);
    }

    private void onMarkersChanged(Change<? extends MarkerDto> change) {
        while (change.next()) {
            List<? extends MarkerDto> addedSubList = change.getAddedSubList();
            createMarkerButtons(addedSubList);
        }

        List<? extends MarkerDto> removed = change.getRemoved();
        if (isNotEmpty(removed)) {
            removeMarkerButtons(removed);
        }
    }

    private void createMarkerButtons(List<? extends MarkerDto> markerDtos) {
        markerDtos.forEach(this::createMarkerButtonView);
    }

    private void removeMarkerButtons(List<? extends MarkerDto> markerDtos) {
        throw new NotImplementedException("Marker buttons removal was not implemented");
    }

    private void createMarkerButtonView(MarkerDto markerDto) {
        HBox hBox = new HBox();

        MarkerButtonView markerView = new MarkerButtonView(markerDto);
        hBox.getChildren().add(markerView);
        HBox.setHgrow(markerView, Priority.ALWAYS);
        HBox.setMargin(markerView, new Insets(5.d));

        MarkerButtonBox.getChildren().add(hBox);

        markerView.setOnAction(event -> onButtonClicked(markerView, event));
    }

    private void onButtonClicked(MarkerButtonView markerView, ActionEvent event) {

    }

    public void onChooseXmlFileButtonClicked() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Wybierz plik xml");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Pliki xml (*.xml)", "*.xml");
        chooser.getExtensionFilters().add(extFilter);
        File file = chooser.showOpenDialog(XmlFilePathButton.getScene().getWindow());

        XmlFilePathField.setText(file.getAbsolutePath());
    }

    public void onChooseSaveFolderButtonClicked() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Wybierz folder do zapisu");
        File file = chooser.showDialog(SaveFolderField.getScene().getWindow());

        SaveFolderField.setText(file.getAbsolutePath());
    }

    public void onLoadButtonClicked() {

    }

    public void onLoadXmlFileButtonClicked() {

    }

    public void onSaveButtonClicked() {

    }
}
