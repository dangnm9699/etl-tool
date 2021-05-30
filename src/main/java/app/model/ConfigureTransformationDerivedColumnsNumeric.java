package app.model;

import java.util.List;

public class ConfigureTransformationDerivedColumnsNumeric {
    private List<NumericDerivedColumn> numericDerivedColumns;

    public List<NumericDerivedColumn> getDerivedColumns() {
        return numericDerivedColumns;
    }

    public void setDerivedColumns(List<NumericDerivedColumn> numericDerivedColumns) {
        this.numericDerivedColumns = numericDerivedColumns;
    }
}
