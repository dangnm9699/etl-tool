package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @author dangnm9699
 */
public class App extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/Index.fxml"));

            primaryStage.setTitle("Index");
            primaryStage.setScene(new Scene(loader.load()));
            primaryStage.show();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
