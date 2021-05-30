package app.controller;

import app.connection.ConnectorDatabase;
import app.model.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class ConfigureSourceDBController implements Initializable {
    @FXML
    private Tab tabConnection;
    @FXML
    private Tab tabColumns;
    @FXML
    private MenuButton srcDriver;
    @FXML
    private TextField srcHost;
    @FXML
    private TextField srcPort;
    @FXML
    private TextField srcUsername;
    @FXML
    private PasswordField srcPassword;
    @FXML
    private TextField srcDatabase;
    @FXML
    private MenuButton srcTable;
    @FXML
    private TableView<MappingSelect> mappingTable;
    @FXML
    private Button testConn;
    @FXML
    private Button cancel;
    @FXML
    private Button ok;
    @FXML
    private ImageView imageView;

    private EventHandler<ActionEvent> eClickCancel = null;
    private EventHandler<ActionEvent> eClickOK = null;
    private EventHandler<ActionEvent> eClickTestConn = null;
    private EventHandler<ActionEvent> eClickDriversMenu = null;

    private ConnectorDatabase connector;
    private boolean isConfigured = false;
    private CloseType closeType;
    private ConfigureSourceDB config;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tabColumns.setDisable(true);
        ok.setDisable(true);
        buildEventHandlers();
        buildDriversMenu();
        assignEventHandlers();
    }

    public CloseType getCloseType() {
        return this.closeType;
    }

    public ConfigureSourceDB getConfig() {
        return this.config;
    }

    public void presetConfig(ConfigureSourceDB config) {
        if (config == null) {
            this.config = new ConfigureSourceDB();
            return;
        }
        this.config = config;
        //Set metadata
        this.srcDriver.setText(this.config.getDriver().toString());
        this.srcHost.setText(this.config.getHost());
        this.srcPort.setText(this.config.getPort());
        this.srcUsername.setText(this.config.getUsername());
        this.srcPassword.setText(this.config.getPassword());
        this.srcDatabase.setText(this.config.getDatabase());
        //
        MetadataDB metadata = this.config.getMetadata();
        this.connect(metadata);
    }

    private void connect(MetadataDB metadata) {
        connector = new ConnectorDatabase(metadata);
        connector.Connect();
        boolean connected = connector.getConnection() != null;
        if (connected) {
            setImageView(true);
            setConfig(metadata);
            //Open tab "Columns"
            tabColumns.setDisable(false);
            //
            buildTabColumns();
        } else {
            setImageView(false);
        }
    }

    private void buildEventHandlers() {
        eClickCancel = event -> {
            this.closeType = CloseType.CANCEL;
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        };

        eClickOK = event -> {
            this.closeType = CloseType.OK;
            config.setTable(srcTable.getText());
            config.setSelects(mappingTable.getItems());
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        };

        eClickDriversMenu = event -> srcDriver.setText(((MenuItem) event.getSource()).getText());

        eClickTestConn = event -> {
            this.clear();
            MetadataDB metadata = buildMetadata();
            boolean validate = !wrongMetadata(metadata);
            if (!validate) setImageView(false);
            else this.connect(metadata);
        };
    }

    private void buildTabColumns() {
        try {
            List<String> tables = connector.retrieveTables();
            buildTablesMenu(tables);
        } catch (SQLException exception) {
            System.out.println("Some error occurred");
        }
    }

    private void setConfig(MetadataDB metadata) {
        config.setDriver(metadata.getDriver());
        config.setHost(metadata.getHost());
        config.setPort(metadata.getPort());
        config.setUsername(metadata.getUsername());
        config.setPassword(metadata.getPassword());
        config.setDatabase(metadata.getDatabase());
    }

    private void setImageView(boolean ok) {
        String image = "/images/";
        if (ok) {
            image = image + "correct.png";
        } else {
            image = image + "incorrect.png";
        }
        imageView.setImage(new Image(image, 30, 30, false, true));
    }

    private void assignEventHandlers() {
        cancel.setOnAction(eClickCancel);
        ok.setOnAction(eClickOK);
        testConn.setOnAction(eClickTestConn);
    }

    private void buildTablesMenu(List<String> tables) {
        srcTable.getItems().clear();
        EventHandler<ActionEvent> eClickTablesMenu = event -> {
            String tableName = ((MenuItem) event.getSource()).getText();
            srcTable.setText(tableName);
            buildMappingSelectTable(tableName);
            if (!isConfigured) {
                isConfigured = true;
                ok.setDisable(false);
            }
        };
        for (String table : tables) {
            MenuItem item = new MenuItem(table);
            item.setOnAction(eClickTablesMenu);
            srcTable.getItems().add(item);
        }

        String configuredTable = this.config.getTable();
        if (configuredTable != null && tables.contains(configuredTable)) {
            srcTable.setText(configuredTable);
            buildMappingSelectTable(configuredTable);
            isConfigured = true;
            ok.setDisable(false);
        }
    }

    private void buildMappingSelectTable(String tableName) {
        mappingTable.getColumns().clear();
        mappingTable.getItems().clear();
        //
        try {
            List<String> fields = connector.retrieveFieldName(tableName);
            if (config.getSelects() != null) {
                List<String> externals = MappingSelectUtils.getExternalList(config.getSelects());
                int diff = MappingSelectUtils.compare2ExternalLists(fields, externals);
                System.out.println(diff);
                if (diff == 0) {
                    MappingSelectUtils.buildTable(mappingTable, config.getSelects());
                    return;
                }
            }
            List<MappingSelect> selects = MappingSelectUtils.buildMappingList(fields);
            MappingSelectUtils.buildTable(mappingTable, selects);
        } catch (SQLException exception) {
            System.out.println("Some errors occurred");
        }
    }

    private void buildDriversMenu() {
        List<MenuItem> items = Arrays.asList(
                new MenuItem(Driver.MySQL.toString()),
                new MenuItem(Driver.SqlServer.toString()),
                new MenuItem(Driver.PostgreSQL.toString())
        );

        for (MenuItem item : items) {
            item.setOnAction(eClickDriversMenu);
            srcDriver.getItems().add(item);
        }
    }

    private boolean wrongMetadata(MetadataDB metadata) {
        return metadata == null
                || metadata.getHost().equals("")
                || metadata.getPort().equals("")
                || metadata.getUsername().equals("")
                || metadata.getPassword().equals("")
                || metadata.getDatabase().equals("");
    }

    private MetadataDB buildMetadata() {
        try {
            return new MetadataDB(
                    Driver.valueOf(this.srcDriver.getText()),
                    this.srcHost.getText(),
                    this.srcPort.getText(),
                    this.srcUsername.getText(),
                    this.srcPassword.getText(),
                    this.srcDatabase.getText()
            );
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private void clear() {
        this.ok.setDisable(true);
        this.tabColumns.setDisable(true);
        //
        this.srcTable.getItems().clear();
        this.srcTable.setText("Choose table");
        this.mappingTable.getColumns().clear();
        this.mappingTable.getItems().clear();
        //
        this.isConfigured = false;
    }
}
