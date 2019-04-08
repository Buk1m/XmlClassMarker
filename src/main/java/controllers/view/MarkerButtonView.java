package controllers.view;

import data.MarkerDto;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;

public class MarkerButtonView extends Button {
    private MarkerDto marker;

    public MarkerButtonView(MarkerDto marker) {
        this.marker = marker;

        setPrefHeight(25.d);
        setMaxWidth(Double.MAX_VALUE);

        setText(marker.getDescription());
        getStyleClass().add("Button");
        setStyle("-fx-background-color: " + toHex(marker.getColor()));
        setTooltip(new Tooltip(marker.getShortcut()));
    }

    public static String toHex(Color color)
    {
        return String.format("#%02X%02X%02X",
                             (int) (color.getRed() * 255),
                             (int) (color.getGreen() * 255),
                             (int) (color.getBlue() * 255));
    }
}
