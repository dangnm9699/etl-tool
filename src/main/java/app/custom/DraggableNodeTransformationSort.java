package app.custom;

import app.controller.ConfigureTransformationSortController;
import app.model.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tech.tablesaw.api.Table;

import java.io.IOException;
import java.util.ArrayList;

public class DraggableNodeTransformationSort extends DraggableNodeTransformation {
    private ConfigureTransformationSort config;

    public DraggableNodeTransformationSort() {
        super();

        this.dragIconType = DragIconType.transformation_sort;
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
    protected boolean execute() {
        Table table = Workspace.getDataset(inputNodeIds[0]);
        String col = this.config.getSortBy().getColumn();
        SortOrder order = this.config.getSortBy().getOrder();
        if (order.equals(SortOrder.Ascending)) {
            table = table.sortAscendingOn(col);
        } else {
            table = table.sortDescendingOn(col);
        }
        Workspace.addDataset(getId(), table);
        return true;
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
                loader.setLocation(getClass().getResource("/fxml/ConfigureTransformationSort.fxml"));

                Parent root = loader.load();
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Transformation | Sort");
                stage.setResizable(false);
                //get loader controller
                ConfigureTransformationSortController controller = loader.getController();
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
        return new ConfigData(
                new ArrayList<>(inputData[0].getColumns())
        );
    }
}
