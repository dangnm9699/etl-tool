package app.model;

import javafx.scene.control.TreeItem;

public class TreeToolbox {
    private static TreeItem<String> RootItem = null;

    private TreeToolbox() {
    }

    public static TreeItem<String> getInstance() {
        if (RootItem != null) return RootItem;
        //Init root
        RootItem = new TreeItem<>("Toolbox");
        RootItem.setExpanded(true);
        //Init inter
        TreeItem<String> source = new TreeItem<>("Source");
        source.setExpanded(true);
        TreeItem<String> destination = new TreeItem<>("Destination");
        destination.setExpanded(true);
        TreeItem<String> transformation = new TreeItem<>("Transformation");
        transformation.setExpanded(true);
        //Init children
        for (DragIconType type : DragIconType.values()) {
            String type2String = type.toString();
            TreeItem<String> item = new TreeItem<>();
            if (type2String.startsWith("source_")) {
                item.setValue(type2String.replace("source_", ""));
                source.getChildren().add(item);
            }
            if (type2String.startsWith("destination_")) {
                item.setValue(type2String.replace("destination_", ""));
                destination.getChildren().add(item);
            }
            if (type2String.startsWith("transformation_")) {
                item.setValue(type2String.replace("transformation_", ""));
                transformation.getChildren().add(item);
            }
        }
        RootItem.getChildren().add(source);
        RootItem.getChildren().add(destination);
        RootItem.getChildren().add(transformation);
        return RootItem;
    }
}
