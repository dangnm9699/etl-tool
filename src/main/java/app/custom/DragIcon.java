package app.custom;

import app.model.DragIconType;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class DragIcon extends AnchorPane {
    private DragIconType type;

    public DragIcon() {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/fxml/Custom_DragIcon.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void relocateToPoint(Point2D p) {
        Point2D localCoords = getParent().sceneToLocal(p);
        relocate(
                (int) (localCoords.getX() - (getBoundsInLocal().getWidth() / 2)),
                (int) (localCoords.getY() - (getBoundsInLocal().getHeight() / 2))
        );
    }

    public DragIconType getType() {
        return type;
    }

    public void setType(DragIconType type) {
        this.type = type;

        getStyleClass().clear();
        getStyleClass().add("drag-icon");

        if (getChildren().size() > 0) {
            getChildren().clear();
        }

        switch (this.type) {
            case source_db:
            case destination_db:
                getStyleClass().add("icon-red");
                break;
            case source_csv:
            case destination_csv:
                getStyleClass().add("icon-blue");
                break;
            case source_excel:
            case destination_excel:
                getStyleClass().add("icon-green");
                break;
            default:
                getStyleClass().add("icon-purple");
                break;
        }
    }
}
