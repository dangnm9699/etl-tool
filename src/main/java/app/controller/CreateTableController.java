package app.controller;

import app.connection.ConnectorDatabase;
import app.custom.Workspace;
import app.model.Column;
import app.model.ConfigData;
import app.model.Driver;
import app.model.MetadataDB;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import tech.tablesaw.api.ColumnType;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateTableController implements Initializable {
    @FXML
    private TextArea query;
    @FXML
    private Button exit;
    @FXML
    private Button parse;
    @FXML
    private Label inform;

    private EventHandler<ActionEvent> eClickParse = null;
    private EventHandler<ActionEvent> eClickExit = null;

    private MetadataDB metadataDB;
    private String table;
    private boolean parsed = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        buildEventHandlers();
        assignEventHandlers();
        inform.setStyle(" -fx-text-alignment: justify");
    }

    public boolean isParsed() {
        return parsed;
    }

    public void setParsed(boolean parsed) {
        this.parsed = parsed;
        if (parsed) {
            inform.setStyle("-fx-text-fill: mediumseagreen");
            inform.setText("Parsed successfully");
        } else {
            inform.setStyle("-fx-text-fill: tomato");
            inform.setText("Parsed failed");
        }
    }

    public void preset(MetadataDB metadataDB, ConfigData configData) {
        this.metadataDB = metadataDB;
        buildQuery(configData);
    }

    public String getTable() {
        return table;
    }

    private void buildEventHandlers() {
        eClickParse = event -> {
            try {
                ConnectorDatabase connectorDatabase = new ConnectorDatabase(metadataDB);
                connectorDatabase.Connect();
                connectorDatabase.create(query.getText());
                setParsed(true);
                Pattern pattern = Pattern.compile("CREATE TABLE (.+) \\(");
                Matcher matcher = pattern.matcher(query.getText());
                if (matcher.find()) {
                    table = matcher.group(1);
                    if (table.charAt(0) == '`' || table.charAt(0) == '[') {
                        table = table.substring(1, table.length() - 1);
                    }
                }
            } catch (SQLException exception) {
                Workspace.appendLn(exception.getMessage());
                exception.printStackTrace();
                setParsed(false);
            }
        };
        eClickExit = event -> ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    private void assignEventHandlers() {
        parse.setOnAction(eClickParse);
        exit.setOnAction(eClickExit);
    }

    private void buildQuery(ConfigData configData) {
        StringBuilder stringBuilder = new StringBuilder();
        if (metadataDB.getDriver().equals(Driver.SqlServer)) {
            stringBuilder.append("CREATE TABLE [Automatic] (");
        } else {
            stringBuilder.append("CREATE TABLE `Automatic` (");
        }
        for (Column column : configData.getColumns()) {
            if (metadataDB.getDriver().equals(Driver.SqlServer)) {
                stringBuilder.append("\n[?] ".replace("?", column.getName()));
                ColumnType type = column.getType();
                if (type.equals(ColumnType.INTEGER)) {
                    stringBuilder.append("INT,");
                } else if (type.equals(ColumnType.DOUBLE)) {
                    stringBuilder.append("FLOAT,");
                } else if (type.equals(ColumnType.STRING)) {
                    stringBuilder.append("VARCHAR(255),");
                } else if (type.equals(ColumnType.LOCAL_DATE)) {
                    stringBuilder.append("DATE,");
                }
            } else {
                stringBuilder.append("\n`?` ".replace("?", column.getName()));
                ColumnType type = column.getType();
                if (type.equals(ColumnType.INTEGER)) {
                    stringBuilder.append("INTEGER,");
                } else if (type.equals(ColumnType.DOUBLE)) {
                    stringBuilder.append("DOUBLE,");
                } else if (type.equals(ColumnType.STRING)) {
                    stringBuilder.append("VARCHAR(255),");
                } else if (type.equals(ColumnType.LOCAL_DATE)) {
                    stringBuilder.append("DATE,");
                }
            }
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1).append("\n)");
        query.setText(stringBuilder.toString());
    }
}
