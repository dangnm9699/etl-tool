package app.model;

import javafx.scene.control.TreeItem;

import java.util.Arrays;

public class LazyToolboxMenuInitialization extends TreeItem<String> {
    private static LazyToolboxMenuInitialization INSTANCE;

    private LazyToolboxMenuInitialization() {
    }

    public static LazyToolboxMenuInitialization getInstance() {
        if (INSTANCE == null) {
            //Init root
            INSTANCE = new LazyToolboxMenuInitialization();
            INSTANCE.setValue("Toolbox");
            //Init sub
            TreeItem<String> source = new TreeItem<>("Source");
            TreeItem<String> destination = new TreeItem<>("Destination");
            TreeItem<String> transformation = new TreeItem<>("Transformation");
            //Init source children
            TreeItem<String> source_db = new TreeItem<>("DB");
            TreeItem<String> source_csv = new TreeItem<>("CSV");
            TreeItem<String> source_excel = new TreeItem<>("Excel");
            //Init destination children
            TreeItem<String> destination_db = new TreeItem<>("DB");
            TreeItem<String> destination_csv = new TreeItem<>("CSV");
            TreeItem<String> destination_excel = new TreeItem<>("Excel");
            //Init transformation children
            TreeItem<String> transformation_test = new TreeItem<>("Test");
            TreeItem<String> transformation_check = new TreeItem<>("Check");
            //Add sub
            INSTANCE.getChildren().addAll(Arrays.asList(source, destination, transformation));
            //Add children
            source.getChildren().addAll(Arrays.asList(source_db, source_csv, source_excel));
            destination.getChildren().addAll(Arrays.asList(destination_db, destination_csv, destination_excel));
            transformation.getChildren().addAll(Arrays.asList(transformation_test, transformation_check));
            //Config root
            INSTANCE.setExpanded(true);
            //Config sub
            source.setExpanded(true);
            destination.setExpanded(true);
            transformation.setExpanded(true);
        }
        return INSTANCE;
    }
}
