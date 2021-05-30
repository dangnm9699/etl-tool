package app.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DefaultStringConverter;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static void alertError(String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void buildMappingSelectsTable(TableView<MappingSelect> table, List<MappingSelect> items) {
        table.setEditable(true);
        TableColumn<MappingSelect, Boolean> selected = new TableColumn<>();
        selected.prefWidthProperty().bind(table.widthProperty().divide(5));
        selected.setSortable(false);
        selected.setResizable(false);
        TableColumn<MappingSelect, String> external = new TableColumn<>("External");
        external.prefWidthProperty().bind(table.widthProperty().divide(2.5));
        external.setSortable(false);
        selected.setResizable(false);
        TableColumn<MappingSelect, String> output = new TableColumn<>("Output");
        output.prefWidthProperty().bind(table.widthProperty().divide(2.5));
        output.setSortable(false);
        output.setResizable(false);

        table.getColumns().add(selected);
        table.getColumns().add(external);
        table.getColumns().add(output);

        selected.setCellValueFactory(cell -> {
            MappingSelect cellValue = cell.getValue();
            SimpleBooleanProperty property = cellValue.selectedProperty();

            // Add listener to handler change
            property.addListener((observable, oldValue, newValue) -> cellValue.setSelected(newValue));

            return property;
        });
        selected.setCellFactory(col -> new CheckBoxTableCell<>());
        external.setCellValueFactory(new PropertyValueFactory<>("external"));

        output.setCellValueFactory(new PropertyValueFactory<>("output"));
        output.setCellFactory(TextFieldTableCell.forTableColumn());
        output.setOnEditCommit(event -> {
            boolean isUnique = true;
            String newVal = event.getNewValue();
            int index = event.getTablePosition().getRow();
            List<MappingSelect> selects = table.getItems();
            for (int i = 0; i < selects.size(); i++) {
                if (i != index) {
                    String other = selects.get(i).getOutput();
                    if (newVal.equals(other)) {
                        isUnique = false;
                        break;
                    }
                }
            }
            if (isUnique) event.getRowValue().setOutput(newVal);
            output.setVisible(false);
            output.setVisible(true);
        });

        table.getItems().addAll(items);
    }

    public static void buildColumnSelectsTable(TableView<ColumnSelect> table, List<ColumnSelect> items) {
        table.setEditable(true);
        TableColumn<ColumnSelect, Boolean> selected = new TableColumn<>();
        selected.prefWidthProperty().bind(table.widthProperty().divide(10 / 3));
        selected.setSortable(false);
        TableColumn<ColumnSelect, String> column = new TableColumn<>("Column");
        column.prefWidthProperty().bind(table.widthProperty().divide(10 / 7));
        column.setSortable(false);

        table.getColumns().add(selected);
        table.getColumns().add(column);

        selected.setCellValueFactory(cell -> {
            ColumnSelect cellValue = cell.getValue();
            SimpleBooleanProperty property = cellValue.selectedProperty();

            // Add listener to handler change
            property.addListener((observable, oldValue, newValue) -> cellValue.setSelected(newValue));

            return property;
        });
        selected.setCellFactory(col -> new CheckBoxTableCell<>());
        column.setCellValueFactory(new PropertyValueFactory<>("column"));

        table.getItems().addAll(items);
    }

    public static void buildMappingTable(TableView<Mapping> table, List<Mapping> items, List<String> inputs) {
        table.getItems().clear();
        table.getColumns().clear();
        TableColumn<Mapping, String> input = new TableColumn<>("Input");
        input.prefWidthProperty().bind(table.widthProperty().divide(2));
        TableColumn<Mapping, String> output = new TableColumn<>("Output");
        output.prefWidthProperty().bind(table.widthProperty().divide(2));

        table.getColumns().add(input);
        table.getColumns().add(output);

        List<String> choices = new ArrayList<>(inputs);
        choices.add("ignore this");

        input.setCellFactory(ComboBoxTableCell.forTableColumn(new DefaultStringConverter(), choices.toArray(new String[0])));

        input.setCellValueFactory(cell -> {
            Mapping cellValue = cell.getValue();
            SimpleStringProperty property = cellValue.inputProperty();

            property.addListener((obs, oldVal, newVal) -> {
                if (!oldVal.equals("ignore this")) {
                    if (!choices.contains(oldVal)) choices.add(oldVal);
                }
                if (!newVal.equals("ignore this")) choices.remove(newVal);

                List<String> nullChoices = new ArrayList<>();
                input.setCellFactory(ComboBoxTableCell.forTableColumn(new DefaultStringConverter(), nullChoices.toArray(new String[0])));
                input.setCellFactory(ComboBoxTableCell.forTableColumn(new DefaultStringConverter(), choices.toArray(new String[0])));
                cellValue.setInput(newVal);
            });

            return property;
        });

        output.setCellValueFactory(new PropertyValueFactory<>("output"));

        table.getItems().addAll(items);
    }

    public static List<MappingSelect> buildMappingSelectList(List<Column> columns) {
        List<MappingSelect> selects = new ArrayList<>();
        for (Column column : columns) {
            MappingSelect select = new MappingSelect(
                    column.getName(),
                    column.getName(),
                    true,
                    column.getType());
            selects.add(select);
        }
        return selects;
    }

    public static List<Mapping> buildMappingList(List<String> fields) {
        List<Mapping> selects = new ArrayList<>();
        for (String field : fields) {
            Mapping select = new Mapping(
                    "ignore this", field,
                    null);
            selects.add(select);
        }
        return selects;
    }

    public static int compare2StringLists(List<String> list1, List<String> list2) {
        List<String> differences1 = new ArrayList<>(list1);
        differences1.removeAll(list2);
        List<String> differences2 = new ArrayList<>(list2);
        differences2.removeAll(list1);
        return differences1.size() + differences2.size();
    }

    /**
     * Validate an expression
     *
     * @param expression a string array
     * @return boolean
     * @author VuKhacChinh
     */
    public static boolean validateExpression(String[] expression) {
        String f = expression[0];
        if (isOperator(f) || f.equals(")")) return false;
        int check = 0;
        if (f.equals("(")) check++;
        int n = expression.length;
        for (int i = 1; i < n; ++i) {
            String p = expression[i];
            String q = expression[i - 1];
            if (isOperator(p)) {
                if (isOperator(q) || q.equals("(")) return false;
            } else {
                if (p.equals("(")) {
                    ++check;
                    if (!isOperator(q)) return false;
                } else {
                    if (p.equals(")")) {
                        --check;
                        if (check < 0) return false;
                        if (isOperator(q) || q.equals("(")) return false;
                    } else if (!(isOperator(q) || q.equals("("))) return false;
                }
            }
            if (i == n - 1 && isOperator(p)) return false;
        }
        return check == 0;
    }

    /**
     * Check if a character is an operator
     *
     * @param op a single character string
     * @return boolean
     * @author VuKhacChinh
     */
    public static boolean isOperator(String op) {
        return op.equals("+") || op.equals("-") || op.equals("*") || op.equals("/") || op.equals("%");
    }

    /**
     * Get operator priority
     *
     * @param op a single character string
     * @return int
     * @author VuKhacChinh
     */
    public static int getPriority(String op) {
        if (op.equals("*") || op.equals("/") || op.equals("%"))
            return 2;
        if (op.equals("+") || op.equals("-"))
            return 1;
        return 0;
    }
}
