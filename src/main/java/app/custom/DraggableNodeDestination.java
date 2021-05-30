package app.custom;

import app.model.ConfigData;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author dangnm9699
 */
public abstract class DraggableNodeDestination extends DraggableNodeImpl {
    public DraggableNodeDestination() {
        super();

        this.requiredInput = 1;
        this.inputNodeIds = new String[requiredInput];
        this.inputLinkIds = new String[this.requiredInput];
        this.inputData = new ConfigData[requiredInput];

        this.requiredOutput = 0;
        this.outputNodeIds = new String[requiredOutput];
        this.outputLinkIds = new String[this.requiredOutput];
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupNode();
        //
        handleInputLink.setOnDragDropped(eLinkHandleDropped);
    }
}