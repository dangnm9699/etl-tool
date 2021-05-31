package app.controller;

import app.model.*;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tech.tablesaw.api.ColumnType;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class ConfigureTransformationDateSplitController implements Initializable {
    @FXML
    private TableView<Column> inputs;
    @FXML
    private TableView<SelectColumn> selects;
    @FXML
    private Button cancel;
    @FXML
    private Button ok;

    private EventHandler<ActionEvent> eClickOk = null;
    private EventHandler<ActionEvent> eClickCancel = null;

    private final ToggleGroup group = new ToggleGroup();
    private ConfigureTransformationDateSplit config;
    private CloseType closeType;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        buildEventHandlers();
        assignEventHandlers();
    }

    public CloseType getCloseType() {
        return closeType;
    }

    public ConfigureTransformationDateSplit getConfig() {
        return config;
    }

    public void presetConfig(ConfigureTransformationDateSplit config, ConfigData configData, boolean configuredBefore) {
        this.config = Objects.requireNonNullElseGet(config, ConfigureTransformationDateSplit::new);
        buildInputsTable(configData.getColumns());
        List<SelectColumn> selectColumns = new ArrayList<>();
        if (configuredBefore) {
            SelectColumn selectColumn = this.config.getSelectColumn();
            for (Column col : configData.getColumns()) {
                if (col.getType().equals(ColumnType.LOCAL_DATE)) {
                    boolean selected = col.getName().equals(selectColumn.getName());
                    selectColumns.add(
                            new SelectColumn(col.getName(), selected)
                    );
                }
            }
        } else {
            for (Column col : configData.getColumns()) {
                if (col.getType().equals(ColumnType.LOCAL_DATE))
                    selectColumns.add(
                            new SelectColumn(col.getName(), false)
                    );
            }
            if (selectColumns.size() == 0) {
                Utils.alertError("There is no LOCAL_DATE column in table!");

                return;
            }
        }
        buildSelectsTable(selectColumns);
    }

    private void buildEventHandlers() {
        eClickOk = event -> {
            closeType = CloseType.OK;
            List<SelectColumn> selectColumns = selects.getItems();
//            System.out.println(selectColumns.get(0).isSelected());
//            System.out.println(selectColumns.get(1).isSelected());
            SelectColumn selectedColumn = null;
            for (SelectColumn selectColumn : selectColumns) {
                if (selectColumn.isSelected()) {
                    selectedColumn = selectColumn;
                    break;
                }
            }
            if (selectedColumn == null) {
                Utils.alertError("Please choose 1 column!");
                return;
            }
            config.setSelectColumn(selectedColumn);
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        };
        eClickCancel = event -> {
            closeType = CloseType.CANCEL;
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        };
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

    private void buildSelectsTable(List<SelectColumn> items) {
        //Clear content
        selects.getItems().clear();
        selects.getColumns().clear();
        selects.setEditable(true);
        selects.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        //Setup table columns
        TableColumn<SelectColumn, Boolean> selected = new TableColumn<>();
        selected.setSortable(false);
        selected.setEditable(true);
        selected.setResizable(false);
        selected.setStyle("-fx-alignment: CENTER");
        selected.prefWidthProperty().bind(selects.widthProperty().divide(5));
        selected.setCellValueFactory(cell -> {
            SelectColumn cellValue = cell.getValue();
            SimpleBooleanProperty property = cellValue.selectedProperty();

            // Add listener to handler change
            property.addListener((observable, oldValue, newValue) -> cellValue.setSelected(newValue));

            return property;
        });
        selected.setCellFactory(param -> new TableCell<>() {
            final RadioButton indicator = new RadioButton();

            @Override
            protected void updateItem(Boolean selected, boolean empty) {
                super.updateItem(selected, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    indicator.setSelected(selected);
                    indicator.setToggleGroup(group);
                    setGraphic(indicator);

                    indicator.selectedProperty().addListener(
                            (obs, wasSelected, isSelected) -> {
                                if (isSelected) {
                                    SelectColumn item = getTableRow().getItem();
                                    for (SelectColumn selectColumn : getTableView().getItems()) {
                                        selectColumn.setSelected(item == selectColumn);
                                    }
                                }
                            }
                    );
                }
            }
        });

        TableColumn<SelectColumn, String> name = new TableColumn<>("Column");
        name.setSortable(false);
        name.setEditable(true);
        name.setResizable(false);
        name.prefWidthProperty().bind(selects.widthProperty().divide(5 / 4));
        name.setCellValueFactory(cellValue -> cellValue.getValue().nameProperty());
        //Add columns to table
        selects.getColumns().add(selected);
        selects.getColumns().add(name);
        //Add items to table
        selects.getItems().addAll(items);
    }

    private void assignEventHandlers() {
        ok.setOnAction(eClickOk);
        cancel.setOnAction(eClickCancel);
    }
}
