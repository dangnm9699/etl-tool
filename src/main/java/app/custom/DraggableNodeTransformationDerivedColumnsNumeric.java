package app.custom;

import app.controller.ConfigureTransformationDerivedColumnsNumericController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;

import java.io.IOException;
import java.util.ArrayList;

public class DraggableNodeTransformationDerivedColumnsNumeric extends DraggableNodeTransformation {
    private ConfigureTransformationDerivedColumnsNumeric config;

    public DraggableNodeTransformationDerivedColumnsNumeric() {
        super();
        this.dragIconType = DragIconType.transformation_numeric_derived_columns;
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
                loader.setLocation(getClass().getResource("/fxml/ConfigureTransformationDerivedColumnsNumeric.fxml"));

                Parent root = loader.load();
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Numeric | Derived Columns");
                stage.setResizable(false);
                //get loader controller
                ConfigureTransformationDerivedColumnsNumericController controller = loader.getController();
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
        ConfigData configData = new ConfigData(
                new ArrayList<>(inputData[0].getColumns())
        );
        if (configured) {
            for (NumericDerivedColumn numericDerivedColumn : config.getDerivedColumns()) {
                configData.addDoubleColumn(numericDerivedColumn.getName());
            }
        }
        return configData;
    }

    protected boolean execute() {
        Table table = Workspace.getDataset(inputNodeIds[0]);
        if (config != null && config.getDerivedColumns() != null) {
            for (NumericDerivedColumn numericDerivedColumn : config.getDerivedColumns()) {
                DoubleColumn column = numericDerivedColumn.calculate(table);
                table.addColumns(column);
            }
        }
        Workspace.addDataset(getId(), table);
        return true;
    }
}
