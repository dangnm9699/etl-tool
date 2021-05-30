package app.model;

import java.util.List;

public class ConfigureTransformationNumericMinMax {
    private List<SelectColumn> selectColumns;
    private String minMax;
    private String name;

    public List<SelectColumn> getNumericSelects() {
        return selectColumns;
    }

    public void setNumericSelects(List<SelectColumn> selectColumns) {
        this.selectColumns = selectColumns;
    }

    public String getMinMax() {
        return minMax;
    }

    public void setMinMax(String minMax) {
        this.minMax = minMax;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
