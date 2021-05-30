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
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.DefaultStringConverter;
import tech.tablesaw.api.ColumnType;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class ConfigureTransformationDatePlusMinusController implements Initializable {
    @FXML
    private TableView<Column> inputs;
    @FXML
    private TableView<DatePlusMinus> selects;
    @FXML
    private Button cancel;
    @FXML
    private Button ok;

    private EventHandler<ActionEvent> eClickOk = null;
    private EventHandler<ActionEvent> eClickCancel = null;


    private ConfigureTransformationDatePlusMinus config;
    private CloseType closeType;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        buildEventHandlers();
        assignEventHandlers();
    }

    public CloseType getCloseType() {
        return closeType;
    }

    public ConfigureTransformationDatePlusMinus getConfig() {
        return config;
    }

    public void presetConfig(ConfigureTransformationDatePlusMinus config, ConfigData configData, boolean configuredBefore) {
        this.config = Objects.requireNonNullElseGet(config, ConfigureTransformationDatePlusMinus::new);
        buildInputsTable(configData.getColumns());
        if (configuredBefore) {
            buildDatePlusMinusTable(this.config.getDatePlusMinuses());
        } else {
            List<DatePlusMinus> datePlusMinuses = new ArrayList<>();
            for (Column col : configData.getColumns()) {
                if (col.getType().equals(ColumnType.LOCAL_DATE))
                    datePlusMinuses.add(
                            new DatePlusMinus(col.getName())
                    );
            }
            buildDatePlusMinusTable(datePlusMinuses);
        }
    }

    private void buildDatePlusMinusTable(List<DatePlusMinus> items) {
        //Clear content
        selects.getItems().clear();
        selects.getColumns().clear();
        selects.setEditable(true);
        selects.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        //Setup table columns
        TableColumn<DatePlusMinus, String> name = new TableColumn<>("Name");
        name.setSortable(false);
        name.setEditable(false);
        name.setResizable(false);
        name.prefWidthProperty().bind(selects.widthProperty().divide(6));
        name.setCellValueFactory(cellValue -> cellValue.getValue().nameProperty());

        TableColumn<DatePlusMinus, String> day = new TableColumn<>("Day");
        day.setSortable(false);
        day.setEditable(true);
        day.setResizable(false);
        day.prefWidthProperty().bind(selects.widthProperty().divide(6));
        day.setCellValueFactory(cellValue -> cellValue.getValue().dayProperty());
        day.setCellFactory(TextFieldTableCell.forTableColumn());
        day.setOnEditCommit(event -> {
            String newVal = event.getNewValue();
            String oldVal = event.getOldValue();
            if (newVal.equals("")) {
                event.getRowValue().setDay(oldVal);
                day.setVisible(false);
                day.setVisible(true);
                return;
            }
            if (!newVal.matches("\\d*")) {
                event.getRowValue().setDay(oldVal);
                day.setVisible(false);
                day.setVisible(true);
                return;
            }
            int newInt = Integer.parseInt(newVal);
            if (newInt < 0) {
                event.getRowValue().setYear(oldVal);
                day.setVisible(false);
                day.setVisible(true);
                return;
            }
            event.getRowValue().setDay(newVal);
            day.setVisible(false);
            day.setVisible(true);
        });

        TableColumn<DatePlusMinus, String> week = new TableColumn<>("Week");
        week.setSortable(false);
        week.setEditable(true);
        week.setResizable(false);
        week.prefWidthProperty().bind(selects.widthProperty().divide(6));
        week.setCellValueFactory(cellValue -> cellValue.getValue().weekProperty());
        week.setCellFactory(TextFieldTableCell.forTableColumn());
        week.setOnEditCommit(event -> {
            String newVal = event.getNewValue();
            String oldVal = event.getOldValue();
            if (newVal.equals("")) {
                event.getRowValue().setDay(oldVal);
                week.setVisible(false);
                week.setVisible(true);
                return;
            }
            if (!newVal.matches("\\d*")) {
                event.getRowValue().setWeek(oldVal);
                week.setVisible(false);
                week.setVisible(true);
                return;
            }
            int newInt = Integer.parseInt(newVal);
            if (newInt < 0) {
                event.getRowValue().setYear(oldVal);
                week.setVisible(false);
                week.setVisible(true);
                return;
            }
            event.getRowValue().setWeek(newVal);
            week.setVisible(false);
            week.setVisible(true);
        });

        TableColumn<DatePlusMinus, String> month = new TableColumn<>("Month");
        month.setSortable(false);
        month.setEditable(true);
        month.setResizable(false);
        month.prefWidthProperty().bind(selects.widthProperty().divide(6));
        month.setCellValueFactory(cellValue -> cellValue.getValue().monthProperty());
        month.setCellFactory(TextFieldTableCell.forTableColumn());
        month.setOnEditCommit(event -> {
            String newVal = event.getNewValue();
            String oldVal = event.getOldValue();
            if (newVal.equals("")) {
                event.getRowValue().setDay(oldVal);
                month.setVisible(false);
                month.setVisible(true);
                return;
            }
            if (!newVal.matches("\\d*")) {
                event.getRowValue().setMonth(oldVal);
                month.setVisible(false);
                month.setVisible(true);
                return;
            }
            int newInt = Integer.parseInt(newVal);
            if (newInt < 0) {
                event.getRowValue().setYear(oldVal);
                month.setVisible(false);
                month.setVisible(true);
                return;
            }
            event.getRowValue().setMonth(newVal);
            month.setVisible(false);
            month.setVisible(true);
        });

        TableColumn<DatePlusMinus, String> year = new TableColumn<>("Year");
        year.setSortable(false);
        year.setEditable(true);
        year.setResizable(false);
        year.prefWidthProperty().bind(selects.widthProperty().divide(6));
        year.setCellValueFactory(cellValue -> cellValue.getValue().yearProperty());
        year.setCellFactory(TextFieldTableCell.forTableColumn());
        year.setOnEditCommit(event -> {
            String newVal = event.getNewValue();
            String oldVal = event.getOldValue();
            if (newVal.equals("")) {
                event.getRowValue().setDay(oldVal);
                year.setVisible(false);
                year.setVisible(true);
                return;
            }
            if (!newVal.matches("\\d*")) {
                event.getRowValue().setYear(oldVal);
                year.setVisible(false);
                year.setVisible(true);
                return;
            }
            int newInt = Integer.parseInt(newVal);
            if (newInt < 0) {
                event.getRowValue().setYear(oldVal);
                year.setVisible(false);
                year.setVisible(true);
                return;
            }
            event.getRowValue().setYear(newVal);
            year.setVisible(false);
            year.setVisible(true);
        });

        TableColumn<DatePlusMinus, String> type = new TableColumn<>("Type");
        type.setSortable(false);
        type.setEditable(true);
        type.setResizable(false);
        type.prefWidthProperty().bind(selects.widthProperty().divide(6));
        type.setCellValueFactory(cellValue -> cellValue.getValue().typeProperty());
        List<String> plusMinus = new ArrayList<>();
        plusMinus.add("Plus");
        plusMinus.add("Minus");
        type.setCellFactory(ComboBoxTableCell.forTableColumn(new DefaultStringConverter(), plusMinus.toArray(new String[0])));
        type.setCellValueFactory(cell -> {
            DatePlusMinus cellValue = cell.getValue();
            SimpleStringProperty property = cellValue.typeProperty();
            property.addListener((obs, oldVal, newVal) -> cellValue.setType(newVal));
            return property;
        });
        //Add columns to table
        selects.getColumns().add(name);
        selects.getColumns().add(day);
        selects.getColumns().add(week);
        selects.getColumns().add(month);
        selects.getColumns().add(year);
        selects.getColumns().add(type);
        //Add items to table
        selects.getItems().addAll(items);
    }

    private void buildEventHandlers() {
        eClickOk = event -> {
            closeType = CloseType.OK;
            config.setDatePlusMinuses(selects.getItems());
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
}
