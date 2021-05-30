package app.controller;

import app.model.CloseType;
import app.model.ConfigData;
import app.model.Utils;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Stack;

public class CalculatorController implements Initializable {
    private final Stack<String> tempExpression = new Stack<>();
    @FXML
    private AnchorPane calculator;
    @FXML
    private TextField screen;
    @FXML
    private Button ok;
    @FXML
    private Button cancel;
    @FXML
    private Button add;
    @FXML
    private Button subtract;
    @FXML
    private Button multiply;
    @FXML
    private Button divide;
    @FXML
    private Button dot;
    @FXML
    private Button delete;
    @FXML
    private Button numpad0;
    @FXML
    private Button numpad1;
    @FXML
    private Button numpad2;
    @FXML
    private Button numpad3;
    @FXML
    private Button numpad4;
    @FXML
    private Button numpad5;
    @FXML
    private Button numpad6;
    @FXML
    private Button numpad7;
    @FXML
    private Button numpad8;
    @FXML
    private Button numpad9;
    @FXML
    private Button open;
    @FXML
    private Button close;
    @FXML
    private TableView<String> columnNames;
    @FXML
    private TableColumn<String, String> column;
    private EventHandler<ActionEvent> eClickNumpad = null;
    private EventHandler<ActionEvent> eClickOperator = null;
    private EventHandler<ActionEvent> eClickDelete = null;
    private EventHandler<ActionEvent> eCLickOk = null;
    private EventHandler<ActionEvent> eClickCancel = null;
    private String[] expression = null;
    private CloseType closeType;

    private StringBuilder number = new StringBuilder();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        column.setSortable(false);
        column.setEditable(false);
        column.prefWidthProperty().bind(columnNames.widthProperty().divide(1));
        column.setCellValueFactory(celLValue -> new SimpleStringProperty(celLValue.getValue()));
        column.setCellFactory(col -> {
            TableCell<String, String> cell = (TableCell<String, String>) TableColumn.DEFAULT_CELL_FACTORY.call(col);
            cell.setOnMouseClicked(event -> {
                String name = cell.getItem();
                if (name == null) return;
                tempExpression.add("column_" + name);
                screen.setText(stack2String());
            });
            return cell;
        });
        buildEventHandlers();
        assignEventHandlers();
    }

    public CloseType getCloseType() {
        return closeType;
    }

    public String[] getExpression() {
        return expression;
    }

    public void getString(KeyEvent keyEvent) {
        System.out.println(keyEvent.getCode());
    }

    public void presetExpressions(ConfigData configData, String[] expression) {
        //Build column table
        columnNames.getItems().addAll(configData.getNumericColumnNames());
        //Expr to screen and expressions stack
        if (expression != null) {
            this.tempExpression.addAll(Arrays.asList(expression));
            String lastElm = this.tempExpression.pop();
            if (!lastElm.startsWith("column_")
                    && (lastElm.contains("0") || lastElm.contains("1") || lastElm.contains("2") || lastElm.contains("3") || lastElm.contains("4")
                    || lastElm.contains("5") || lastElm.contains("6") || lastElm.contains("7") || lastElm.contains("8") || lastElm.contains("9"))
            ) {
                this.number.append(lastElm);
            } else {
                this.tempExpression.push(lastElm);
            }
        }
        this.expression = stack2StringArray();
        this.screen.setText(stack2String() + number.toString());
    }

    private void buildEventHandlers() {
        eCLickOk = event -> {
            closeType = CloseType.OK;
            if (!number.toString().equals("")) {
                tempExpression.add(number.toString());
                number = new StringBuilder();
            }
            String[] expression = stack2StringArray();
            if (expression.length == 0 || !Utils.validateExpression(expression)) {
                Utils.alertError("Invalid expression! Please try again!");
                return;
            }
            this.expression = expression;
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        };
        eClickCancel = event -> {
            closeType = CloseType.CANCEL;
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        };
        eClickNumpad = event -> {
            String ops = ((Button) event.getSource()).getText();
            String curNumber = number.toString();
            if (ops.equals(".") && curNumber.contains(".")) {
                Utils.alertError("Number contains only 1 dot");
                return;
            }
            if (!curNumber.contains(".")
                    && (!ops.equals("."))
                    && (((curNumber.length() >= 11) && (curNumber.charAt(0) == '0')) || ((curNumber.length() >= 10) && (curNumber.charAt(0) != '0')))) {
                Utils.alertError("Integer only contain 10 meaning digits");
                return;
            }
            number.append(ops);
            screen.setText(stack2String() + number.toString());
        };
        eClickOperator = event -> {
            String ops = ((Button) event.getSource()).getText();
            String curNumber = number.toString();
            if (!curNumber.equals("")) {
                tempExpression.add(number.toString());
                number = new StringBuilder();
            }
            tempExpression.add(ops);
            screen.setText(stack2String());
        };
        eClickDelete = event -> {
            if (number.length() == 0) {
                if (tempExpression.size() == 0) return;
                String lastElm = tempExpression.pop();
                if (!lastElm.startsWith("column_")
                        && (lastElm.contains("0") || lastElm.contains("1") || lastElm.contains("2") || lastElm.contains("3") || lastElm.contains("4")
                        || lastElm.contains("5") || lastElm.contains("6") || lastElm.contains("7") || lastElm.contains("8") || lastElm.contains("9"))
                ) {
                    this.number.append(lastElm);
                    number.deleteCharAt(number.length() - 1);
                }
            } else {
                number.deleteCharAt(number.length() - 1);
            }
            screen.setText(stack2String() + number.toString());
        };
    }

    private void assignEventHandlers() {
        //
        ok.setOnAction(eCLickOk);
        cancel.setOnAction(eClickCancel);
        //
        dot.setOnAction(eClickNumpad);
        numpad0.setOnAction(eClickNumpad);
        numpad1.setOnAction(eClickNumpad);
        numpad2.setOnAction(eClickNumpad);
        numpad3.setOnAction(eClickNumpad);
        numpad4.setOnAction(eClickNumpad);
        numpad5.setOnAction(eClickNumpad);
        numpad6.setOnAction(eClickNumpad);
        numpad7.setOnAction(eClickNumpad);
        numpad8.setOnAction(eClickNumpad);
        numpad9.setOnAction(eClickNumpad);
        //
        add.setOnAction(eClickOperator);
        subtract.setOnAction(eClickOperator);
        multiply.setOnAction(eClickOperator);
        divide.setOnAction(eClickOperator);
        open.setOnAction(eClickOperator);
        close.setOnAction(eClickOperator);
        delete.setOnAction(eClickDelete);
    }

    private String stack2String() {
        StringBuilder stringBuilder = new StringBuilder();
        tempExpression.forEach(stringBuilder::append);
        return stringBuilder.toString();
    }

    private String[] stack2StringArray() {
        return tempExpression.toArray(new String[0]);
    }
}
