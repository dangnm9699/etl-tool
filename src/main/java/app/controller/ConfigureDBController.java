package app.controller;

import app.connection.ConnectorDatabase;
import app.custom.Workspace;
import app.model.CloseType;
import app.model.Driver;
import app.model.MetadataDB;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public abstract class ConfigureDBController implements Initializable {
    @FXML
    protected Tab tab1;
    @FXML
    protected Tab tab2;
    @FXML
    protected MenuButton dbDriver;
    protected final EventHandler<ActionEvent> eClickDriversMenu =
            event -> dbDriver.setText(((MenuItem) event.getSource()).getText());
    @FXML
    protected TextField dbHost;
    @FXML
    protected TextField dbPort;
    @FXML
    protected TextField dbUsername;
    @FXML
    protected PasswordField dbPassword;
    @FXML
    protected TextField dbDatabase;
    @FXML
    protected MenuButton dbTable;
    @FXML
    protected Button testConn;
    @FXML
    protected Button cancel;
    @FXML
    protected Button ok;
    @FXML
    protected ImageView imageView;
    protected EventHandler<ActionEvent> eClickCancel = null;
    protected EventHandler<ActionEvent> eClickOK = null;
    protected EventHandler<ActionEvent> eClickTestConn = null;
    protected ConnectorDatabase connector;
    protected boolean isConfigured = false;
    protected CloseType closeType;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tab2.setDisable(true);
        ok.setDisable(true);
        buildEventHandlers();
        buildDriversMenu();
        assignEventHandlers();
    }

    public CloseType getCloseType() {
        return this.closeType;
    }

    public boolean validateMetadata(MetadataDB metadata) {
        return metadata != null
                && !metadata.getHost().equals("")
                && !metadata.getPort().equals("")
                && !metadata.getUsername().equals("")
                && !metadata.getPassword().equals("")
                && !metadata.getDatabase().equals("");
    }

    public void setImageView(boolean ok) {
        String image = "/images/";
        if (ok) {
            image = image + "correct.png";
        } else {
            image = image + "incorrect.png";
        }
        imageView.setImage(new Image(image, 30, 30, false, true));
    }

    public List<MenuItem> getMenuItems() {
        return Arrays.asList(
                new MenuItem(Driver.MySQL.toString()),
                new MenuItem(Driver.SqlServer.toString()),
                new MenuItem(Driver.PostgreSQL.toString())
        );
    }

    protected void setMetadata(MetadataDB metadata) {
        this.dbDriver.setText(metadata.getDriver().toString());
        this.dbHost.setText(metadata.getHost());
        this.dbPort.setText(metadata.getPort());
        this.dbUsername.setText(metadata.getUsername());
        this.dbPassword.setText(metadata.getPassword());
        this.dbDatabase.setText(metadata.getDatabase());
    }

    protected void clear() {
        this.ok.setDisable(true);
        this.tab2.setDisable(true);
        this.dbTable.getItems().clear();
        this.dbTable.setText("Choose table");
        //Clear all tables content
        clearTables();
        this.isConfigured = false;
    }

    protected MetadataDB buildMetadata() {
        try {
            return new MetadataDB(
                    Driver.valueOf(this.dbDriver.getText()),
                    this.dbHost.getText(),
                    this.dbPort.getText(),
                    this.dbUsername.getText(),
                    this.dbPassword.getText(),
                    this.dbDatabase.getText()
            );
        } catch (IllegalArgumentException exception) {
            Workspace.appendLn(exception.getMessage());
            return null;
        }
    }

    protected void buildDriversMenu() {
        List<MenuItem> items = getMenuItems();
        for (MenuItem item : items) {
            item.setOnAction(eClickDriversMenu);
            dbDriver.getItems().add(item);
        }
    }

    protected void connect(MetadataDB metadata) {
        connector = new ConnectorDatabase(metadata);
        connector.Connect();
        boolean connected = connector.getConnection() != null;
        if (connected) {
            setImageView(true);
            setConfig(metadata);
            //Open tab "Columns"
            tab2.setDisable(false);
            //
            buildTab2();
        } else {
            setImageView(false);
        }
    }

    protected abstract void assignEventHandlers();

    protected abstract void buildEventHandlers();

    protected abstract void buildTab2();

    protected abstract void clearTables();

    protected abstract void setConfig(MetadataDB metadata);
}
