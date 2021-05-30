package app.custom;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.When;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.CubicCurve;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

public class NodeLink extends AnchorPane implements Initializable {
    private final DoubleProperty mControlOffsetX = new SimpleDoubleProperty();
    private final DoubleProperty mControlOffsetY = new SimpleDoubleProperty();
    private final DoubleProperty mControlDirectionX1 = new SimpleDoubleProperty();
    private final DoubleProperty mControlDirectionY1 = new SimpleDoubleProperty();
    private final DoubleProperty mControlDirectionX2 = new SimpleDoubleProperty();
    private final DoubleProperty mControlDirectionY2 = new SimpleDoubleProperty();
    @FXML
    CubicCurve node_link;
    private String sourceId;
    private String targetId;

    public NodeLink() {

        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/fxml/Custom_NodeLink.fxml")
        );

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();

        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        //Provide UUID
        setId(UUID.randomUUID().toString());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        mControlOffsetX.set(100.0);
        mControlOffsetY.set(50.0);


        mControlDirectionX1.bind(new When(
                node_link.startXProperty().greaterThan(node_link.endXProperty()))
                .then(-1.0).otherwise(1.0));

        mControlDirectionX2.bind(new When(
                node_link.startXProperty().greaterThan(node_link.endXProperty()))
                .then(1.0).otherwise(-1.0));


        node_link.controlX1Property().bind(
                Bindings.add(
                        node_link.startXProperty(), mControlOffsetX.multiply(mControlDirectionX1)
                )
        );

        node_link.controlX2Property().bind(
                Bindings.add(
                        node_link.endXProperty(), mControlOffsetX.multiply(mControlDirectionX2)
                )
        );

        node_link.controlY1Property().bind(
                Bindings.add(
                        node_link.startYProperty(), mControlOffsetY.multiply(mControlDirectionY1)
                )
        );

        node_link.controlY2Property().bind(
                Bindings.add(
                        node_link.endYProperty(), mControlOffsetY.multiply(mControlDirectionY2)
                )
        );
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setStart(Point2D startPoint) {

        node_link.setStartX(startPoint.getX());
        node_link.setStartY(startPoint.getY());
    }

    public void setEnd(Point2D endPoint) {

        node_link.setEndX(endPoint.getX());
        node_link.setEndY(endPoint.getY());
    }


    public void bindEnds(DraggableNodeImpl source, DraggableNodeImpl target) {
        node_link.startXProperty().bind(
                Bindings.add(source.layoutXProperty(), (source.getWidth() / 2.0)));

        node_link.startYProperty().bind(
                Bindings.add(source.layoutYProperty(), (source.getWidth() / 2.0)));

        node_link.endXProperty().bind(
                Bindings.add(target.layoutXProperty(), (target.getWidth() / 2.0)));

        node_link.endYProperty().bind(
                Bindings.add(target.layoutYProperty(), (target.getWidth() / 2.0)));

        sourceId = source.getId();
        targetId = target.getId();

        source.plugOutput(targetId, getId());
        target.plugInput(sourceId, getId());
    }

    public void removeSelf() {
        AnchorPane view = (AnchorPane) getParent();

        for (Node node : view.getChildren()) {
            if (node.getId() == null) continue;
            if (!(node instanceof DraggableNodeImpl)) continue;
            if (node.getId().equals(sourceId)) ((DraggableNodeImpl) node).unplugOutput(getId());
            if (node.getId().equals(targetId)) ((DraggableNodeImpl) node).unplugInput(getId());
        }
    }
}
