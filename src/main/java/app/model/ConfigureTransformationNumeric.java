package app.model;

import java.util.List;

public class ConfigureTransformationNumeric {
    private List<SelectColumn> selectColumns;

    public List<SelectColumn> getNumericSelects() {
        return selectColumns;
    }

    public void setNumericSelects(List<SelectColumn> selectColumns) {
        this.selectColumns = selectColumns;
    }
}
