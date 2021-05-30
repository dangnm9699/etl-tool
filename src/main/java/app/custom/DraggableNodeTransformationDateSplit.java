package app.custom;

import app.controller.ConfigureTransformationDateSplitController;
import app.model.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tech.tablesaw.api.Table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DraggableNodeTransformationDateSplit extends DraggableNodeTransformation {
    private ConfigureTransformationDateSplit config;

    public DraggableNodeTransformationDateSplit() {
        super();

        this.dragIconType = DragIconType.transformation_date_split;
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
                loader.setLocation(getClass().getResource("/fxml/ConfigureTransformationDateSplit.fxml"));

                Parent root = loader.load();
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Transformation | Date Split");
                stage.setResizable(false);
                //get loader controller
                ConfigureTransformationDateSplitController controller = loader.getController();
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
        return new ConfigData(columns);
    }

    @Override
    protected boolean execute() {
        Table table = Workspace.getDataset(inputNodeIds[0]);
        SelectColumn selectColumn = config.getSelectColumn();
        selectColumn.split(table);
        Workspace.addDataset(getId(), table);
        return true;
    }
}

