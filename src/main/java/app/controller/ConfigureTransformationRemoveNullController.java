package app.controller;

import app.model.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class ConfigureTransformationRemoveNullController implements Initializable {
    @FXML
    private TableView<ColumnSelect> columnSelect;
    @FXML
    private Button ok;
    @FXML
    private Button cancel;

    private EventHandler<ActionEvent> eClickButtonOK = null;
    private EventHandler<ActionEvent> eClickButtonCancel = null;

    private ConfigureTransformationRemoveNull config;
    private CloseType closeType;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        buildEventHandlers();
        assignEventHandlers();
    }

    public void presetConfig(ConfigureTransformationRemoveNull config, ConfigData input, boolean configuredBefore) {
        this.config = Objects.requireNonNullElseGet(config, ConfigureTransformationRemoveNull::new);
        if (configuredBefore) {
            Utils.buildColumnSelectsTable(columnSelect, this.config.getColumnSelects());
        } else {
            List<ColumnSelect> items = new ArrayList<>();
            for (Column column : input.getColumns()) {
                items.add(new ColumnSelect(false, column.getName(), column.getType()));
            }
            Utils.buildColumnSelectsTable(columnSelect, items);
        }
    }

    public ConfigureTransformationRemoveNull getConfig() {
        return config;
    }

    public CloseType getCloseType() {
        return closeType;
    }

    private void buildEventHandlers() {
        eClickButtonOK = event -> {
            this.closeType = CloseType.OK;
            this.config.setColumnSelects(columnSelect.getItems());
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        };
        eClickButtonCancel = event -> {
            this.closeType = CloseType.CANCEL;
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        };
    }

    private void assignEventHandlers() {
        ok.setOnAction(eClickButtonOK);
        cancel.setOnAction(eClickButtonCancel);
    }

}
