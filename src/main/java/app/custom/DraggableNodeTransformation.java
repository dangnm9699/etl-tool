package app.custom;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class DraggableNodeTransformation extends DraggableNodeImpl {
    public DraggableNodeTransformation() {
        super();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupNode();
        //
        handleOutputLink.setOnDragDetected(eLinkHandleDetected);
        handleInputLink.setOnDragDropped(eLinkHandleDropped);
    }
}
