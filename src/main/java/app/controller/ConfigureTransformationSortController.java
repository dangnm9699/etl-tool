package app.controller;

import app.model.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class ConfigureTransformationSortController implements Initializable {
    @FXML
    private ComboBox<String> column;
    @FXML
    private ComboBox<SortOrder> order;
    @FXML
    private Button cancel;
    @FXML
    private Button ok;

    private EventHandler<ActionEvent> eClickOk = null;
    private EventHandler<ActionEvent> eClickCancel = null;

    private ConfigureTransformationSort config;
    private CloseType closeType;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        buildEventHandlers();
        assignEventHandlers();
        buildOrderSelectCombobox();
    }

    public ConfigureTransformationSort getConfig() {
        return config;
    }

    public CloseType getCloseType() {
        return closeType;
    }

    public void presetConfig(ConfigureTransformationSort config, ConfigData configData, boolean configuredBefore) {
        this.config = Objects.requireNonNullElseGet(config, ConfigureTransformationSort::new);
        buildColumnSelectCombobox(configData.getColumnNames());
        if (configuredBefore) {
            column.getSelectionModel().select(config.getSortBy().getColumn());
            order.getSelectionModel().select(config.getSortBy().getOrder());
        } else {
            column.getSelectionModel().select(null);
            order.getSelectionModel().select(SortOrder.Ascending);
        }
    }

    private void buildOrderSelectCombobox() {
        order.getItems().addAll(SortOrder.values());
        order.setConverter(new StringConverter<SortOrder>() {
            @Override
            public String toString(SortOrder sortOrder) {
                return sortOrder.getName();
            }

            @Override
            public SortOrder fromString(String s) {
                return order.getItems().stream().filter(
                        ap -> ap.getName().equals(s)).findFirst().orElse(null);
            }
        });
    }

    private void buildColumnSelectCombobox(List<String> colNames) {
        column.getItems().addAll(colNames);
    }

    private void buildEventHandlers() {
        eClickOk = event -> {
            closeType = CloseType.OK;
            String selectedColumn = column.getSelectionModel().getSelectedItem();
            SortOrder selectedOrder = order.getSelectionModel().getSelectedItem();
            if (selectedColumn == null) {
                Utils.alertError("Please choose a column!");
                return;
            }
            config.setSortBy(new SortBy(
                    selectedColumn,
                    selectedOrder
            ));
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
