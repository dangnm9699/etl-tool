package app.model;

import tech.tablesaw.api.ColumnType;

import java.util.ArrayList;
import java.util.List;

public class ConfigData {
    private List<Column> columns;

    public ConfigData(List<Column> columns) {
        this.columns = columns;
    }

    public static ConfigData getInstanceFromMappingList(List<Mapping> mappings) {
        List<Column> columns = new ArrayList<>();
        for (Mapping mapping : mappings) {
            System.out.println(mapping.getType());
            columns.add(
                    new Column(
                            mapping.getOutput(),
                            mapping.getType()
                    ));
        }
        return new ConfigData(columns);
    }

    public static ConfigData getInstanceFromMappingSelectList(List<MappingSelect> mappingSelects) {
        List<Column> columns = new ArrayList<>();
        for (MappingSelect mappingSelect : mappingSelects) {
            if (mappingSelect.isSelected()) {
                columns.add(
                        new Column(
                                mappingSelect.getOutput(),
                                mappingSelect.getType()
                        ));
            }
        }
        return new ConfigData(columns);
    }

    public static ConfigData getInstanceFromColumnSelectList(List<ColumnSelect> columnSelects) {
        List<Column> columns = new ArrayList<>();
        for (ColumnSelect columnSelect : columnSelects) {
            columns.add(
                    new Column(
                            columnSelect.getColumn(),
                            columnSelect.getType()
                    ));
        }
        return new ConfigData(columns);
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public void addDoubleColumn(String name) {
        columns.add(new Column(name, ColumnType.DOUBLE));
    }

    public List<String> getColumnNames() {
        List<String> columnNames = new ArrayList<>();
        for (Column column : columns) {
            columnNames.add(column.getName());
        }
        return columnNames;
    }

    public List<String> getNumericColumnNames() {
        List<String> columnNames = new ArrayList<>();
        for (Column column : columns) {
            ColumnType type = column.getType();
            if (
                    type.equals(ColumnType.INTEGER)
                            || type.equals(ColumnType.DOUBLE)
                            || type.equals(ColumnType.FLOAT)
                            || type.equals(ColumnType.LONG)
                            || type.equals(ColumnType.SHORT)
            ) columnNames.add(column.getName());
        }
        return columnNames;
    }
}
