package app.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AboutController implements Initializable {
    @FXML
    private Hyperlink dangnm;
    @FXML
    private Hyperlink benbp;
    @FXML
    private Hyperlink chungpv;
    @FXML
    private Hyperlink chinhvk;
    @FXML
    private Button back;

    private EventHandler<ActionEvent> eClickBack;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        buildEventHandlers();

        back.setOnAction(eClickBack);
    }

    private void buildEventHandlers() {
        eClickBack = event -> {
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        };
    }
}
