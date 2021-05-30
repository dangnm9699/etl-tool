package app.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class IndexController implements Initializable {
    @FXML
    private Button workspace;
    @FXML
    private Button about;
    @FXML
    private Button exit;

    private EventHandler<ActionEvent> eClickWorkspace;
    private EventHandler<ActionEvent> eClickAbout;
    private EventHandler<ActionEvent> eClickExit;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        buildEventHandlers();

        workspace.setOnAction(eClickWorkspace);
        about.setOnAction(eClickAbout);
        exit.setOnAction(eClickExit);
    }

    private void buildEventHandlers() {
        eClickWorkspace = event -> {
            Workspace ws = new Workspace();

            Stage stage = new Stage();
            stage.setTitle("Workspace");
            stage.setScene(new Scene(ws));

            ((Node) event.getSource()).getScene().getWindow().hide();
            stage.showAndWait();

            ((Stage) ((Node) event.getSource()).getScene().getWindow()).show();
        };

        eClickAbout = event -> {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/fxml/About.fxml"));

                Stage stage = new Stage();
                stage.setTitle("About us");
                stage.setScene(new Scene(loader.load()));
                ((Node) event.getSource()).getScene().getWindow().hide();

                stage.showAndWait();
                ((Stage) ((Node) event.getSource()).getScene().getWindow()).show();

            } catch (IOException exception) {
                exception.printStackTrace();
            }
        };

        eClickExit = event -> ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }
}