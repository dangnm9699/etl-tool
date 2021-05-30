package app.custom;

import app.controller.ConfigureTransformationRemoveNullController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tech.tablesaw.api.Table;

import java.io.IOException;
import java.util.List;

public class DraggableNodeTransformationRemoveNull extends DraggableNodeTransformation {
    private ConfigureTransformationRemoveNull config;

    public DraggableNodeTransformationRemoveNull() {
        super();

        this.dragIconType = DragIconType.transformation_remove_null;
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
                loader.setLocation(getClass().getResource("/fxml/ConfigureTransformationRemoveNull.fxml"));

                Parent root = loader.load();
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Transformation | Remove Null");
                stage.setResizable(false);
                //get loader controller
                ConfigureTransformationRemoveNullController controller = loader.getController();
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
        return this.config.getConfigData();
    }

    protected boolean execute() {
        //Make a copy
        Table dataset = Workspace.getDataset(inputNodeIds[0]);
        //Execute
        List<Integer> indexes = this.config.getIndexOfSelectedColumns();
        for (int index : indexes) {
            dataset = dataset.dropWhere(dataset.column(index).isMissing());
        }
        Workspace.addDataset(getId(), dataset);
        return true;
    }
}
