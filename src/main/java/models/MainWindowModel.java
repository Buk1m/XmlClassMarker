package models;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import data.MarkerDto;
import data.MedicalTextDto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

@Slf4j
@Getter
public class MainWindowModel extends BaseModel {

    private static final String fxmlMainWindowFileName = "fxml/MainWindow.fxml";

    private ObservableList<MarkerDto> markers = FXCollections.observableArrayList();
    private ObservableList<MedicalTextDto> medicalTextDtos = FXCollections.observableArrayList();
    private Stage stage;

    public MainWindowModel(Stage primaryStage) {
        stage = primaryStage;
        createScene(primaryStage, fxmlMainWindowFileName);

        initMarkerButtons();
    }

    private void initMarkerButtons() {
        markers.add(new MarkerDto("CLEAR", "Wyczyść", Color.WHITE, "CTRL-Q"));
        markers.add(new MarkerDto("czynnosc", "Czynności", Color.valueOf("#a0bd9b"), "CTRL-W"));
        markers.add(new MarkerDto("p_ust", "Płyny", Color.valueOf("#accfff"), "CTRL-E"));
        markers.add(new MarkerDto("narzedzie", "Narzędzia", Color.valueOf("#ed9a79"), "CTRL-R"));
        markers.add(new MarkerDto("sub_chem", "Sub. chemiczne", Color.valueOf("#deb96a"), "CTRL-T"));
        markers.add(new MarkerDto("narzad", "narządy", Color.valueOf("#a0eaff"), "CTRL-1"));
        markers.add(new MarkerDto("cz_ciala", "cz. ciała", Color.valueOf("#d5adff"), "CTRL-2"));
        markers.add(new MarkerDto("wymiar", "Wymiary", Color.valueOf("#ffadd7"), "CTRL-3"));
        markers.add(new MarkerDto("lokalizacja", "Lokalizacje", Color.valueOf("#ffebad"), "CTRL-4"));
        markers.add(new MarkerDto("patologia", "Patologie", Color.valueOf("#d6ffad"), "CTRL-5"));
    }

    public void loadXlsFile(String path) {
        try {
            FileInputStream ip = new FileInputStream(path);
            Workbook wb = WorkbookFactory.create(ip);
            Sheet sheet = wb.getSheet("Arkusz1");

            List<MedicalTextDto> medicalTexts = new ArrayList<>();
            Iterator<Row> rowIterator = sheet.rowIterator();
            rowIterator.next(); // skip column names
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                int textId = (int) row.getCell(0).getNumericCellValue();
                String text = row.getCell(7).getStringCellValue();

                medicalTexts.add(new MedicalTextDto(textId, text));
            }

            medicalTextDtos.addAll(medicalTexts);
        } catch (Exception e) {
            log.error("Loading xml file failed", e);
        }
    }

    void configureWindow(Stage stage, Scene scene) {
        stage.setTitle("Xml marker");
        stage.setWidth(800);
        stage.setHeight(500);
        stage.setMinWidth(750);
        stage.setMinHeight(500);

        stage.show();
    }
}
