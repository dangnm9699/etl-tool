package app.model;

import java.util.List;

public class ConfigureTransformationStringMerge {
    private List<SelectColumn> selectColumns;
    private String name;

    public List<SelectColumn> getSelectColumns() {
        return selectColumns;
    }

    public void setSelectColumns(List<SelectColumn> selectColumns) {
        this.selectColumns = selectColumns;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
