package app.custom;

import app.controller.ConfigureTransformationStringMergeController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DraggableNodeTransformationStringMerge extends DraggableNodeTransformation {
    private ConfigureTransformationStringMerge config;

    public DraggableNodeTransformationStringMerge() {
        super();

        this.dragIconType = DragIconType.transformation_string_merge;
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
                loader.setLocation(getClass().getResource("/fxml/ConfigureTransformationStringMerge.fxml"));

                Parent root = loader.load();
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Transformation | String Merge");
                stage.setResizable(false);
                //get loader controller
                ConfigureTransformationStringMergeController controller = loader.getController();
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
                            ColumnType.STRING
                    )
            );
        }
        return new ConfigData(columns);
    }

    @Override
    protected boolean execute() {
        Table table = Workspace.getDataset(inputNodeIds[0]);
        List<SelectColumn> selectColumns = config.getSelectColumns();
        List<String> selectedCols = new ArrayList<>();
        for (SelectColumn selectColumn : selectColumns) {
            if (selectColumn.isSelected()) selectedCols.add(selectColumn.getName());
        }
        StringColumn newCol = table.stringColumn(selectedCols.get(0)).copy();
        for (int idx = 1; idx < selectedCols.size(); idx++) {
            newCol = newCol.join(" ", table.stringColumn(selectedCols.get(idx)));
        }
        table.addColumns(newCol.setName(config.getName()));
        Workspace.addDataset(getId(), table);
        return true;
    }
}

