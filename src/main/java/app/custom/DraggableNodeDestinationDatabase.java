package app.custom;

import app.connection.ConnectorDatabase;
import app.controller.ConfigureDBDestinationController;
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

public class DraggableNodeDestinationDatabase extends DraggableNodeDestination {
    private ConfigureDBDestination config;

    public DraggableNodeDestinationDatabase() {
        super();

        this.dragIconType = DragIconType.destination_db;
        printInfo();
    }

    @Override
    public void setupConfigHandler() {
        eConfigClicked = e -> {
            if (inputLinkIds[0] == null) {
                Utils.alertError("Input node is not available");
                return;
            }
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/fxml/ConfigureDBDestination.fxml"));

                Parent root = loader.load();
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("DB Destination Configuration");
                stage.setResizable(false);

                ConfigureDBDestinationController controller = loader.getController();
                controller.presetConfig(this.config, inputData[0], configured);

                stage.setScene(new Scene(root));
                stage.showAndWait();

                if (controller.getCloseType() == CloseType.OK) {
                    this.config = controller.getConfig();
                    this.setConfigured(true);
                }
            } catch (IOException exception) {
                System.out.println("Some errors occurred");
            }
        };
    }

    @Override
    public ConfigData getConfigData() {
        return null;
    }

    protected boolean execute() {
        //Make a copy
        Table dataset = Workspace.getDataset(inputNodeIds[0]);
        //Execute
        List<Mapping> mappings = this.config.getMappings();
        List<String> colsNeedRemoving = new ArrayList<>();
        //Get column names to remove
        for (int i = 0; i < dataset.columnCount(); i++) {
            boolean stt = false;
            String colName = dataset.columnNames().get(i);
            for (Mapping mapping : mappings) {
                if (mapping.getInput().equals(colName)) {
                    //If input is selected, rename table columns with output
                    dataset.column(i).setName(mapping.getOutput());
                    stt = true;
                    break;
                }
            }
            if (!stt) {
                //If none of inputs
                colsNeedRemoving.add(colName);
            }
        }
        //Removing
        for (String colName : colsNeedRemoving) {
            dataset.removeColumns(colName);
        }
        //Write to DB
        try {
            ConnectorDatabase connector = new ConnectorDatabase(this.config.getMetadata());
            connector.Connect();
            connector.insert(dataset, this.config.getTable());
        } catch (Exception e) {
            e.printStackTrace();
            Workspace.appendLn(e.getMessage());
            return false;
        }
        Workspace.addDataset(getId(), dataset);
        return true;
    }
}
