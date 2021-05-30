package app.custom;

import app.connection.ConnectorExcel;
import app.controller.ConfigureExelSourceController;
import app.model.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tech.tablesaw.api.Table;

import java.io.IOException;
import java.util.List;

public class DraggableNodeSourceExcel extends DraggableNodeSource {
    private ConfigureExelSource config;

    public DraggableNodeSourceExcel() {
        super();

        this.dragIconType = DragIconType.source_excel;
        printInfo();
    }

    @Override
    public void setupConfigHandler() {
        eConfigClicked = e -> {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/fxml/ConfigureExelSource.fxml"));

                Parent root = loader.load();
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Excel Source Configuration");
                stage.setResizable(false);

                ConfigureExelSourceController controller = loader.getController();
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
        };
    }

    @Override
    public ConfigData getConfigData() {
        return this.config.getConfigData();
    }


    @Override
    public boolean execute() {
        Table dataset;
        try {
            ConnectorExcel connector = new ConnectorExcel(this.config.getPath());
            connector.setHasLabel(this.config.getHasLabel());
            dataset = connector.retrieveData(this.config.getHasLabel(),this.config.getTable(),this.config.getSelectedExternals(),500);
        } catch (IOException e) {
            e.printStackTrace();
            Workspace.appendLn(e.getMessage());
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
