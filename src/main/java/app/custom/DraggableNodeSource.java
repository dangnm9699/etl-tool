package app.custom;

import app.model.ConfigData;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author dangnm9699
 */
public abstract class DraggableNodeSource extends DraggableNodeImpl {
    public DraggableNodeSource() {
        super();

        this.requiredInput = 0;
        this.inputNodeIds = new String[requiredInput];
        this.inputLinkIds = new String[this.requiredInput];
        this.inputData = new ConfigData[requiredInput];

        this.requiredOutput = 1;
        this.outputNodeIds = new String[requiredOutput];
        this.outputLinkIds = new String[this.requiredOutput];

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupNode();
        //
        handleOutputLink.setOnDragDetected(eLinkHandleDetected);
    }
}
