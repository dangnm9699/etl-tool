package app.custom;

import app.model.ConfigData;
import app.model.DragContainer;
import app.model.DragIconType;
import app.model.NodeLinkType;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author dangnm9699
 */
public abstract class DraggableNodeImpl extends AnchorPane implements Initializable, DraggableNode, Callable<Void> {

    protected final DraggableNodeImpl self;
    @FXML
    protected AnchorPane handleInputLink;
    @FXML
    protected AnchorPane handleOutputLink;
    @FXML
    protected AnchorPane handleConfig;
    @FXML
    protected ImageView imageConfigured;
    @FXML
    protected ImageView imageRun;
    @FXML
    protected Label title;
    @FXML
    protected Label close;
    protected EventHandler<MouseEvent> eConfigClicked;
    protected EventHandler<DragEvent> eContextDragOver;
    protected EventHandler<DragEvent> eContextDragDropped;
    protected EventHandler<MouseEvent> eLinkHandleDetected;
    protected EventHandler<DragEvent> eLinkHandleDropped;
    protected EventHandler<DragEvent> eContextLinkDragOver;
    protected EventHandler<DragEvent> eContextLinkDragDropped;
    protected DragIconType dragIconType;
    protected Point2D dragOffset = new Point2D(0.0, 0.0);
    protected NodeLink dragLink = null;
    protected AnchorPane parent = null;

    protected int requiredInput;
    protected String[] inputLinkIds;
    protected int requiredOutput;
    protected String[] outputLinkIds;

    protected String[] inputNodeIds;
    protected String[] outputNodeIds;

    protected ConfigData[] inputData;

    protected boolean configured;
    protected boolean runSuccess;

    protected Tooltip information = new Tooltip();

    public DraggableNodeImpl() {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/fxml/Custom_DraggableNode.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        self = this;

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        //Provide UUID
        setId(UUID.randomUUID().toString());
    }

    public void invokeInputNodes() throws Exception {
        if (isSource()) return;
        List<Callable<Void>> callables = new ArrayList<>();
        //Find input node by UUID
        for (String inputNodeId : inputNodeIds) {
            for (Node node : parent.getChildren()) {
                if (node.getId() == null) continue;
                if (!(node instanceof DraggableNodeImpl)) continue;
                if (!node.getId().equals(inputNodeId)) continue;
                callables.add((DraggableNodeImpl) node);
            }
        }
        int poolSize = callables.size();
        if (poolSize == 0) return;
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        List<Future<Void>> futures = executor.invokeAll(callables);
        //Wait for completion
        for (Future<Void> future : futures) future.get();
        System.out.println("Complete invoke input nodes");
        //Stop executor
        executor.shutdown();
    }

    public boolean notConfigured() {
        return !this.configured;
    }

    public boolean runSuccess() {
        return this.runSuccess;
    }

    public boolean isSource() {
        return this instanceof DraggableNodeSource;
    }

    public int getOutDegree() {
        int degree = 0;
        for (int i = 0; i < requiredOutput; i++) {
            if (outputLinkIds[i] != null) degree++;
        }
        return degree;
    }

    public void setTitle(String title) {
        this.information.setText(title);
        this.information.setHideDelay(Duration.millis(500));
        this.information.setShowDelay(Duration.millis(500));
        this.title.setText(title);

        Tooltip.install(this, this.information);
    }

    public void relocateToPoint(Point2D p) {
        //relocates the object to a point that has been converted to
        //scene coordinates
        Point2D localCoords = getParent().sceneToLocal(p);

        relocate(
                (int) (localCoords.getX() - dragOffset.getX()),
                (int) (localCoords.getY() - dragOffset.getY())
        );
    }

    protected void setupNode() {
        setConfigured(false);
        setupConfigHandler();
        setupNodeDragDropHandlers();
        setupLinkDragDropHandlers();
        //
        handleConfig.setOnMouseClicked(eConfigClicked);
        dragLink = new NodeLink();
        dragLink.setVisible(false);
        setParent();
    }

