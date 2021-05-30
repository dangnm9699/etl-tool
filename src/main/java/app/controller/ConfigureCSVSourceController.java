package app.controller;

import app.connection.ConnectorCSV;
import app.custom.Workspace;
import app.model.*;
import com.opencsv.CSVParser;
import com.opencsv.exceptions.CsvValidationException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class ConfigureCSVSourceController extends ConfigureCSVController {
    @FXML
    private TableView<MappingSelect> tableView;
    @FXML
    private Button sample;
    private ConfigureCSVSource config;
//    protected EventHandler<ActionEvent> eClickSample = null;


    @Override
    protected void assignEventHandlers() {
        cancel.setOnAction(eClickCancel);
        ok.setOnAction(eClickOK);
        view.setOnAction(eClickView);
        brow.setOnAction(eClickBrow);
        check.setOnAction(eClickCheck);
//        sample.setOnAction(eClickSample);

    }

    @Override
    protected void buildEventHandlers() {
//        eClickSample = event ->{
////            Table table = connector.retrieveData(check.isSelected(),)
//        };
        eClickCancel = event -> {
            this.closeType = CloseType.CANCEL;
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        };

        eClickOK = event -> {
            this.closeType = CloseType.OK;
            config.setMappingSelects(tableView.getItems());
            config.setPath(path.getText());
//            System.out.println("haslabel"+check.isSelected());
            config.setHasLabel(check.isSelected());
            isConfigured = true;
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
                view.setDisable(false);
                this.ok.setDisable(false);
//                System.out.println("dkm" + file.getAbsolutePath());
//                this.connector.setPath(file.getAbsolutePath().toString());
//                System.out.println("dkm" + this.csvPath);
//                this.config.setPath(file.getAbsolutePath());
                this.path.setText(file.getAbsolutePath());
            }
        };
        eClickView = event -> {
//            try {
            tableView.getItems().clear();
//                this.config.setHasLabel(check.isSelected());
            this.connector = new ConnectorCSV(this.config.getPath());
            this.connector.setHasLabel(check.isSelected());
            sample.setDisable(false);
            buildMappingSelectTable();

//            } catch (e) {
//                System.out.println("Some error occurred");
//            }
        };
        eClickCheck = event -> {
//            this.connector.setHasLabel(!this.connector.getHasLabel());
//            this.config.setHasLabel(!this.config.getHasLabel());
//            System.out.println("check"+checkHasLabel);
        };


    }

    public ConfigureCSVSource getConfig() {
        return this.config;
    }

    //    @Override
//    protected void setConfig(MetadataDB metadata) {
//        config.setMetadata(metadata);
//    }
//}
    public void presetConfig(ConfigureCSVSource config) {
        if (config == null) {
            this.config = new ConfigureCSVSource();
//            this.connector = new ConnectorCSV();
            return;
        }
        this.config = config;

        this.path.setText(config.getPath());
        this.check.setSelected(config.getHasLabel());
        this.connector = new ConnectorCSV(config.getPath());
        this.connector.setHasLabel(config.getHasLabel());
//        this.checkHasLabel = config.getHasLabel();
        this.buildMappingSelectTable();
        view.setDisable(false);
        ok.setDisable(false);
    }

    @FXML
    private void showSamples() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/ConfigureCSVSourceShowSamples.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            ConfigureCSVSourceShowSamplesController controller = loader.getController();
            ConfigureCSVSource configureCSVSource = new ConfigureCSVSource();
            configureCSVSource.setPath(this.path.getText());
            configureCSVSource.setHasLabel(this.check.isSelected());
//            configureCSVSource.setMetadata(buildMetadata());
//            configureDBSource.setTable(dbTable.getText());
            configureCSVSource.setMappingSelects(tableView.getItems());
            controller.setup(configureCSVSource);
            stage.setTitle("Samples");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException exception) {
            exception.printStackTrace();
            System.out.println("Some errors occurred");
        }
    }

    private void buildMappingSelectTable() {
        tableView.getColumns().clear();
        tableView.getItems().clear();
        //
        try {
//            BufferedReader reader;
            String path = this.config.getPath();
            if (path == null) {
                path = this.path.getText();
            } else if (!path.equals(this.path.getText())) {
//                System.out.println("kho hieu1 "+path +"  "+this.path.getText() );
                config.setMappingSelects(null);
                path = this.path.getText();
            }
//            System.out.println("kho hieu "+config.getMappingSelects());
//            reader = new BufferedReader(new FileReader(path));
//            String line = reader.readLine();
            if (config.getMappingSelects() == null || this.config.getHasLabel() != check.isSelected()) {
                isConfigured = true;
                connector.setPath(path);
                connector.setHasLabel(check.isSelected());
                ConfigData configData = new ConfigData(connector.retrieveColumns());
//                System.out.println("chetday "+path);
//                if (check.isSelected()){
//                    field = Arrays.asList(line);
//                }
//                else {
//                    for (Integer i = 0; i<line.length;i ++){
//                        field.add("Column "+i.toString());
//                    }
//
//                }
                List<MappingSelect> selects = Utils.buildMappingSelectList(configData.getColumns());
                Utils.buildMappingSelectsTable(tableView, selects);
                return;
            }
//            if (config.getMappingSelects() == null) {
//                return;
//            }

            Utils.buildMappingSelectsTable(tableView, config.getMappingSelects());
        } catch (FileNotFoundException exception) {
            Workspace.appendLn(exception.getMessage());
            System.out.println("File not Found");
        } catch (IOException exception) {
            exception.printStackTrace();
            Workspace.appendLn(exception.getMessage());
        }
    }
}
