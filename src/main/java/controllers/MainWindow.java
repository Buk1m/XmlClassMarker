package controllers;

import java.util.List;

import controllers.view.MarkerButtonView;
import data.MarkerDto;
import javafx.collections.ListChangeListener.Change;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import models.MainWindowModel;
import org.apache.commons.lang3.NotImplementedException;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

public class MainWindow implements ModelBindable<MainWindowModel> {
    private MainWindowModel service;
    public VBox MarkerButtonBox;

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
    }
}
