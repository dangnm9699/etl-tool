package app.controller;

import app.connection.ConnectorCSV;
import app.custom.Workspace;
import app.model.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class ConfigureCSVDestinationController extends ConfigureCSVController {
    @FXML
    private TableView<Mapping> tableView;
    @FXML
    private TableView<Column> output;

    private ConfigureCSVDestination config;
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
//            if (Utils.getSelectedInputsLengthFromMappingList(tableView.getItems()) == 0) {
//                Utils.alertError("There is no selected input");
//                return;
//            }
            config.setMappings(tableView.getItems());
            if (config.getSelectedInputCount() == 0) {
                Utils.alertError("There is no selected input");
                return;
            }

            config.setPath(path.getText());
            this.closeType = CloseType.OK;
//            System.out.println("haslabel"+check.isSelected());
            config.setHasLabel(check.isSelected());
//            isConfigured = true;

            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        };


        eClickBrow = event -> {
            Stage stage = (Stage) csv.getScene().getWindow();
            FileChooser fc = new FileChooser();
            fc.setTitle("Choose a csv file");
            FileChooser.ExtensionFilter csvFilter = new FileChooser.ExtensionFilter("CSV Files", "*.csv");
            fc.getExtensionFilters().add(csvFilter);
            File file = fc.showOpenDialog(stage);
            if (file != null) {
                this.view.setDisable(false);
//                this.connector.setPath(file.getAbsolutePath());
//                System.out.println("dkm" + this.csvPath);
                this.path.setText(file.getAbsolutePath());
            }

        };
        eClickView = event -> {
//            try {
            this.ok.setDisable(false);
            tableView.getItems().clear();
//                this.config.setHasLabel(check.isSelected());
            this.connector = new ConnectorCSV(this.config.getPath());
            this.connector.setHasLabel(check.isSelected());
            buildTables();

//            } catch (e) {
//                System.out.println("Some error occurred");
//            }
        };
        eClickCheck = event -> {
            checkHasLabel = !checkHasLabel;
//            this.connector.setHasLabel(!this.connector.getHasLabel());
//            System.out.println("check"+checkHasLabel);
        };


    }

    public ConfigureCSVDestination getConfig() {
        return this.config;
    }

    public void presetConfig(ConfigureCSVDestination config, ConfigData input, boolean configuredBefore) {
        this.inputConfigData = input;
        this.configuredBefore = configuredBefore;
        if (config == null) {
//            this.inputs = inputs;
            this.config = new ConfigureCSVDestination();
            return;
        }
        this.config = config;
        if (this.inputConfigData != input) {
            this.config.setMappings(null);
        }
//        this.inputConfigData = input;
        this.path.setText(config.getPath());
        this.check.setSelected(config.getHasLabel());
        this.connector = new ConnectorCSV(config.getPath());
        this.connector.setHasLabel(config.getHasLabel());
//        this.checkHasLabel = config.getHasLabel();
//        System.out.println("File dijgoierajgre "+isConfigured);
        this.buildTables();
        ok.setDisable(false);
    }

    private void buildTables() {
        buildMappingTable();
        buildOutputColumnsTable();
    }

    private void buildMappingTable() {
        tableView.getColumns().clear();
        tableView.getItems().clear();
        //
        try {
            String path = this.config.getPath();
            if (path == null) {
                path = this.path.getText();
            } else if (!path.equals(this.path.getText())) {
//                System.out.println("kho hieu1 "+path +"  "+this.path.getText() );
                config.setMappings(null);
                path = this.path.getText();
            }
//            System.out.println("kho hieu "+config.getMappingSelects());
//            reader = new BufferedReader(new FileReader(path));
//            String line = reader.readLine();
            if (!configuredBefore || this.config.getHasLabel() != check.isSelected()) {
//                isConfigured = true;
//                System.out.println("HREREREREREQRERERER "+isConfigured);
                connector.setPath(path);
                connector.setHasLabel(check.isSelected());
//                ConfigData configData = new ConfigData(connector.retrieveColumns());
                List<String> fields = connector.retrieveFieldName();
                config.setInput(inputConfigData.getColumnNames());
                Utils.buildMappingTable(tableView, Utils.buildMappingList(fields), config.getInput());
                return;
            }
            List<String> prevSelectedInputs = config.getSelectedInputs();
            config.getInput().removeAll(prevSelectedInputs);
            Utils.buildMappingTable(tableView, config.getMappings(), config.getInput());
        } catch (FileNotFoundException exception) {
            System.out.println("File not Found");
            Workspace.appendLn(exception.getMessage());
        } catch (IOException exception) {
            exception.printStackTrace();
            Workspace.appendLn(exception.getMessage());
        }
    }

    private void buildOutputColumnsTable() {
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
            List<Column> columns = connector.retrieveColumns();
            //Add items to table
            output.getItems().addAll(columns);
        } catch (IOException exception) {
            Workspace.appendLn(exception.getMessage());
            System.out.println("Some errors occurred");
        }
    }
}
