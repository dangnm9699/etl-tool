package app.model;

import java.util.ArrayList;
import java.util.List;

public class ConfigureTransformationRemoveNull {
    private List<ColumnSelect> columnSelects;

    public List<ColumnSelect> getColumnSelects() {
        return columnSelects;
    }

    public void setColumnSelects(List<ColumnSelect> columnSelects) {
        this.columnSelects = columnSelects;
    }

    public List<Integer> getIndexOfSelectedColumns() {
        List<Integer> selected = new ArrayList<>();
        for (int i = 0; i < columnSelects.size(); i++) {
            if (columnSelects.get(i).isSelected()) selected.add(i);
        }
        return selected;
    }

    public ConfigData getConfigData() {
        return ConfigData.getInstanceFromColumnSelectList(columnSelects);
    }
}
