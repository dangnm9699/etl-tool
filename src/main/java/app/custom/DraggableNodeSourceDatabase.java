package app.custom;

import app.connection.ConnectorDatabase;
import app.controller.ConfigureDBSourceController;
import app.model.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tech.tablesaw.api.Table;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @author dangnm9699
 */
public class DraggableNodeSourceDatabase extends DraggableNodeSource {
    private ConfigureDBSource config;

    public DraggableNodeSourceDatabase() {
        super();

        this.dragIconType = DragIconType.source_db;
        printInfo();
    }

    @Override
    public ConfigData getConfigData() {
        return this.config.getConfigData();
    }

    @Override
    public void setupConfigHandler() {
        eConfigClicked = e -> {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/fxml/ConfigureDBSource.fxml"));

                Parent root = loader.load();
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("DB Source Configuration");
                stage.setResizable(false);

                ConfigureDBSourceController controller = loader.getController();
                controller.presetConfig(this.config);

                stage.setScene(new Scene(root));
                stage.showAndWait();

                if (controller.getCloseType() == CloseType.OK) {
                    this.config = controller.getConfig();
                    this.setConfigured(true);
                    this.sendConfigDataToOutputs();
                }
            } catch (IOException exception) {
                System.out.println("Some errors occurred");
            }
        };
    }

    protected boolean execute() {
        Table dataset;
        try {
            ConnectorDatabase connector = new ConnectorDatabase(this.config.getMetadata());
            connector.Connect();
            dataset = connector.retrieveData(this.config.getTable(), this.config.getSelectedExternals());
        } catch (SQLException e) {
            Workspace.appendLn(e.getMessage());
            e.printStackTrace();
            return false;
        }
        //Rename fields
        List<MappingSelect> mappings = this.config.getMappingSelects();
        for (MappingSelect mapping : mappings) {
            if (mapping.isSelected()) dataset.column(mapping.getExternal()).setName(mapping.getOutput());
        }
        //Add data to hashmap
        Workspace.addDataset(getId(), dataset);
        return true;
    }
}
