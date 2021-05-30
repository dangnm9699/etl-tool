package app.controller;

import app.connection.ConnectorExcel;
import app.custom.Workspace;
import app.model.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class ConfigureExelDestinationController extends ConfigureExelController {
    @FXML
    private TableView<Mapping> tableView;
    @FXML
    private TableView<Column> output;

    private ConfigureExelDestination config;
    private ConfigData inputConfigData;
    private boolean configuredBefore;
    //    private List<String> inputs;
    private boolean checkHasLabel;


    @Override
    protected void assignEventHandlers() {
        cancel.setOnAction(eClickCancel);
        ok.setOnAction(eClickOK);
        view.setOnAction(eClickView);
        brow.setOnAction(eClickBrow);
        check.setOnAction(eClickCheck);

    }

    @Override
    protected void buildEventHandlers() {
        eClickCancel = event -> {
            this.closeType = CloseType.CANCEL;
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        };

        eClickOK = event -> {
            this.closeType = CloseType.OK;
//            this.isConfigured = true;
            config.setMappings(tableView.getItems());
            if (config.getSelectedInputCount() == 0) {
                Utils.alertError("There is no selected input");
                return;
            }

            config.setPath(path.getText());
//            System.out.println("haslabel"+check.isSelected());
            config.setHasLabel(check.isSelected());
            config.setTable(exelTable.getText());
//            this.connectorExcel.set
//            isConfigured = true;
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        };


        eClickBrow = event -> {
            Stage stage = (Stage) exel.getScene().getWindow();
            FileChooser fc = new FileChooser();
            fc.setTitle("Choose a Exel file");
            FileChooser.ExtensionFilter exelFilter = new FileChooser.ExtensionFilter("Exel Files", "*.xlsx");
            fc.getExtensionFilters().add(exelFilter);
            File file = fc.showOpenDialog(stage);
            if (file != null) {
//                this.ok.setDisable(false);

//                this.exelPath = file.getAbsolutePath();
//                System.out.println("dkm" + this.csvPath);
                this.path.setText(file.getAbsolutePath());
                try {
                    this.connectorExcel = new ConnectorExcel(file.getAbsolutePath());
                } catch (IOException exception) {
                    Workspace.appendLn(exception.getMessage());
                    exception.printStackTrace();
                }
                try {
                    this.buildTablesMenu(this.connectorExcel.getListSheet());
                } catch (IOException e) {
                    Workspace.appendLn(e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        eClickView = event -> {
//            try {
            tableView.getItems().clear();
            try {
                this.connectorExcel = new ConnectorExcel(this.path.getText());
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            this.connectorExcel.setHasLabel(check.isSelected());
            String tableName = this.exelTable.getText();
            try {
                buildTables(tableName);
            } catch (IOException e) {
                Workspace.appendLn(e.getMessage());
                e.printStackTrace();
            }

//            } catch (e) {
//                System.out.println("Some error occurred");
//            }
        };
        eClickCheck = event -> {
            checkHasLabel = !checkHasLabel;
//            System.out.println("check"+checkHasLabel);
        };


    }

    public CloseType getCloseType() {
        return this.closeType;
    }

    public ConfigureExelDestination getConfig() {
        return this.config;
    }

    public void presetConfig(ConfigureExelDestination config, ConfigData input, boolean configuredBefore) throws IOException {
        this.inputConfigData = input;
        this.configuredBefore = configuredBefore;
        if (config == null) {
            this.config = new ConfigureExelDestination();
            return;
        }
        this.config = config;

        this.path.setText(config.getPath());
        this.check.setSelected(config.getHasLabel());
//        this.checkHasLabel = config.getHasLabel();
        this.connectorExcel = new ConnectorExcel(this.config.getPath());
        this.connectorExcel.setHasLabel(config.getHasLabel());
        this.connectorExcel.setTable(this.config.getTable());
        this.buildTablesMenu(this.connectorExcel.getListSheet());
        this.buildTables(this.config.getTable());
//        this.buildMappingSelectTable(this.config.getTable());
        view.setDisable(false);
        ok.setDisable(false);
    }

    private void buildTables(String tableName) throws IOException {
        buildMappingTable(tableName);
        buildOutputColumnsTable(tableName);
    }

    private void buildMappingTable(String tableName) throws IOException {
        tableView.getColumns().clear();
        tableView.getItems().clear();
        //
        ok.setDisable(false);
        String path = this.config.getPath();
//        System.out.println("compare "+this.config.getTable() +" "+this.exelTable.getText());
        if (path == null) {
            path = this.path.getText();
        } else if (!configuredBefore || !this.config.getTable().equals(this.exelTable.getText())) {
//                System.out.println("kho hieu1 "+path +"  "+this.path.getText() );
            config.setMappingSelects(null);
            path = this.path.getText();
        }
//            System.out.println("kho hieu "+config.getMappingSelects());
//            reader = new BufferedReader(new FileReader(path));
//            String line = reader.readLine();
        if (config.getMappingSelects() == null || this.config.getHasLabel() != check.isSelected()) {
//            isConfigured = true;
//                System.out.println("chetday "+path);
//                reader = new BufferedReader(new FileReader(path));
//                String line = reader.readLine();
//                ConnectorExcel connectorExcel = new ConnectorExcel(path);
//                String[] line = csvReader.readNext();
            connectorExcel.setExelPath(path);
            connectorExcel.setHasLabel(check.isSelected());
            List<String> fields = connectorExcel.retrieveFieldName(tableName);
            System.out.println("Stop herer ");
            config.setInput(inputConfigData.getColumnNames());
            Utils.buildMappingTable(tableView, Utils.buildMappingList(fields), config.getInput());
            return;
        }
        List<String> prevSelectedInputs = config.getSelectedInputs();
        config.getInput().removeAll(prevSelectedInputs);
        Utils.buildMappingTable(tableView, config.getMappings(), config.getInput());
    }

    private void buildTablesMenu(List<String> tables) throws IOException {
        exelTable.getItems().clear();
        EventHandler<ActionEvent> eClickTablesMenu = event -> {
            String tableName = ((MenuItem) event.getSource()).getText();
            exelTable.setText(tableName);
            this.view.setDisable(false);
            this.connectorExcel.setTable(tableName);
//            System.out.println("coma + "+ this.isConfigured + " "+ tableName);
            if (this.isConfigured) {
                try {
                    buildTables(tableName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (!isConfigured) {
                isConfigured = true;
                view.setDisable(false);
                ok.setDisable(false);
            }
        };
        for (String table : tables) {
            MenuItem item = new MenuItem(table);
            item.setOnAction(eClickTablesMenu);
            exelTable.getItems().add(item);
        }

        String configuredTable = this.config.getTable();
//        System.out.println("dlehieu "+configuredTable + "  "+ tables);
        if (configuredTable != null && tables.contains(configuredTable)) {
            exelTable.setText(configuredTable);
            this.connectorExcel.setTable(configuredTable);
//            System.out.println("coma2 "+ this.isConfigured);
            if (this.config != null) {
                buildTables(configuredTable);
            }
            isConfigured = true;
            view.setDisable(false);
            ok.setDisable(false);
        }
    }

    private void buildOutputColumnsTable(String tableName) throws FileNotFoundException {
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

        List<Column> columns = connectorExcel.retrieveColumns(tableName);
        //Add items to table
        output.getItems().addAll(columns);
    }
}
