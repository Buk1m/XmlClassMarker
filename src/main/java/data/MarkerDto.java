package data;

import javafx.scene.paint.Color;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarkerDto {
    private String classLabel;
    private String description;
    private Color color;
    private String shortcut;
}
