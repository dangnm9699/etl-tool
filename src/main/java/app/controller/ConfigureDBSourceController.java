package app.controller;

import app.custom.Workspace;
import app.model.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ConfigureDBSourceController extends ConfigureDBController {
    @FXML
    private TableView<MappingSelect> columnsTable;
    @FXML
    private Button samples;

    private ConfigureDBSource config;

    public ConfigureDBSource getConfig() {
        return this.config;
    }

    @Override
    protected void setConfig(MetadataDB metadata) {
        config.setMetadata(metadata);
    }

    public void presetConfig(ConfigureDBSource config) {
        samples.setDisable(true);
        if (config == null) {
            this.config = new ConfigureDBSource();
            dbDriver.setText(Driver.MySQL.toString());
            dbHost.setText("localhost");
            dbPort.setText("3306");
            dbUsername.setText("root");
            dbPassword.setText("12345678");
            dbDatabase.setText("datawarehouse");
            return;
        }
        this.config = config;
        //Get metadata
        MetadataDB metadata = this.config.getMetadata();
        //Set metadata
        setMetadata(metadata);
        //Connect to DB
        this.connect(metadata);
    }

    @FXML
    private void showSamples() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/ConfigureDBSourceShowSamples.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            ConfigureDBSourceShowSamplesController controller = loader.getController();
            ConfigureDBSource configureDBSource = new ConfigureDBSource();
            configureDBSource.setMetadata(buildMetadata());
            configureDBSource.setTable(dbTable.getText());
            configureDBSource.setMappingSelects(columnsTable.getItems());
            controller.setup(configureDBSource);
            stage.setTitle("Samples");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException exception) {
            exception.printStackTrace();
            System.out.println("Some errors occurred");
        }
    }

    private void buildTablesMenu(List<String> tables) {
        dbTable.getItems().clear();
        EventHandler<ActionEvent> eClickTablesMenu = event -> {
            String tableName = ((MenuItem) event.getSource()).getText();
            dbTable.setText(tableName);
            buildMappingSelectTable(tableName);
            if (!isConfigured) {
                isConfigured = true;
                ok.setDisable(false);
                samples.setDisable(false);
            }
        };
        for (String table : tables) {
            MenuItem item = new MenuItem(table);
            item.setOnAction(eClickTablesMenu);
            dbTable.getItems().add(item);
        }

        String configuredTable = this.config.getTable();
        if (configuredTable != null && tables.contains(configuredTable)) {
            dbTable.setText(configuredTable);
            buildMappingSelectTable(configuredTable);
            isConfigured = true;
            ok.setDisable(false);
            samples.setDisable(false);
        }
    }

    private void buildMappingSelectTable(String tableName) {
        columnsTable.getColumns().clear();
        columnsTable.getItems().clear();
        //
        try {
            ConfigData configData = new ConfigData(connector.retrieveColumns(tableName));
            if (config.getMappingSelects() != null) {
                List<String> externals = this.config.getOriginalExternals();
                int diff = Utils.compare2StringLists(configData.getColumnNames(), externals);
                if (diff == 0) {
                    Utils.buildMappingSelectsTable(columnsTable, config.getMappingSelects());
                    return;
                }
            }
            List<MappingSelect> selects = Utils.buildMappingSelectList(configData.getColumns());
            Utils.buildMappingSelectsTable(columnsTable, selects);
        } catch (SQLException exception) {
            Workspace.appendLn(exception.getMessage());
            System.out.println("Some errors occurred");
        }
    }

    @Override
    protected void buildEventHandlers() {
        eClickCancel = event -> {
            this.closeType = CloseType.CANCEL;
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        };

        eClickOK = event -> {
            this.closeType = CloseType.OK;
            config.setTable(dbTable.getText());
            config.setMappingSelects(columnsTable.getItems());
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        };

        eClickTestConn = event -> {
            this.clear();
            MetadataDB metadata = buildMetadata();
            boolean validate = validateMetadata(metadata);
            if (!validate) setImageView(false);
            else this.connect(metadata);
        };
    }

    @Override
    protected void assignEventHandlers() {
        cancel.setOnAction(eClickCancel);
        ok.setOnAction(eClickOK);
        testConn.setOnAction(eClickTestConn);
    }

    @Override
    protected void buildTab2() {
        try {
            List<String> tables = connector.retrieveTableNames();
            buildTablesMenu(tables);
        } catch (SQLException exception) {
            Workspace.appendLn(exception.getMessage());
            System.out.println("Some error occurred");
        }
    }

    @Override
    protected void clearTables() {
        this.columnsTable.getColumns().clear();
        this.columnsTable.getItems().clear();
    }
}
