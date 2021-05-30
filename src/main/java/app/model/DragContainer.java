package app.model;

import javafx.scene.input.DataFormat;
import javafx.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DragContainer implements Serializable {
    public static final DataFormat AddNode = new DataFormat("app.custom.DragIcon.add");
    public static final DataFormat AddLink = new DataFormat("app.custom.NodeLink.add");
    private final List<Pair<String, Object>> pairs = new ArrayList<>();

    public void addData(String key, Object value) {
        pairs.add(new Pair<>(key, value));
    }

    public <T> T getValue(String key) {
        for (Pair<String, Object> data : pairs) {
            if (data.getKey().equals(key))
                return (T) data.getValue();
        }
        return null;
    }
}
