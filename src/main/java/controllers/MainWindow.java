package controllers;

import java.io.File;
import java.util.List;

import controllers.view.MarkerButtonView;
import data.MarkerDto;
import data.MedicalTextDto;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import lombok.extern.slf4j.Slf4j;
import models.MainWindowModel;
import org.apache.commons.lang3.NotImplementedException;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Slf4j
public class MainWindow implements ModelBindable<MainWindowModel> {
    private MainWindowModel service;
    public VBox MarkerButtonBox;
    public TextArea TextFieldArea;
    public TextField XlsFilePathField;
    public Button XlsFilePathButton;
    public TextField SaveFolderField;
    public Button SaveFolderButton;
    public Button SaveButton;
    public Spinner<Integer> IdSpinner;
    public Button LoadButton;
    public Button LoadXlsFileButton;

    private MedicalTextDto currentText;

    @Override
    public void setService(MainWindowModel service) {
        this.service = service;

        connectObservables();
        initValidation();
        initValues();
    }

    private void initValues() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 0);
        IdSpinner.setValueFactory(valueFactory);
    }

    private void connectObservables() {
        service.getMarkers().addListener(this::onMarkersChanged);
    }

    private void initValidation() {
        IdSpinner.disableProperty().bind(Bindings.size(service.getMedicalTextDtos()).isEqualTo(0));
        LoadButton.disableProperty().bind(Bindings.size(service.getMedicalTextDtos()).isEqualTo(0));
        LoadButton.disableProperty().bind(IdSpinner.valueProperty().isNull());

        LoadXlsFileButton.disableProperty().bind(Bindings.size(service.getMedicalTextDtos()).greaterThan(0));
        LoadXlsFileButton.disableProperty().bind(XlsFilePathField.textProperty().isEmpty());
        SaveButton.disableProperty().bind(TextFieldArea.textProperty().isNull()
                                                       .or(TextFieldArea.textProperty().isEmpty()));
        SaveButton.disableProperty().bind(SaveFolderField.textProperty().isNull()
                                                         .or(SaveFolderField.textProperty().isEmpty()));
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
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Pliki xlsx (*.xlsx)", "*.xlsx");
        chooser.getExtensionFilters().add(extFilter);
        File file = chooser.showOpenDialog(XlsFilePathButton.getScene().getWindow());

        XlsFilePathField.setText(file.getAbsolutePath());
    }

    public void onChooseSaveFolderButtonClicked() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Wybierz folder do zapisu");
        File file = chooser.showDialog(SaveFolderField.getScene().getWindow());

        SaveFolderField.setText(file.getAbsolutePath());
    }

    public void onLoadButtonClicked() {
        Integer chosenText = IdSpinner.getValue();
        ObservableList<MedicalTextDto> medicalTextDtos = service.getMedicalTextDtos();
        if (chosenText < medicalTextDtos.size()) {
            currentText = medicalTextDtos.get(chosenText);
            TextFieldArea.setText(currentText.getOperationDescription());
        } else {
            log.warn("Chosen text id is incorrect");
        }
    }

    public void onLoadXmlFileButtonClicked() {
        String xlsFilePath = XlsFilePathField.getText();
        service.loadXlsFile(xlsFilePath);
    }

    public void onSaveButtonClicked() {

    }
}
