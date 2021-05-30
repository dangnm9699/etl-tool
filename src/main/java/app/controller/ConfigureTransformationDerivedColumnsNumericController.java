package app.controller;

import app.model.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class ConfigureTransformationDerivedColumnsNumericController implements Initializable {
    @FXML
    private TableView<Column> inputs;
    @FXML
    private TreeView<String> treeView;
    @FXML
    private TableView<NumericDerivedColumn> numericDerivedColumns;
    @FXML
    private Button cancel;
    @FXML
    private Button ok;
    @FXML
    private TextField description;
    @FXML
    private Button addRow;
    @FXML
    private Button deleteRow;

    private EventHandler<ActionEvent> eClickOk = null;
    private EventHandler<ActionEvent> eClickCancel = null;
    private EventHandler<ActionEvent> eClickAddRow = null;
    private EventHandler<ActionEvent> eClickDeleteRow = null;

    private ConfigureTransformationDerivedColumnsNumeric config;
    private ConfigData configData;
    private CloseType closeType;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        buildFunctionsTreeView();
        buildEventHandlers();
        assignEventHandlers();
    }

    public void presetConfig(ConfigureTransformationDerivedColumnsNumeric config, ConfigData configData, boolean configuredBefore) {
        this.config = Objects.requireNonNullElseGet(config, ConfigureTransformationDerivedColumnsNumeric::new);
        this.configData = configData;
        buildInputsTable(this.configData.getColumns());
        if (configuredBefore) {
            buildDerivedColumnsTable(this.config.getDerivedColumns());
        } else {
            buildDerivedColumnsTable(null);
        }
    }

    public ConfigureTransformationDerivedColumnsNumeric getConfig() {
        return config;
    }

    public CloseType getCloseType() {
        return this.closeType;
    }

    private void buildEventHandlers() {
        eClickOk = event -> {
            List<NumericDerivedColumn> numericDerivedColumns = this.numericDerivedColumns.getItems();
            for (NumericDerivedColumn numericDerivedColumn : numericDerivedColumns) {
                if (
                        numericDerivedColumn.getName().equals("")
                                || numericDerivedColumn.getExpression() == null
                                || numericDerivedColumn.getExpression().length == 0
                ) {
                    Utils.alertError("Some fields are empty!!!");
                    return;
                }
            }
            closeType = CloseType.OK;
            config.setDerivedColumns(numericDerivedColumns);
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        };
        eClickCancel = event -> {
            closeType = CloseType.CANCEL;
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        };
        eClickAddRow = event -> {
            numericDerivedColumns.getItems().add(new NumericDerivedColumn());
            numericDerivedColumns.getSelectionModel().selectLast();
        };
        eClickDeleteRow = event -> numericDerivedColumns.getItems().removeAll(
                numericDerivedColumns.getSelectionModel().getSelectedItem()
        );
    }

    private void assignEventHandlers() {
        ok.setOnAction(eClickOk);
        cancel.setOnAction(eClickCancel);
        addRow.setOnAction(eClickAddRow);
        deleteRow.setOnAction(eClickDeleteRow);
    }

    private void buildFunctionsTreeView() {
        treeView.setRoot(TreeFunction.getNumericInstance());
        treeView.setShowRoot(false);
        treeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        treeView.setCellFactory(new Callback<>() {
            @Override
            public TreeCell<String> call(TreeView<String> stringTreeView) {
                TreeCell<String> treeCell = new TreeCell<>() {
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(item);
                        }
                        setGraphic(null);
                    }
                };
                //Add event handler
                treeCell.setOnMouseClicked(event -> {
                    TreeCell<String> cell = (TreeCell<String>) event.getSource();
                    try {
                        String functionType = cell.getTreeItem().getParent().getValue();
                        String functionSpec = cell.getTreeItem().getValue();
                        //
                        description.setText(functionType + " " + functionSpec);
                    } catch (NullPointerException exception) {
                        System.out.println("Oh no!");
                    }
                });
                return treeCell;
            }
        });
    }

    private void buildInputsTable(List<Column> columns) {
        //Clear content
        inputs.getColumns().clear();
        inputs.getItems().clear();
        //Setup table columns
        inputs.setEditable(false);
        TableColumn<Column, String> name = new TableColumn<>("Name");
        name.setSortable(false);
        name.setEditable(false);
        name.prefWidthProperty().bind(inputs.widthProperty().divide(2));
        name.setCellValueFactory(cellValue -> new SimpleStringProperty(cellValue.getValue().getName()));
        TableColumn<Column, String> type = new TableColumn<>("Type");
        type.setSortable(false);
        type.setEditable(false);
        type.prefWidthProperty().bind(inputs.widthProperty().divide(2));
        type.setCellValueFactory(cellValue -> new SimpleStringProperty(cellValue.getValue().getType().toString()));
        //Add columns to table
        inputs.getColumns().add(name);
        inputs.getColumns().add(type);
        //Add items to table
        inputs.getItems().addAll(columns);
    }

    private void buildDerivedColumnsTable(List<NumericDerivedColumn> items) {
        //Clear content
        numericDerivedColumns.getItems().clear();
        numericDerivedColumns.getItems().clear();
        //Setup table columns
        numericDerivedColumns.setEditable(true);
        numericDerivedColumns.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        TableColumn<NumericDerivedColumn, String> name = new TableColumn<>("Name");
        name.setSortable(false);
        name.setEditable(true);
        name.setResizable(false);
        name.prefWidthProperty().bind(numericDerivedColumns.widthProperty().divide(3));
        name.setCellValueFactory(cellValue -> cellValue.getValue().nameProperty());
        name.setCellFactory(TextFieldTableCell.forTableColumn());
        name.setOnEditCommit(event -> {
            boolean isUnique = true;
            String newVal = event.getNewValue();
            int index = event.getTablePosition().getRow();
            List<NumericDerivedColumn> selects = numericDerivedColumns.getItems();
            for (int i = 0; i < selects.size(); i++) {
                if (i != index) {
                    String other = selects.get(i).getName();
                    if (newVal.equals(other)) {
                        isUnique = false;
                        break;
                    }
                }
            }
            if (isUnique) event.getRowValue().setName(newVal);
            name.setVisible(false);
            name.setVisible(true);
        });
        TableColumn<NumericDerivedColumn, String> expression = new TableColumn<>("Expression");
        expression.setSortable(false);
        expression.setEditable(true);
        expression.setResizable(false);
        expression.prefWidthProperty().bind(numericDerivedColumns.widthProperty().divide(1.5));
        expression.setCellValueFactory(cellValue -> {
            SimpleStringProperty string = new SimpleStringProperty();
            String[] operations = cellValue.getValue().getExpression();
            if (operations == null) string.set("");
            else string.set(String.join("", operations));
            return string;
        });
        expression.setCellFactory(derivedColumnStringTableColumn -> {
            TableCell<NumericDerivedColumn, String> cell = (TableCell<NumericDerivedColumn, String>) TableColumn.DEFAULT_CELL_FACTORY.call(derivedColumnStringTableColumn);
            cell.setOnMouseClicked(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/fxml/Calculator.fxml"));

                    Parent root = loader.load();
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setTitle("Calculator");
                    stage.setResizable(false);

                    CalculatorController controller = loader.getController();
                    controller.presetExpressions(configData, cell.getTableRow().getItem().getExpression());

                    Scene scene = new Scene(root);
                    scene.setOnKeyPressed(controller::getString);

                    stage.setScene(scene);
                    stage.showAndWait();

                    if (controller.getCloseType().equals(CloseType.OK)) {
                        cell.getTableRow().getItem().setExpression(controller.getExpression());
                        expression.setVisible(false);
                        expression.setVisible(true);
                        System.out.println(Arrays.toString(controller.getExpression()));
                    }
                } catch (IOException exception) {
                    exception.printStackTrace();
                    System.out.println("Some errors occurred");
                } catch (NullPointerException exception) {
                    System.out.println("Oh no! Null cell");
                }
            });
            return cell;
        });
        //Add columns to
        numericDerivedColumns.getColumns().add(name);
        numericDerivedColumns.getColumns().add(expression);
        //Add items if not null
        if (items != null) numericDerivedColumns.getItems().addAll(items);
    }
}
