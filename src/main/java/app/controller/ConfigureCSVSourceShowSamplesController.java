package app.controller;

import app.connection.ConnectorCSV;
import app.connection.ConnectorDatabase;
import app.custom.Workspace;
import app.model.ConfigureCSVSource;
import app.model.ConfigureDBSource;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ConfigureCSVSourceShowSamplesController implements Initializable {
    @FXML
    private TableView<Row> samples;
    @FXML
    private Button ok;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ok.setOnAction(event -> ((Stage) ((Node) event.getSource()).getScene().getWindow()).close());
    }

    public void setup(ConfigureCSVSource config) {
        try {
            ConnectorCSV connectorCSV = new ConnectorCSV(config.getPath());
            connectorCSV.setHasLabel(config.getHasLabel());
            Table table = connectorCSV.retrieveData(config.getHasLabel(),config.getSelectedExternals(),100);
            int numCol = table.columnCount();
            for (int idx = 0; idx < numCol; idx++) {
                TableColumn<Row, Object> column = new TableColumn<>(config.getSelectedOutputs().get(idx));
                int finalIdx = idx;
                column.prefWidthProperty().bind(samples.widthProperty().divide(numCol));
                column.setCellValueFactory(cellValue -> new SimpleObjectProperty<>(cellValue.getValue().getObject(finalIdx)));
                samples.getColumns().add(column);
            }
            int numRow = table.rowCount();
            for (int idx = 0; idx < numRow; idx++) {
                samples.getItems().add(table.row(idx));
            }
        } catch (IOException exception) {
            Workspace.appendLn(exception.getMessage());
            exception.printStackTrace();
        }
    }
}
