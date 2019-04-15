package models;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.model.StyleSpan;
import org.fxmisc.richtext.model.StyleSpans;

@Slf4j
@Getter
public class MainWindowModel extends BaseModel {
    public static final String CLEAR_BUTTON_LABEL = "CLEAR";
    private static final String CACHE_FILE = "markedWordsCache.txt";
    private static final String fxmlMainWindowFileName = "fxml/MainWindow.fxml";

    private final CacheManager cacheManager;
    private ObservableList<MarkerDto> markers = FXCollections.observableArrayList();
    private ObservableList<MedicalTextDto> medicalTextDtos = FXCollections.observableArrayList();
    private Stage stage;

    public MainWindowModel(Stage primaryStage) {
        stage = primaryStage;
        createScene(primaryStage, fxmlMainWindowFileName);

        initMarkerButtons();
        cacheManager = new CacheManager(CACHE_FILE);
    }

    private void initMarkerButtons() {
        markers.add(new MarkerDto(CLEAR_BUTTON_LABEL, "Wyczyść", Color.WHITE, "Ctrl+Q"));
        markers.add(new MarkerDto("czynnosc", "Czynności", Color.valueOf("#a0bd9b"), "Ctrl+W"));
        markers.add(new MarkerDto("p_ust", "Płyny", Color.valueOf("#accfff"), "Ctrl+E"));
        markers.add(new MarkerDto("narzedzie", "Narzędzia", Color.valueOf("#ed9a79"), "Ctrl+R"));
        markers.add(new MarkerDto("sub_chem", "Sub. chemiczne", Color.valueOf("#deb96a"), "Ctrl+T"));
        markers.add(new MarkerDto("narzad", "Narządy", Color.valueOf("#a0eaff"), "Ctrl+1"));
        markers.add(new MarkerDto("cz_ciala", "Cz. ciała", Color.valueOf("#d5adff"), "Ctrl+2"));
        markers.add(new MarkerDto("wymiar", "Wymiary", Color.valueOf("#ffadd7"), "Ctrl+3"));
        markers.add(new MarkerDto("lokalizacja", "Lokalizacje", Color.valueOf("#ffebad"), "Ctrl+4"));
        markers.add(new MarkerDto("patologia", "Patologie", Color.valueOf("#d6ffad"), "Ctrl+5"));
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

    public void applyCache(StyleClassedTextArea htmlEditor) {
        Map<String, String> classLabelsByWords = cacheManager.getClassLabelsByWords();
        String text = htmlEditor.getText();
        htmlEditor.clearStyle(0, text.length());

        List<Entry<String, String>> cacheEntries = classLabelsByWords.entrySet()
                                                                     .stream()
                                                                     .sorted(Comparator.comparingInt(entry -> entry.getKey().length()))
                                                                     .collect(Collectors.toList());
        for (var pair : cacheEntries) {
            String startRegex = "(?i)[( ,.\\\\\\/'\\\"\\n]";
            String endRegex = "[) ,.\\\\\\/'\\\"\\n]";

            Matcher matcher = Pattern.compile(startRegex + pair.getKey() + endRegex).matcher(text);
            matcher.results().forEach(match -> htmlEditor.setStyleClass(match.start() + 1,
                                                                        match.end() - 1,
                                                                        pair.getValue()));

            // match at the beginning
            matcher = Pattern.compile("(?i)^" + pair.getKey() + endRegex).matcher(text);
            matcher.results().forEach(match -> htmlEditor.setStyleClass(match.start(),
                                                                        match.end() - 1,
                                                                        pair.getValue()));

            // match at the end
            matcher = Pattern.compile(startRegex + pair.getKey() + "$").matcher(text);
            matcher.results().forEach(match -> htmlEditor.setStyleClass(match.start() + 1,
                                                                        match.end(),
                                                                        pair.getValue()));
        }

    }

    public void saveMarkedFile(String directoryPath, StyleSpans<Collection<String>> styleSpans, MedicalTextDto medicalText) {
        String preparedText = prepareText(styleSpans, medicalText);
        String filePath = directoryPath + "/markedFile_" + medicalText.getProtocolId() + ".txt";

        saveToFile(filePath, preparedText);
    }

    private void saveToFile(String filePath, String textToSave) {
        try (PrintWriter writer = new PrintWriter(filePath, StandardCharsets.UTF_8)) {
            writer.write(textToSave);
        } catch (IOException e) {
            log.error("File saving failed!", e);
        }
    }

    private String prepareText(StyleSpans<Collection<String>> styleSpans, MedicalTextDto medicalText) {
        int position = 0;

        StringBuilder builder = new StringBuilder();
        builder.append("<opis id=\"");
        builder.append(medicalText.getProtocolId());
        builder.append("\">\n");

        String description = medicalText.getOperationDescription().replaceAll("\\r", "");
        for (StyleSpan<Collection<String>> span : styleSpans) {
            String extractedString = description.substring(position, position + span.getLength());
            if (span.getStyle().isEmpty()) {
                builder.append(extractedString);
            } else {
                String classLabel = span.getStyle().iterator().next();
                String markedText = markText(classLabel, extractedString);
                builder.append(markedText);
            }

            position += span.getLength();
        }
        cacheManager.saveCache();

        builder.append("\n</opis>\n");
        return builder.toString();
    }

    private String markText(String classLabel, String text) {
        cacheManager.addEntry(classLabel, text);
        return "<" + classLabel + ">" + text + "</" + classLabel + ">";
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
