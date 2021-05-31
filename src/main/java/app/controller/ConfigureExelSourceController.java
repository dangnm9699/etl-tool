package app.controller;

import app.connection.ConnectorExcel;
import app.custom.Workspace;
import app.model.*;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.beans.property.SimpleObjectProperty;
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
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tech.tablesaw.api.ColumnType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigureExelSourceController extends ConfigureExelController {
    @FXML
    private TableView<MappingSelect> tableView;
    @FXML
    private TableView<Column> setType;
    @FXML
    private Button set;

    private ConfigureExelSource config;
    protected EventHandler<ActionEvent> eClickSet = null;
    private List<Column> colList = new ArrayList<Column>();

    @Override
    protected void assignEventHandlers() {
        cancel.setOnAction(eClickCancel);
        ok.setOnAction(eClickOK);
        view.setOnAction(eClickView);
        brow.setOnAction(eClickBrow);
        check.setOnAction(eClickCheck);
        set.setOnAction(eClickSet);

    }

    @Override
    protected void buildEventHandlers() {
        eClickSet = event -> {
            List<Column> list = new ArrayList<Column>();
            try {
                this.connectorExcel = new ConnectorExcel(this.path.getText());
                this.connectorExcel.setHasLabel(check.isSelected());
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            try {
                buildOutputColumnsTable(this.exelTable.getText(), list);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        };
        eClickCancel = event -> {
            this.closeType = CloseType.CANCEL;
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        };

        eClickOK = event -> {
            this.closeType = CloseType.OK;
//            this.isConfigured = true;
            System.out.println("debug " + tableView.getItems().get(0).getType().toString());
            config.setPath(path.getText());
//            System.out.println("haslabel"+check.isSelected());
            config.setHasLabel(check.isSelected());
            config.setTable(exelTable.getText());
//            this.connectorExcel.set
            this.colList = setType.getItems();
            List<MappingSelect> mappingSelects = new ArrayList<>();
            int numCol = tableView.getItems().size();
            for (int idx = 0; idx < numCol; idx++) {
                mappingSelects.add(
                        new MappingSelect(
                                tableView.getItems().get(idx).getExternal(),
                                tableView.getItems().get(idx).getOutput(),
                                tableView.getItems().get(idx).isSelected(),
                                colList.get(idx).getType()
                        )
                );
            }
            config.setMappingSelects(mappingSelects);
            isConfigured = true;
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
                } catch (FileNotFoundException e) {
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
                Workspace.appendLn(exception.getMessage());
                exception.printStackTrace();
            }
            this.connectorExcel.setHasLabel(check.isSelected());
            String tableName = this.exelTable.getText();
            try {
                buildMappingSelectTable(tableName);
            } catch (FileNotFoundException e) {
                Workspace.appendLn(e.getMessage());
                e.printStackTrace();
            }

//            } catch (e) {
//                System.out.println("Some error occurred");
//            }
        };
        eClickCheck = event -> {
//            checkHasLabel = !checkHasLabel;
//            System.out.println("check"+checkHasLabel);
        };


    }

    public CloseType getCloseType() {
        return this.closeType;
    }

    public ConfigureExelSource getConfig() {
        return this.config;
    }

    public void presetConfig(ConfigureExelSource config, List<Column> listCol) throws IOException {
        if (config == null) {
            this.config = new ConfigureExelSource();
            return;
        }
        this.config = config;
        this.colList = listCol;
        this.path.setText(config.getPath());
        this.check.setSelected(config.getHasLabel());
//        this.checkHasLabel = config.getHasLabel();
        this.connectorExcel = new ConnectorExcel(this.config.getPath());
        this.connectorExcel.setHasLabel(config.getHasLabel());
        this.connectorExcel.setTable(this.config.getTable());
        this.buildOutputColumnsTable(this.config.getTable(), this.colList);
        this.buildTablesMenu(this.connectorExcel.getListSheet());

//        this.buildMappingSelectTable(this.config.getTable());
        ok.setDisable(false);
    }

    private void buildOutputColumnsTable(String tableName, List<Column> list) throws FileNotFoundException {
        List<Column> listcolumns = connectorExcel.retrieveColumns(tableName, list);
        //Clear content
        setType.getColumns().clear();
        setType.getItems().clear();
        //Setup table columns
        setType.setEditable(true);
        TableColumn<Column, String> name = new TableColumn<>("Name");
        name.setSortable(false);
        name.setEditable(false);
        name.setResizable(false);
        name.prefWidthProperty().bind(setType.widthProperty().divide(2));
        name.setCellValueFactory(cellValue -> new SimpleStringProperty(cellValue.getValue().getName()));
        TableColumn<Column, ColumnType> type = new TableColumn<>("Type");
        type.setSortable(false);
        type.setEditable(true);
        type.setResizable(false);
        type.prefWidthProperty().bind(setType.widthProperty().divide(2));

//        type.setCellValueFactory(cellValue -> new SimpleStringProperty(cellValue.getValue().getType().toString()));
        //Add scolumns to table
        setType.getColumns().add(name);
        setType.getColumns().add(type);

        List<ColumnType> choices = new ArrayList<>();
        choices.add(ColumnType.DOUBLE);
        choices.add(ColumnType.STRING);
        choices.add(ColumnType.INTEGER);
        choices.add(ColumnType.LOCAL_DATE);
        choices.add(ColumnType.FLOAT);

//        choices.add("String");
//        choices.add("Double");
//        choices.add("Float");
//        choices.add("Int");
//        choices.add("Date dd-mm-yyyy");

        type.setCellFactory(ChoiceBoxTableCell.<Column, ColumnType>forTableColumn(new ObservableListWrapper<ColumnType>(choices)));

        type.setOnEditCommit((TableColumn.CellEditEvent<Column, ColumnType> e) ->
        {
            // new value coming from combobox
            ColumnType newValue = e.getNewValue();

            // index of editing person in the tableview
            int index = e.getTablePosition().getRow();

            // person currently being edited
            Column col = (Column) e.getTableView().getItems().get(index);

            // Now you have all necessary info, decide where to set new value
            // to the person or not.
            col.setType(newValue);
        });

//        type.setCellValueFactory(
//                cellData -> {
//                    cellData.getValue().getType();
//                    SimpleStringProperty property = cellData.getValue().;
//                }
//        );


//        type.setCellValueFactory(cell -> {
//            PropertyValueFactory<> property = new PropertyValueFactory<>("lastName");
//            return property;
//        });


        List<Column> columns = listcolumns;
        //Add items to table
        setType.getItems().addAll(columns);
        type.setCellValueFactory(cellValue -> new SimpleObjectProperty<ColumnType>(cellValue.getValue().getType()));

    }


    @FXML
    private void showSamples() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/ConfigureExelSourceShowSamples.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            ConfigureExelSourceShowSamplesController controller = loader.getController();
            ConfigureExelSource configureExelSource = new ConfigureExelSource();
            configureExelSource.setPath(this.path.getText());
            configureExelSource.setHasLabel(this.check.isSelected());
            configureExelSource.setTable(this.exelTable.getText());
//            configureCSVSource.setMetadata(buildMetadata());
//            configureDBSource.setTable(dbTable.getText());
            configureExelSource.setMappingSelects(tableView.getItems());
            controller.setup(configureExelSource);
            stage.setTitle("Samples");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException exception) {
            exception.printStackTrace();
            System.out.println("Some errors occurred");
        }
    }

    private void buildMappingSelectTable(String tableName) throws FileNotFoundException {
//        List<Column> list = new ArrayList<Column>()
//        buildOutputColumnsTable(tableName,list);
        tableView.getColumns().clear();
        tableView.getItems().clear();
        //
        ok.setDisable(false);
        String path = this.config.getPath();
//        System.out.println("compare "+this.config.getTable() +" "+this.exelTable.getText());
        if (path == null) {
            path = this.path.getText();
        } else if (!path.equals(this.path.getText()) || !this.config.getTable().equals(this.exelTable.getText())) {
//                System.out.println("kho hieu1 "+path +"  "+this.path.getText() );
            config.setMappingSelects(null);
            path = this.path.getText();
        }
//            System.out.println("kho hieu "+config.getMappingSelects());
//            reader = new BufferedReader(new FileReader(path));
//            String line = reader.readLine();
        System.out.println("here for two " + (this.colList != this.setType.getItems()));
        if (config.getMappingSelects() == null || this.config.getHasLabel() != check.isSelected()
                || this.colList.equals(this.setType.getItems())) {
            isConfigured = true;
//                System.out.println("chetday "+path);
//                reader = new BufferedReader(new FileReader(path));
//                String line = reader.readLine();
//                ConnectorExcel connectorExcel = new ConnectorExcel(path);
//                String[] line = csvReader.readNext();
            connectorExcel.setExelPath(path);
            connectorExcel.setHasLabel(check.isSelected());
            List<Column> listColumn = setType.getItems();
//            System.out.println("debug 2 "+ listColumn.get(0).getType());
            ConfigData configData = new ConfigData(connectorExcel.retrieveColumns(tableName, listColumn));
//            System.out.println("debug 3 "+ configData.getColumns().get(0).getType());
//            List<String> field = new ArrayList<String>();
////                System.out.println(config.getHasLabel());
//            if (check.isSelected()){
//                field = this.connectorExcel.getField();
//            }
//            else {
//                for (Integer i = 0; i<this.connectorExcel.getField().size();i ++){
//                    field.add("Column "+i.toString());
//                }
//
//            }
            List<MappingSelect> selects = Utils.buildMappingSelectList(configData.getColumns());
            Utils.buildMappingSelectsTable(tableView, selects);
            return;
        }
//            if (config.getMappingSelects() == null) {
//                return;
//            }
        System.out.println("here for once");
        Utils.buildMappingSelectsTable(tableView, config.getMappingSelects());
    }

    private void buildTablesMenu(List<String> tables) throws FileNotFoundException {
        exelTable.getItems().clear();
        EventHandler<ActionEvent> eClickTablesMenu = event -> {
            String tableName = ((MenuItem) event.getSource()).getText();
            exelTable.setText(tableName);
            this.view.setDisable(false);
            this.connectorExcel.setTable(tableName);
            System.out.println("coma + " + this.isConfigured + " " + tableName);
            if (this.isConfigured) {
                try {
                    buildMappingSelectTable(tableName);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            if (!isConfigured) {
                isConfigured = true;
                view.setDisable(false);
//                ok.setDisable(false);
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
                buildMappingSelectTable(configuredTable);
            }
            isConfigured = true;
            view.setDisable(false);
//            ok.setDisable(false);
        }
    }

    public List<Column> getColumnList() {
        return this.colList;
    }
}
