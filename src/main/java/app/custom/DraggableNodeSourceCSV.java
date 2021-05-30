package app.custom;

import app.connection.ConnectorCSV;
import app.controller.ConfigureCSVSourceController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tech.tablesaw.api.Table;

import java.io.IOException;
import java.util.List;

public class DraggableNodeSourceCSV extends DraggableNodeSource {
    private ConfigureCSVSource config;

    public DraggableNodeSourceCSV() {
        super();

        this.dragIconType = DragIconType.source_csv;
        printInfo();
    }

    @Override
    public void setupConfigHandler() {
        eConfigClicked = e -> {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/fxml/ConfigureCSVSource.fxml"));

                Parent root = loader.load();
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("CSV Source Configuration");
                stage.setResizable(false);

                ConfigureCSVSourceController controller = loader.getController();
                controller.presetConfig(this.config);

                stage.setScene(new Scene(root));
                stage.showAndWait();
//
                if (controller.getCloseType() == CloseType.OK) {
                    this.config = controller.getConfig();
                    this.setConfigured(true);
                    this.sendConfigDataToOutputs();
                }
//                System.out.println("Some"+this.getConfigData());
//                System.out.println("Some2 " +this.config);
            } catch (IOException exception) {
                System.out.println("Some errors occurred");
            }

//            setConfigured(true);
        };
    }

    @Override
    public ConfigData getConfigData() {
        return this.config.getConfigData();
    }

    protected boolean execute() {
        Table dataset;
        try {
            ConnectorCSV connector = new ConnectorCSV(this.config.getPath());
            connector.setHasLabel(this.config.getHasLabel());
            dataset = connector.retrieveData(this.config.getHasLabel(), this.config.getSelectedExternals(),0);
        } catch (IOException e) {
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
