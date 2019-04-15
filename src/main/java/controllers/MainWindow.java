package controllers;

import java.io.File;
import java.time.Duration;
import java.util.Collection;
import java.util.List;

import controllers.view.MarkerButtonView;
import data.MarkerDto;
import data.MedicalTextDto;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import lombok.extern.slf4j.Slf4j;
import models.MainWindowModel;
import org.apache.commons.lang3.NotImplementedException;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.event.MouseOverTextEvent;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyledDocument;
import org.fxmisc.wellbehaved.event.EventPattern;
import org.fxmisc.wellbehaved.event.InputMap;
import org.fxmisc.wellbehaved.event.Nodes;
import org.reactfx.value.Val;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Slf4j
public class MainWindow implements ModelBindable<MainWindowModel> {
    private MainWindowModel service;
    public VBox MarkerButtonBox;
    public StyleClassedTextArea htmlEditor;
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
        setupEditor();
    }

    private void setupEditor() {
        htmlEditor.setWrapText(true);

        setupTooltip();
    }

    private void setupTooltip() {
        Popup popup = new Popup();
        Label popupMsg = new Label();
        popupMsg.setStyle(
                "-fx-background-color: grey;" +
                "-fx-text-fill: white;" +
                "-fx-padding: 5;");
        popup.getContent().add(popupMsg);

        htmlEditor.setMouseOverTextDelay(Duration.ofMillis(200));
        htmlEditor.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_BEGIN, e -> {
            int chIdx = e.getCharacterIndex();
            Point2D pos = e.getScreenPosition();

            Collection<String> styleOfChar = htmlEditor.getStyleOfChar(chIdx);
            if (!styleOfChar.isEmpty()) {
                popupMsg.setText(styleOfChar.iterator().next());
                popup.show(htmlEditor, pos.getX(), pos.getY() + 10);
            }
        });
        htmlEditor.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_END, e -> popup.hide());
    }

    private void initValues() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 0);
        IdSpinner.setValueFactory(valueFactory);
        IdSpinner.setEditable(true);
    }

    private void connectObservables() {
        service.getMarkers().addListener(this::onMarkersChanged);
    }

    private void initValidation() {
        IdSpinner.disableProperty().bind(Bindings.size(service.getMedicalTextDtos()).isEqualTo(0));
        LoadButton.disableProperty().bind(Bindings.size(service.getMedicalTextDtos()).isEqualTo(0)
                                                  .or(IdSpinner.valueProperty().isNull()));

        LoadXlsFileButton.disableProperty().bind(Bindings.size(service.getMedicalTextDtos()).greaterThan(0));
        LoadXlsFileButton.disableProperty().bind(XlsFilePathField.textProperty().isEmpty());
        SaveButton.disableProperty().bind(Val.map(htmlEditor.lengthProperty(), n -> n == 0));
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
        Nodes.addInputMap(htmlEditor,
                          InputMap.consume(EventPattern.keyPressed(KeyCombination.valueOf(markerView.getMarker().getShortcut())),
                          event -> onButtonClicked(markerView, null)));
    }

    private void onButtonClicked(MarkerButtonView markerView, ActionEvent event) {
        IndexRange selection = htmlEditor.getSelection();

        if (!markerView.getMarker().getClassLabel().equals(MainWindowModel.CLEAR_BUTTON_LABEL)) {
            htmlEditor.setStyleClass(selection.getStart(), selection.getEnd(), markerView.getMarker().getClassLabel());
        } else {
            htmlEditor.clearStyle(selection.getStart(), selection.getEnd());
        }
    }

    public void onChooseXmlFileButtonClicked() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Wybierz plik xml");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Pliki xlsx (*.xlsx)", "*.xlsx");
        chooser.getExtensionFilters().add(extFilter);
        File file = chooser.showOpenDialog(XlsFilePathButton.getScene().getWindow());

        if (file != null) {
            XlsFilePathField.setText(file.getAbsolutePath());
        } else {
            log.info("Chosen file path is null");
        }
    }

    public void onChooseSaveFolderButtonClicked() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Wybierz folder do zapisu");
        File file = chooser.showDialog(SaveFolderField.getScene().getWindow());

        if (file != null) {
            SaveFolderField.setText(file.getAbsolutePath());
        } else {
            log.info("Chosen directory path is null");
        }
    }

    public void onLoadButtonClicked() {
        Integer chosenText = IdSpinner.getValue();
        ObservableList<MedicalTextDto> medicalTextDtos = service.getMedicalTextDtos();
        if (chosenText < medicalTextDtos.size()) {
            currentText = medicalTextDtos.get(chosenText);
            htmlEditor.replaceText(currentText.getOperationDescription());

            service.applyCache(htmlEditor);
        } else {
            log.warn("Chosen text id is incorrect");
        }
    }

    public void onLoadXmlFileButtonClicked() {
        String xlsFilePath = XlsFilePathField.getText();
        service.loadXlsFile(xlsFilePath);

        htmlEditor.requestFocus();
        htmlEditor.moveTo(0);
    }

    public void onSaveButtonClicked() {
        StyledDocument<Collection<String>, String, Collection<String>> document = htmlEditor.getDocument();
        StyleSpans<Collection<String>> styleSpans = document.getStyleSpans(0, htmlEditor.getLength());

        service.saveMarkedFile(SaveFolderField.getText(), styleSpans, currentText);
    }
}
