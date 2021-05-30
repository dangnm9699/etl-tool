package app.controller;

import app.connection.ConnectorCSV;
import app.model.CloseType;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class ConfigureCSVController implements Initializable {
    @FXML
    protected AnchorPane csv;
    @FXML
    protected TextField path;
    //    @FXML
//    protected Boolean withLabel;
    @FXML
    protected Button view;
    @FXML
    protected Button brow;
    @FXML
    protected CheckBox check;
    @FXML
    protected Button cancel;
    @FXML
    protected Button ok;
    @FXML
    protected TableView tableView;

    protected EventHandler<ActionEvent> eClickCancel = null;
    protected EventHandler<ActionEvent> eClickOK = null;
    protected EventHandler<ActionEvent> eClickView = null;
    protected EventHandler<ActionEvent> eClickBrow = null;
    protected EventHandler<ActionEvent> eClickCheck = null;


    //    protected ConnectorDatabase connector;
    protected ConnectorCSV connector;
    protected boolean isConfigured = false;
    protected CloseType closeType;
//    protected String csvPath;
//    protected Boolean checkHasLabel = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        buildEventHandlers();
        ok.setDisable(true);
        view.setDisable(true);
        assignEventHandlers();
//        assignEventHandlers();
    }


    public CloseType getCloseType() {
        return this.closeType;
    }

    protected abstract void buildEventHandlers();

    protected abstract void assignEventHandlers();

//    protected abstract void buildTab2();
//    protected abstract void clearTables();
//    protected abstract void setConfig(MetadataDB metadata);
}