    protected void setupNodeDragDropHandlers() {
        //Handle node dragging in parent view
        eContextDragOver = (event) -> {
            event.acceptTransferModes(TransferMode.ANY);
            relocateToPoint(new Point2dSerial(event.getSceneX(), event.getSceneY()));

            event.consume();
        };

        //For node dragging
        eContextDragDropped = (event) -> {
            getParent().setOnDragOver(null);
            getParent().setOnDragDropped(null);

            event.setDropCompleted(true);

            event.consume();
        };

        //Close button
        close.setOnMouseClicked((event) -> {
            AnchorPane parent = (AnchorPane) self.getParent();
            System.out.println("==== [START REMOVING NODE] ====");
            for (ListIterator<Node> iterNode = parent.getChildren().listIterator();
                 iterNode.hasNext(); ) {
                Node node = iterNode.next();
                if (node.getId() == null) continue;
                if (!(node instanceof NodeLink)) continue;
                if (
                        (inputLinkIds != null && Arrays.stream(inputLinkIds).anyMatch(e -> e != null && e.equals(node.getId())))
                                ||
                                (outputLinkIds != null && Arrays.stream(outputLinkIds).anyMatch(e -> e != null && e.equals(node.getId())))
                ) {
                    ((NodeLink) node).removeSelf();
                    //Remove link in user view
                    iterNode.remove();
                }
            }
            parent.getChildren().remove(self);
            System.out.printf("Node[id=%s, type=%s] removed\n", getId(), dragIconType.toString());
            System.out.println("==== [COMPLETE REMOVE NODE] ===");
        });

        //Drag detection for node dragging
        title.setOnDragDetected((event) -> {
            getParent().setOnDragOver(null);
            getParent().setOnDragDropped(null);

            getParent().setOnDragOver(eContextDragOver);
            getParent().setOnDragDropped(eContextDragDropped);

            dragOffset = new Point2D(event.getX(), event.getY());
            relocateToPoint(
                    new Point2D(event.getSceneX(), event.getSceneY())
            );
            ClipboardContent content = new ClipboardContent();
            DragContainer container = new DragContainer();

            container.addData("type", dragIconType.toString());

            content.put(DragContainer.AddNode, container);

            startDragAndDrop(TransferMode.ANY).setContent(content);

            event.consume();
        });
    }

    protected void setupLinkDragDropHandlers() {
        eLinkHandleDetected = event -> {

            getParent().setOnDragOver(null);
            getParent().setOnDragDropped(null);

            getParent().setOnDragOver(eContextLinkDragOver);
            getParent().setOnDragDropped(eContextLinkDragDropped);

            //Set up user-draggable link
            parent.getChildren().add(0, dragLink);

            dragLink.setVisible(false);

            Point2D p = new Point2D(
                    getLayoutX() + (getWidth() / 2.0),
                    getLayoutY() + (getHeight() / 2.0)
            );

            dragLink.setStart(p);

            //Drag content code
            ClipboardContent content = new ClipboardContent();
            DragContainer container = new DragContainer();

            //pass the UUID of the source node for later lookup
            container.addData("source", getId());

            content.put(DragContainer.AddLink, container);

            startDragAndDrop(TransferMode.ANY).setContent(content);

            event.consume();
        };

        eLinkHandleDropped = event -> {

            getParent().setOnDragOver(null);
            getParent().setOnDragDropped(null);

            //get the drag data.  If it's null, abort.
            //This isn't the drag event we're looking for.
            DragContainer container =
                    (DragContainer) event.getDragboard().getContent(DragContainer.AddLink);

            if (container == null)
                return;

            //hide the draggable NodeLink and remove it from the right-hand AnchorPane's children
            dragLink.setVisible(false);
            parent.getChildren().remove(0);

            ClipboardContent content = new ClipboardContent();

            //pass the UUID of the target node for later lookup
            container.addData("target", getId());

            content.put(DragContainer.AddLink, container);

            event.getDragboard().setContent(content);
            event.setDropCompleted(true);
            event.consume();
        };

        eContextLinkDragOver = event -> {
            event.acceptTransferModes(TransferMode.ANY);

            //Relocate end of user-draggable link
            if (!dragLink.isVisible())
                dragLink.setVisible(true);

            dragLink.setEnd(new Point2D(event.getX(), event.getY()));

            event.consume();
        };
        //drop event for link creation
        eContextLinkDragDropped = event -> {
            getParent().setOnDragOver(null);
            getParent().setOnDragDropped(null);

            //hide the draggable NodeLink and remove it from the right-hand AnchorPane's children
            dragLink.setVisible(false);
            parent.getChildren().remove(0);

            event.setDropCompleted(true);
            event.consume();
        };
    }

    protected void setParent() {
        parentProperty().addListener((obs, oldVal, newVal) -> parent = (AnchorPane) getParent());
    }

    protected void setConfigured(boolean configured) {
        this.configured = configured;
        if (configured) {
            imageConfigured.setImage(new Image("/images/correct.png", 30, 30, false, true));
        } else {
            imageConfigured.setImage(new Image("/images/incorrect.png", 30, 30, false, true));
        }
    }

