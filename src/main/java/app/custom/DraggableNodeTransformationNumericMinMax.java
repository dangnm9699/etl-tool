package app.custom;

import app.controller.ConfigureTransformationNumericMinMaxController;
import app.model.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DraggableNodeTransformationNumericMinMax extends DraggableNodeTransformation {
    private ConfigureTransformationNumericMinMax config;

    public DraggableNodeTransformationNumericMinMax() {
        super();

        this.dragIconType = DragIconType.transformation_numeric_min_max;
        this.requiredInput = 1;
        this.inputNodeIds = new String[requiredInput];
        this.inputLinkIds = new String[this.requiredInput];
        this.inputData = new ConfigData[requiredInput];

        this.requiredOutput = 1;
        this.outputNodeIds = new String[requiredOutput];
        this.outputLinkIds = new String[this.requiredOutput];

        printInfo();
    }

    @Override
    public void setupConfigHandler() {
        eConfigClicked = e -> {
            if (inputLinkIds[0] == null) {
                Utils.alertError("Input node is not available");
                return;
            }
            //Open configure view
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/fxml/ConfigureTransformationNumericMinMax.fxml"));

                Parent root = loader.load();
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Transformation | Numeric MinMax");
                stage.setResizable(false);
                //get loader controller
                ConfigureTransformationNumericMinMaxController controller = loader.getController();
                //preset config
                controller.presetConfig(this.config, inputData[0], configured);

                stage.setScene(new Scene(root));
                stage.showAndWait();

                if (controller.getCloseType() == CloseType.OK) {
                    this.config = controller.getConfig();
                    this.setConfigured(true);
                    this.sendConfigDataToOutputs();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
                System.out.println("Some errors occurred");
            }
        };
    }

    @Override
    public ConfigData getConfigData() {
        List<Column> columns = new ArrayList<>(inputData[0].getColumns());
        if (configured) {
            columns.add(
                    new Column(
                            config.getName(),
                            ColumnType.DOUBLE
                    )
            );
        }
        return new ConfigData(columns);
    }

    @Override
    protected boolean execute() {
        Table table = Workspace.getDataset(inputNodeIds[0]);
        List<SelectColumn> selectColumns = config.getNumericSelects();
        List<String> selectedCols = new ArrayList<>();
        for (SelectColumn selectColumn : selectColumns) {
            if (selectColumn.isSelected()) selectedCols.add(selectColumn.getName());
        }
        boolean mode = config.getMinMax().equals("MIN");
        List<Double> newCol = new ArrayList<>();
        for (Row row : table) {
            double tmp = getDoubleVal(row, selectedCols.get(0));
            for (String name : selectedCols) {
                double tmp2 = getDoubleVal(row, name);
                if ((tmp > tmp2) == mode) tmp = tmp2;
            }
            newCol.add(tmp);
        }
        table.addColumns(
                DoubleColumn.create(config.getName(), newCol)
        );
        Workspace.addDataset(getId(), table);
        return true;
    }

    private double getDoubleVal(Row row, String colName) {
        ColumnType type = row.getColumnType(colName);
        double tmp = 0;
        if (type.equals(ColumnType.INTEGER)) {
            tmp = row.getInt(colName);
        } else if (type.equals(ColumnType.DOUBLE)) {
            tmp = row.getDouble(colName);
        } else if (type.equals(ColumnType.FLOAT)) {
            tmp = row.getFloat(colName);
        } else if (type.equals(ColumnType.LONG)) {
            tmp = row.getLong(colName);
        } else if (type.equals(ColumnType.SHORT)) {
            tmp = row.getShort(colName);
        }
        return tmp;
    }
}

