package controllers.view;

import data.MarkerDto;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import lombok.Getter;

@Getter
public class MarkerButtonView extends Button {
    private MarkerDto marker;

    public MarkerButtonView(MarkerDto marker) {
        this.marker = marker;

        setPrefHeight(25.d);
        setMaxWidth(Double.MAX_VALUE);

        setText(marker.getDescription());
        getStyleClass().add("Button");
        setStyle("-fx-background-color: " + toHex());

        Tooltip buttonTooltip = new Tooltip(marker.getShortcut());
        buttonTooltip.setShowDelay(new Duration(200));
        setTooltip(buttonTooltip);

    }

    public String toHex() {
        return String.format("#%02X%02X%02X",
                             (int) (marker.getColor().getRed() * 255),
                             (int) (marker.getColor().getGreen() * 255),
                             (int) (marker.getColor().getBlue() * 255));
    }
}
