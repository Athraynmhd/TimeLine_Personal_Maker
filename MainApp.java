import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.SnapshotParameters;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainApp extends Application {
    private List<Event> events = new ArrayList<>();
    private TableView<Event> tableView = new TableView<>();

    @Override
    public void start(Stage primaryStage) {
        GridPane inputGrid = new GridPane();
        inputGrid.setPadding(new Insets(10, 10, 10, 10));
        inputGrid.setVgap(8);
        inputGrid.setHgap(10);

        Label dateLabel = new Label("Date:");
        GridPane.setConstraints(dateLabel, 0, 0);

        TextField dateField = new TextField();
        dateField.setPromptText("YYYY-MM-DD");
        GridPane.setConstraints(dateField, 1, 0);

        Label titleLabel = new Label("Title:");
        GridPane.setConstraints(titleLabel, 0, 1);

        TextField titleField = new TextField();
        titleField.setPromptText("Event Title");
        GridPane.setConstraints(titleField, 1, 1);

        Label descLabel = new Label("Description:");
        GridPane.setConstraints(descLabel, 0, 2);

        TextArea descArea = new TextArea();
        descArea.setPromptText("Event Description");
        GridPane.setConstraints(descArea, 1, 2);

        Button addButton = new Button("Add Event");
        addButton.setOnAction(e -> {
            Event event = new Event(dateField.getText(), titleField.getText(), descArea.getText());
            events.add(event);
            tableView.getItems().add(event);
        });
        GridPane.setConstraints(addButton, 1, 3);

        inputGrid.getChildren().addAll(dateLabel, dateField, titleLabel, titleField, descLabel, descArea, addButton);

        TableColumn<Event, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Event, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Event, String> descColumn = new TableColumn<>("Description");
        descColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        tableView.getColumns().addAll(dateColumn, titleColumn, descColumn);

        Button saveButton = new Button("Save to File");
        saveButton.setOnAction(e -> {
            try {
                FileManager.saveEvents(events, "events.txt");
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Events saved successfully!");
                alert.showAndWait();
            } catch (IOException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to save events!");
                alert.showAndWait();
            }
        });

        Button loadButton = new Button("Load from File");
        loadButton.setOnAction(e -> {
            try {
                events = FileManager.loadEvents("events.txt");
                tableView.getItems().setAll(events);
            } catch (IOException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load events!");
                alert.showAndWait();
            }
        });

        Button editButton = new Button("Edit Event");
        editButton.setOnAction(e -> {
            Event selectedEvent = tableView.getSelectionModel().getSelectedItem();
            if (selectedEvent != null) {
                dateField.setText(selectedEvent.getDate());
                titleField.setText(selectedEvent.getTitle());
                descArea.setText(selectedEvent.getDescription());
                events.remove(selectedEvent);
                tableView.getItems().remove(selectedEvent);
            }
        });

        Button deleteButton = new Button("Delete Event");
        deleteButton.setOnAction(e -> {
            Event selectedEvent = tableView.getSelectionModel().getSelectedItem();
            if (selectedEvent != null) {
                events.remove(selectedEvent);
                tableView.getItems().remove(selectedEvent);
            }
        });

        Button exportButton = new Button("Export to PNG");
        exportButton.setOnAction(e -> saveAsPng());

        VBox buttonsBox = new VBox(10, saveButton, loadButton, editButton, deleteButton, exportButton);

        VBox root = new VBox(10, inputGrid, tableView, buttonsBox);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 700, 700);
        primaryStage.setTitle("Personal Timeline Maker");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void saveAsPng() {
        WritableImage image = tableView.snapshot(new SnapshotParameters(), null);
        File file = new File("timeline.png");
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to export to PNG!");
            alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
