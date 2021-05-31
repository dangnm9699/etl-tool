package app.controller;

import app.connection.ConnectorCSV;
import app.connection.ConnectorExcel;
import app.custom.Workspace;
import app.model.ConfigureCSVSource;
import app.model.ConfigureExelSource;
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
import java.util.ResourceBundle;

public class ConfigureExelSourceShowSamplesController implements Initializable {
    @FXML
    private TableView<Row> samples;
    @FXML
    private Button ok;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ok.setOnAction(event -> ((Stage) ((Node) event.getSource()).getScene().getWindow()).close());
    }

    public void setup(ConfigureExelSource config) {
        try {
            ConnectorExcel connectorExcel = new ConnectorExcel(config.getPath());
            connectorExcel.setHasLabel(config.getHasLabel());
            connectorExcel.setTable(config.getTable());

            Table table = connectorExcel.retrieveData(config.getHasLabel(),config.getTable(),config.getMappingSelects(),100);
            int numCol = table.columnCount();
            System.out.println("num "+numCol);
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
