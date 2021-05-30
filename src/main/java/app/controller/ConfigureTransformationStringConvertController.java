package app.controller;

import app.model.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.stage.Stage;
import javafx.util.converter.DefaultStringConverter;
import tech.tablesaw.api.ColumnType;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class ConfigureTransformationStringConvertController implements Initializable {
    @FXML
    private TableView<Column> inputs;
    @FXML
    private TableView<StringConvert> stringConverts;
    @FXML
    private Button cancel;
    @FXML
    private Button ok;

    private EventHandler<ActionEvent> eClickOk = null;
    private EventHandler<ActionEvent> eClickCancel = null;

    private ConfigureTransformationStringConvert config;
    private CloseType closeType;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        buildEventHandlers();
        assignEventHandlers();
    }

    public CloseType getCloseType() {
        return closeType;
    }

    public void presetConfig(ConfigureTransformationStringConvert config, ConfigData configData, boolean configuredBefore) {
        this.config = Objects.requireNonNullElseGet(config, ConfigureTransformationStringConvert::new);
        buildInputsTable(configData.getColumns());
        if (configuredBefore) {
            buildStringConvertTale(this.config.getConverts());
        } else {
            List<StringConvert> stringConvertList = new ArrayList<>();
            for (Column col : configData.getColumns()) {
                if (col.getType().equals(ColumnType.STRING))
                    stringConvertList.add(
                            new StringConvert(col.getName(), StringConvertType.Ignore.toString())
                    );
            }
            buildStringConvertTale(stringConvertList);
        }
    }

    public ConfigureTransformationStringConvert getConfig() {
        return config;
    }

    private void buildStringConvertTale(List<StringConvert> items) {
        //Clear content
        stringConverts.getItems().clear();
        stringConverts.getColumns().clear();
        stringConverts.setEditable(true);
        stringConverts.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        //Setup table columns
        TableColumn<StringConvert, String> name = new TableColumn<>("Name");
        name.setSortable(false);
        name.setEditable(false);
        name.setResizable(false);
        name.prefWidthProperty().bind(stringConverts.widthProperty().divide(2));
        name.setCellValueFactory(cellValue -> cellValue.getValue().nameProperty());
        TableColumn<StringConvert, String> type = new TableColumn<>("Convert type");
        type.setSortable(false);
        type.setEditable(true);
        type.setResizable(false);
        type.prefWidthProperty().bind(stringConverts.widthProperty().divide(2));
        List<String> convertTypes = StringConvertType.getList();
        type.setCellFactory(ComboBoxTableCell.forTableColumn(new DefaultStringConverter(), convertTypes.toArray(new String[0])));
        type.setCellValueFactory(cell -> {
            StringConvert cellValue = cell.getValue();
            SimpleStringProperty property = cellValue.typeProperty();
            property.addListener((obs, oldVal, newVal) -> cellValue.setType(newVal));
            return property;
        });
        //Add columns to table
        stringConverts.getColumns().add(name);
        stringConverts.getColumns().add(type);
        //Add items to table
        stringConverts.getItems().addAll(items);
    }

    private void buildInputsTable(List<Column> columns) {
        //Clear content
        inputs.getColumns().clear();
        inputs.getItems().clear();
        //Setup table columns
        inputs.setEditable(false);
        TableColumn<Column, String> name = new TableColumn<>("Name");
        name.setSortable(false);
        name.setEditable(false);
        name.setResizable(false);
        name.prefWidthProperty().bind(inputs.widthProperty().divide(2));
        name.setCellValueFactory(cellValue -> new SimpleStringProperty(cellValue.getValue().getName()));
        TableColumn<Column, String> type = new TableColumn<>("Type");
        type.setSortable(false);
        type.setEditable(false);
        type.setResizable(false);
        type.prefWidthProperty().bind(inputs.widthProperty().divide(2));
        type.setCellValueFactory(cellValue -> new SimpleStringProperty(cellValue.getValue().getType().toString()));
        //Add columns to table
        inputs.getColumns().add(name);
        inputs.getColumns().add(type);
        //Add items to table
        inputs.getItems().addAll(columns);
    }

    private void buildEventHandlers() {
        eClickOk = event -> {
            closeType = CloseType.OK;
            config.setConverts(stringConverts.getItems());
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        };
        eClickCancel = event -> {
            closeType = CloseType.CANCEL;
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        };
    }

    private void assignEventHandlers() {
        ok.setOnAction(eClickOk);
        cancel.setOnAction(eClickCancel);
    }
}
