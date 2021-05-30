package app.controller;

import app.custom.Workspace;
import app.model.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ConfigureDBDestinationController extends ConfigureDBController {
    @FXML
    private TableView<Mapping> mappingTable;
    @FXML
    private TableView<Column> output;
    @FXML
    private Button create;

    private EventHandler<ActionEvent> eClickCreate = null;

    private ConfigureDBDestination config;
    private ConfigData inputConfigData;
    private boolean configuredBefore;

    public ConfigureDBDestination getConfig() {
        return config;
    }

    @Override
    protected void setConfig(MetadataDB metadata) {
        config.setMetadata(metadata);
    }

    public void presetConfig(ConfigureDBDestination config, ConfigData input, boolean configuredBefore) {
        this.inputConfigData = input;
        this.configuredBefore = configuredBefore;
        if (config == null) {
            this.config = new ConfigureDBDestination();
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
        connect(metadata);
    }

    private void buildTablesMenu(List<String> tables) {
        dbTable.getItems().clear();
        EventHandler<ActionEvent> eClickTablesMenu = event -> {
            String tableName = ((MenuItem) event.getSource()).getText();
            dbTable.setText(tableName);
            buildTables(tableName);
            if (!isConfigured) {
                isConfigured = true;
                ok.setDisable(false);
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
            buildTables(configuredTable);
            isConfigured = true;
            ok.setDisable(false);
        }
    }

    private void buildTables(String tableName) {
        buildMappingTable(tableName);
        buildOutputColumnsTable(tableName);
    }

    private void buildMappingTable(String tableName) {
        mappingTable.getColumns().clear();
        mappingTable.getItems().clear();
        try {
            List<String> fields = connector.retrieveFieldName(tableName);
            if (configuredBefore) {
                List<String> prevSelectedInputs = config.getSelectedInputs();
                config.getInput().removeAll(prevSelectedInputs);
                Utils.buildMappingTable(mappingTable, config.getMappings(), config.getInput());
            } else {
                config.setInput(inputConfigData.getColumnNames());
                Utils.buildMappingTable(mappingTable, Utils.buildMappingList(fields), config.getInput());
            }
        } catch (SQLException exception) {
            Workspace.appendLn(exception.getMessage());
            System.out.println("Some errors occurred");
        }
    }

    private void buildOutputColumnsTable(String tableName) {
        //Clear content
        output.getColumns().clear();
        output.getItems().clear();
        //Setup table columns
        output.setEditable(false);
        TableColumn<Column, String> name = new TableColumn<>("Name");
        name.setSortable(false);
        name.setEditable(false);
        name.setResizable(false);
        name.prefWidthProperty().bind(output.widthProperty().divide(2));
        name.setCellValueFactory(cellValue -> new SimpleStringProperty(cellValue.getValue().getName()));
        TableColumn<Column, String> type = new TableColumn<>("Type");
        type.setSortable(false);
        type.setEditable(false);
        type.setResizable(false);
        type.prefWidthProperty().bind(output.widthProperty().divide(2));
        type.setCellValueFactory(cellValue -> new SimpleStringProperty(cellValue.getValue().getType().toString()));
        //Add columns to table
        output.getColumns().add(name);
        output.getColumns().add(type);

        try {
            List<Column> columns = connector.retrieveColumns(tableName);
            //Add items to table
            output.getItems().addAll(columns);
        } catch (SQLException exception) {
            Workspace.appendLn(exception.getMessage());
            System.out.println("Some errors occurred");
        }
    }

    @Override
    protected void buildEventHandlers() {
        eClickCancel = event -> {
            closeType = CloseType.CANCEL;
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        };

        eClickOK = event -> {
            config.setTable(dbTable.getText());
            config.setMappings(mappingTable.getItems());
            if (config.getSelectedInputCount() == 0) {
                Utils.alertError("There is no selected input");
                return;
            }
            closeType = CloseType.OK;
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        };

        eClickTestConn = event -> {
            clear();
            MetadataDB metadata = buildMetadata();
            boolean validate = validateMetadata(metadata);
            if (!validate) setImageView(false);
            else connect(metadata);
        };

        eClickCreate = event -> {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/fxml/CreateTable.fxml"));

                Parent root = loader.load();
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Create table");
                stage.setResizable(false);

                CreateTableController controller = loader.getController();
                controller.preset(config.getMetadata(), inputConfigData);

                Scene scene = new Scene(root);

                stage.setScene(scene);
                stage.showAndWait();

                if (controller.isParsed()) {
                    System.out.println(controller.getTable());
                    this.config.setTable(controller.getTable());
                    buildTab2();
                }

            } catch (IOException exception) {
                exception.printStackTrace();
                System.out.println("Some errors occurred");
            } catch (NullPointerException exception) {
                System.out.println("Oh no! Null cell");
            }
        };
    }

    @Override
    protected void assignEventHandlers() {
        cancel.setOnAction(eClickCancel);
        ok.setOnAction(eClickOK);
        testConn.setOnAction(eClickTestConn);
        create.setOnAction(eClickCreate);
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
        mappingTable.getColumns().clear();
        mappingTable.getItems().clear();
        output.getItems().clear();
    }
}
