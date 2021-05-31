package app.custom;

import app.model.DragContainer;
import app.model.DragIconType;
import app.model.TreeToolbox;
import app.model.Utils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import tech.tablesaw.api.Table;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Workspace extends BorderPane implements Initializable {
    private static HashMap<String, Table> datasetHashMap = null;
    private static StringBuilder OUTPUT = new StringBuilder();

    @FXML
    private BorderPane root;
    @FXML
    private AnchorPane anchorPaneRoot;
    @FXML
    private SplitPane splitPane;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TreeView<String> treeView;
    @FXML
    private Button buttonRun;
    @FXML
    private Button buttonBack;
    @FXML
    private Button buttonStop;
    @FXML
    private TextArea output;

    private DragIcon dragOverIcon = null;
    private EventHandler<DragEvent> eIconDragOverRoot = null;
    private EventHandler<DragEvent> eIconDragDropped = null;
    private EventHandler<DragEvent> eIconDragOverRightPane = null;
    private EventHandler<ActionEvent> eClickButtonRun = null;
    private EventHandler<ActionEvent> eClickButtonBack = null;
    private EventHandler<ActionEvent> eClickButtonStop = null;

    public static void appendLn(String content) {
        OUTPUT.append(content).append("\n");
    }

    public static void append(String content) {
        OUTPUT.append(content);
    }

    public Workspace() {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/fxml/Workspace.fxml")
        );

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();

        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static void createDatasetHashMap() {
        if (datasetHashMap == null) datasetHashMap = new HashMap<>();
    }

    public static void addDataset(String key, Table val) {
        datasetHashMap.put(key, val);
    }

    public static Table getDataset(String key) {
        return datasetHashMap.get(key).copy();
    }

    public static void clearDataset() {
        datasetHashMap.clear();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTreeView();

        buttonRun.setDisable(false);
        buttonStop.setDisable(true);

        dragOverIcon = new DragIcon();

        dragOverIcon.setVisible(false);
        dragOverIcon.setOpacity(0.65);
        anchorPaneRoot.getChildren().add(dragOverIcon);

        setupClickHandlers();
        setupDragHandlers();

        buttonRun.setOnAction(eClickButtonRun);
        buttonBack.setOnAction(eClickButtonBack);
        buttonStop.setOnAction(eClickButtonStop);
    }

    private void setupTreeView() {
        treeView.setRoot(TreeToolbox.getInstance());
        treeView.setShowRoot(false);
        treeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        treeView.setCellFactory(new Callback<>() {
            @Override
            public TreeCell<String> call(TreeView<String> stringTreeView) {
                TreeCell<String> treeCell = new TreeCell<>() {
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(item);
                        }
                        setGraphic(null);
                    }
                };
                //Get dragIconType based on tree item
                treeCell.setOnDragDetected(event -> {
                    TreeCell<String> cell = (TreeCell<String>) event.getSource();

                    DragIconType dragIconType;
                    try {
                        String nodeType = cell.getTreeItem().getParent().getValue();
                        String nodeSpec = cell.getTreeItem().getValue();
                        String typeString = (nodeType + "_" + nodeSpec).toLowerCase();
                        dragIconType = DragIconType.valueOf(typeString);
                    } catch (IllegalArgumentException exception) {
                        System.out.println("Wrong drag icon type");
                        return;
                    } catch (NullPointerException exception) {
                        System.out.println("Null tree item");
                        return;
                    }

                    splitPane.setOnDragOver(eIconDragOverRoot);
                    anchorPane.setOnDragOver(eIconDragOverRightPane);
                    anchorPane.setOnDragDropped(eIconDragDropped);

                    dragOverIcon.setType(dragIconType);
                    dragOverIcon.relocateToPoint(
                            new Point2D(event.getSceneX(), event.getSceneY())
                    );

                    ClipboardContent content = new ClipboardContent();
                    DragContainer container = new DragContainer();

                    container.addData("type", dragOverIcon.getType().toString());

                    content.put(DragContainer.AddNode, container);

                    dragOverIcon.startDragAndDrop(TransferMode.ANY).setContent(content);
                    dragOverIcon.setVisible(true);
                    dragOverIcon.setMouseTransparent(true);
                    event.consume();
                });

                return treeCell;
            }
        });
    }

    private void setupDragHandlers() {
        eIconDragOverRoot = event -> {
            Point2D p = anchorPane.sceneToLocal(event.getSceneX(), event.getSceneY());
            if (!anchorPane.boundsInLocalProperty().get().contains(p)) {
                event.acceptTransferModes(TransferMode.ANY);
                dragOverIcon.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
                return;
            }
            event.consume();
        };

        eIconDragOverRightPane = event -> {
            event.acceptTransferModes(TransferMode.ANY);
            dragOverIcon.relocateToPoint(
                    new Point2D(event.getSceneX(), event.getSceneY())
            );
            event.consume();
        };

        eIconDragDropped = event -> {
            DragContainer container =
                    (DragContainer) event.getDragboard().getContent(DragContainer.AddNode);
            container.addData("scene_coords",
                    new Point2D(event.getSceneX(), event.getSceneY()));

            ClipboardContent content = new ClipboardContent();
            content.put(DragContainer.AddNode, container);

            event.getDragboard().setContent(content);
            event.setDropCompleted(true);
        };

        root.setOnDragDone(event -> {
            anchorPane.removeEventHandler(DragEvent.DRAG_OVER, eIconDragOverRightPane);
            anchorPane.removeEventHandler(DragEvent.DRAG_DROPPED, eIconDragDropped);
            splitPane.removeEventHandler(DragEvent.DRAG_OVER, eIconDragOverRoot);

            dragOverIcon.setVisible(false);

            DragContainer container = (DragContainer) event.getDragboard().getContent(DragContainer.AddNode);

            if (container != null) {
                if (container.getValue("scene_coords") != null) {
                    DragIconType dragIconType = DragIconType.valueOf(container.getValue("type"));

                    DraggableNodeImpl node = newNode(dragIconType);

                    anchorPane.getChildren().add(node);

                    Point2D cursorPoint = container.getValue("scene_coords");
                    node.setTitle(dragIconType.toString());
                    node.relocateToPoint(
                            new Point2D(cursorPoint.getX() - 32, cursorPoint.getY() - 32)
                    );
                }
            }
            container = (DragContainer) event.getDragboard().getContent(DragContainer.AddLink);
            if (container != null) {
                String sourceId = container.getValue("source");
                String targetId = container.getValue("target");
                if (sourceId != null && targetId != null && !sourceId.equals(targetId)) {
                    DraggableNodeImpl source = null;
                    DraggableNodeImpl target = null;
                    for (Node n : anchorPane.getChildren()) {
                        if (n.getId() == null) {
                            continue;
                        }
                        if (n.getId().equals(sourceId)) {
                            source = (DraggableNodeImpl) n;
                        }
                        if (n.getId().equals(targetId)) {
                            target = (DraggableNodeImpl) n;
                        }
                    }
                    if (source != null && target != null && source.canPlugOutput() && target.canPlugInput()) {
                        if (source.notConfigured()) {
                            Utils.alertError("Input node is not configured");
                            return;
                        }
                        System.out.println("===== [START ADDING LINK] =====");
                        NodeLink link = new NodeLink();
                        System.out.printf("Link[id=%s] added\n", link.getId());
                        anchorPane.getChildren().add(0, link);
                        link.bindEnds(source, target);
                        System.out.println("===== [COMPLETE ADD LINK] =====");
                    }
                }

            }
            event.consume();
        });
    }

    private void setupClickHandlers() {
        eClickButtonBack = event -> {
            ((Stage) ((Node) (event.getSource())).getScene().getWindow()).close();
        };

        eClickButtonStop = event -> {
            for (Node node : anchorPane.getChildren()) {
                if (node.getId() == null) continue;
                if (!(node instanceof DraggableNodeImpl)) continue;
                ((DraggableNodeImpl) node).removePrevRun();
            }
            OUTPUT = new StringBuilder();
            output.setText("");
            buttonStop.setDisable(true);
            buttonRun.setDisable(false);
            buttonBack.setDisable(false);
            Workspace.clearDataset();
        };

        eClickButtonRun = event -> {
            //Create list of DraggableNodeImpl (Callable<Void> implemented)
            List<Callable<Void>> callables = new ArrayList<>();
            //Search for nodes that has out degree equal to 0
            for (Node node : anchorPane.getChildren()) {
                if (node.getId() == null) continue;
                if (!(node instanceof DraggableNodeImpl)) continue;
                DraggableNodeImpl draggableNode = (DraggableNodeImpl) node;
                if (((DraggableNodeImpl) node).notConfigured()) {
                    //Must be configured
                    System.out.println("There is at least a node that is not configured");
                    return;
                }
                if (draggableNode.getOutDegree() == 0) callables.add(draggableNode);
            }
            //Have any nodes?
            int poolSize = callables.size();
            if (poolSize == 0) {
                System.out.println("There is no node");
                return;
            }
            //Ready to run
            //Create dataset hashmap to store data nodes
            Workspace.createDatasetHashMap();
            //Disable button
            buttonRun.setDisable(true);
            buttonBack.setDisable(true);
            //Create executorService to run concurrently
            ExecutorService executor = Executors.newFixedThreadPool(poolSize);
            try {
                List<Future<Void>> futures = executor.invokeAll(callables);
                //
                for (Future<Void> future : futures) future.get();
            } catch (Exception exception) {
                exception.printStackTrace();
            } finally {
                //Shutdown executor
                executor.shutdown();
            }
            output.setText("Successfully");
            output.setStyle("-fx-text-fill: seagreen");
            if (!OUTPUT.toString().equals("")) {
                append("Failed");
                output.setText(OUTPUT.toString());
                output.setStyle("-fx-text-fill: tomato");
            }
            //Enable stop button
            buttonStop.setDisable(false);

            datasetHashMap.forEach((s, table) -> {
                System.out.printf("\nNode[id=%s]\n", s);
                System.out.println(table.first(10));
            });
        };
    }

    private DraggableNodeImpl newNode(DragIconType type) {
        DraggableNodeImpl node = null;
        switch (type) {
            case source_db:
                node = new DraggableNodeSourceDatabase();
                break;
            case source_csv:
                node = new DraggableNodeSourceCSV();
                break;
            case source_excel:
                node = new DraggableNodeSourceExcel();
                break;
            case destination_db:
                node = new DraggableNodeDestinationDatabase();
                break;
            case destination_csv:
                node = new DraggableNodeDestinationCSV();
                break;
            case destination_excel:
                node = new DraggableNodeDestinationExcel();
                break;
            case transformation_numeric_derived_columns:
                node = new DraggableNodeTransformationDerivedColumnsNumeric();
                break;
            case transformation_remove_null:
                node = new DraggableNodeTransformationRemoveNull();
                break;
            case transformation_string_convert:
                node = new DraggableNodeTransformationStringConvert();
                break;
            case transformation_string_merge:
                node = new DraggableNodeTransformationStringMerge();
                break;
            case transformation_sort:
                node = new DraggableNodeTransformationSort();
                break;
            case transformation_numeric_ratio:
                node = new DraggableNodeTransformationNumericRatio();
                break;
            case transformation_numeric_abs:
                node = new DraggableNodeTransformationNumericAbs();
                break;
            case transformation_numeric_scale:
                node = new DraggableNodeTransformationNumericScale();
                break;
            case transformation_numeric_min_max:
                node = new DraggableNodeTransformationNumericMinMax();
                break;
            case transformation_date_plus_minus:
                node = new DraggableNodeTransformationDatePlusMinus();
                break;
            case transformation_date_split:
                node = new DraggableNodeTransformationDateSplit();
                break;
        }
        return node;
    }
}
