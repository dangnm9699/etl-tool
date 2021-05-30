package app.model;

import javafx.scene.control.TreeItem;

import java.util.Arrays;

public class TreeFunction {
    private static TreeItem<String> NumericRoot = null;
    private static TreeItem<String> StringRoot = null;

    private TreeFunction() {
    }

    public static TreeItem<String> getNumericInstance() {
        if (NumericRoot != null) return NumericRoot;
        //Init root
        NumericRoot = new TreeItem<>("Function");
        NumericRoot.setExpanded(true);
        //Init children
        NumericRoot.getChildren().addAll(
                Arrays.asList(
                        new TreeItem<>("Add"),
                        new TreeItem<>("Subtract"),
                        new TreeItem<>("Multiply"),
                        new TreeItem<>("Divide")
                ));
        return NumericRoot;
    }

    public static TreeItem<String> getStringConvertInstance() {
        if (StringRoot != null) return StringRoot;
        //Init root
        StringRoot = new TreeItem<>("Function");
        StringRoot.setExpanded(true);
        //Init children
        StringRoot.getChildren().addAll(
                Arrays.asList(
                        new TreeItem<>("Capitalize"),
                        new TreeItem<>("Uppercase"),
                        new TreeItem<>("Lowercase")
                ));
        return StringRoot;
    }
}