    protected void setRun(boolean runSuccess) {
        this.runSuccess = runSuccess;
        if (runSuccess) {
            imageRun.setImage(new Image("/images/correct.png", 30, 30, false, true));
        } else {
            imageRun.setImage(new Image("/images/incorrect.png", 30, 30, false, true));
        }
    }

    protected void removePrevRun() {
        //TODO: Remove data if necessary
        this.runSuccess = false;
        imageRun.setImage(null);
    }

    public boolean canPlugInput() {
        return Arrays.stream(inputLinkIds).anyMatch(Objects::isNull);
    }

    public boolean canPlugOutput() {
        return Arrays.stream(outputLinkIds).anyMatch(Objects::isNull);
    }

    public void plugInput(String nodeUUID, String linkUUID) {
        int idx = plugLink(nodeUUID, linkUUID, requiredInput, inputNodeIds, inputLinkIds, NodeLinkType.INPUT);
        for (Node node : parent.getChildren()) {
            if (node.getId() == null) continue;
            if (!(node instanceof DraggableNodeImpl)) continue;
            if (!node.getId().equals(nodeUUID)) continue;
            inputData[idx] = ((DraggableNodeImpl) node).getConfigData();
        }
    }

    public void plugOutput(String nodeUUID, String linkUUID) {
        plugLink(nodeUUID, linkUUID, requiredOutput, outputNodeIds, outputLinkIds, NodeLinkType.OUTPUT);
    }

    public void unplugInput(String linkUUID) {
        int idx = unplugLink(linkUUID, requiredInput, inputNodeIds, inputLinkIds, NodeLinkType.INPUT);
        inputData[idx] = null;
        setConfigured(false);
    }

    public void unplugOutput(String linkUUID) {
        unplugLink(linkUUID, requiredOutput, outputNodeIds, outputLinkIds, NodeLinkType.OUTPUT);
    }

    protected int plugLink(String nodeId, String linkId, int required, String[] nodeIds, String[] linkIds, NodeLinkType type) {
        System.out.printf("Node[id=%s, type=%s] plug %s Link[id=%s,", getId(), dragIconType.toString(), type.toString().toLowerCase(), linkId);
        int rack = 0;
        for (int i = 0; i < required; i++) {
            if (linkIds[i] == null) {
                linkIds[i] = linkId;
                nodeIds[i] = nodeId;
                break;
            }
            rack++;
        }
        System.out.printf(" rack=%d/%d]\n", rack + 1, required);
        return rack;
    }

    protected int unplugLink(String linkId, int required, String[] nodeIds, String[] linkIds, NodeLinkType type) {
        System.out.printf("Node[id=%s, type=%s] unplug %s Link[id=%s, rack=", getId(), dragIconType.toString(), type.toString().toLowerCase(), linkId);
        int rack = 0;
        for (int i = 0; i < required; i++) {
            if (linkIds[i] != null && linkIds[i].equals(linkId)) {
                linkIds[i] = null;
                nodeIds[i] = null;
                break;
            }
            rack++;
        }
        System.out.printf("%d/%d]\n", rack + 1, required);
        return rack;
    }

    protected void printInfo() {
        System.out.printf("Node[id=%s, type=%s] added\n", getId(), dragIconType.toString());
    }

    protected void sendConfigDataToOutputs() {
        for (String nodeId : outputNodeIds) {
            for (Node node : parent.getChildren()) {
                if (node.getId() == null) continue;
                if (!(node instanceof DraggableNodeImpl)) continue;
                if (!node.getId().equals(nodeId)) continue;
                ((DraggableNodeImpl) node).setConfigured(false);
                ((DraggableNodeImpl) node).receiveConfigDataFromInput(getId(), getConfigData());
            }
        }
    }

    protected void receiveConfigDataFromInput(String inputId, ConfigData data) {
        for (int i = 0; i < requiredInput; i++) {
            if (inputNodeIds[i] != null && inputNodeIds[i].equals(inputId)) {
                inputData[i] = data;
                sendConfigDataToOutputs();
                break;
            }
        }
    }

    @Override
    public Void call() {
        if (runSuccess()) return null;
        try {
            invokeInputNodes();
        } catch (Exception exception) {
            Workspace.appendLn(exception.getMessage());
            setRun(false);
            return null;
        }
        try {
            setRun(execute());
        } catch (Exception exception) {
            Workspace.appendLn(exception.getMessage());
            setRun(false);
            return null;
        }
        return null;
    }

    protected abstract boolean execute();
}
