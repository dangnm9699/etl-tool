package app.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.util.ArrayList;
import java.util.List;

public class MappingSelectUtils {
    public static void buildTable(TableView<MappingSelect> table, List<MappingSelect> items) {
        table.setEditable(true);
        TableColumn<MappingSelect, Boolean> selected = new TableColumn<>();
        selected.prefWidthProperty().bind(table.widthProperty().divide(5));
        TableColumn<MappingSelect, String> external = new TableColumn<>("External");
        external.prefWidthProperty().bind(table.widthProperty().divide(2.5));
        TableColumn<MappingSelect, String> output = new TableColumn<>("Output");
        output.prefWidthProperty().bind(table.widthProperty().divide(2.5));

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

    public static List<String> getExternalList(List<MappingSelect> selects) {
        List<String> externals = new ArrayList<>();
        for (MappingSelect select : selects) {
            externals.add(select.getExternal());
        }
        return externals;
    }

    public static int compare2ExternalLists(List<String> list1, List<String> list2) {
        List<String> differences = new ArrayList<>(list1);
        differences.removeAll(list2);
        return differences.size();
    }

    public static List<MappingSelect> buildMappingList(List<String> fields) {
        List<MappingSelect> selects = new ArrayList<>();
        for (String field : fields) {
            MappingSelect select = new MappingSelect(
                    field, field, true
            );
            selects.add(select);
        }
        return selects;
    }

    public static List<Field> getFields(List<MappingSelect> selects) {
        List<Field> fields = new ArrayList<>();
        for (MappingSelect select : selects) {
            if (select.isSelected()) {
                Field field = new Field(select.getOutput(), DataType.STRING);
                fields.add(field);
            }
        }
        return fields;
    }

    public static List<Mapping> getMappings(List<MappingSelect> mappingSelects) {
        List<Mapping> mappings = new ArrayList<>();
        for (MappingSelect mappingSelect : mappingSelects) {
            if (mappingSelect.isSelected()) {
                mappings.add(new Mapping(
                        mappingSelect.getExternal(),
                        mappingSelect.getOutput()
                ));
            }
        }
        return mappings;
    }
}
