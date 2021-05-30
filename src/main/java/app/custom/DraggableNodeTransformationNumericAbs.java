package app.custom;

import app.controller.ConfigureTransformationNumericController;
import app.model.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DraggableNodeTransformationNumericAbs extends DraggableNodeTransformation {
    private ConfigureTransformationNumeric config;

    public DraggableNodeTransformationNumericAbs() {
        super();

        this.dragIconType = DragIconType.transformation_numeric_abs;
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
                loader.setLocation(getClass().getResource("/fxml/ConfigureTransformationNumeric.fxml"));

                Parent root = loader.load();
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Transformation | Numeric Absolute");
                stage.setResizable(false);
                //get loader controller
                ConfigureTransformationNumericController controller = loader.getController();
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
        List<Column> columns;
        if (configured) {
            columns = new ArrayList<>();
            for (Column column : inputData[0].getColumns()) {
                boolean added = false;
                for (SelectColumn selectColumn : config.getNumericSelects()) {
                    if (column.getName().equals(selectColumn.getName())) {
                        added = true;
                        columns.add(
                                new Column(
                                        selectColumn.getName(),
                                        ColumnType.DOUBLE
                                ));
                    }
                }
                if (!added) {
                    columns.add(
                            new Column(
                                    column.getName(),
                                    column.getType()
                            )
                    );
                }
            }
        } else {
            columns = new ArrayList<>(inputData[0].getColumns());
        }
        return new ConfigData(columns);
    }

    @Override
    protected boolean execute() {
        Table table = Workspace.getDataset(inputNodeIds[0]);
        for (SelectColumn selectColumn : this.config.getNumericSelects()) {
            if (selectColumn.isSelected()) {
                selectColumn.abs(table);
            }
        }
        Workspace.addDataset(getId(), table);
        return true;
    }
}
