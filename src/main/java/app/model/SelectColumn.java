package app.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import tech.tablesaw.api.*;

public class SelectColumn {
    private final SimpleStringProperty name;
    private final SimpleBooleanProperty selected;

    public SelectColumn(String name, boolean selected) {
        this.name = new SimpleStringProperty(name);
        this.selected = new SimpleBooleanProperty(selected);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    public SimpleBooleanProperty selectedProperty() {
        return selected;
    }

    /**
     * @param data a table
     * @author VuKhacChinh
     */
    public void asRatio(Table data) {
        DoubleColumn newCol = getDoubleColumn(data).asRatio();
        data.replaceColumn(name.get(), newCol.setName(name.get()));
    }

    /**
     * @param data a table
     * @author VuKhacChinh
     */
    public void abs(Table data) {
        DoubleColumn newCol = getDoubleColumn(data).abs();
        data.replaceColumn(name.get(), newCol.setName(name.get()));
    }

    /**
     * @param data a table
     * @author VuKhacChinh
     */
    public void scale(Table data) {
        Double max = getDoubleColumn(data).abs().max();
        DoubleColumn newCol = getDoubleColumn(data).divide(max);
        data.replaceColumn(name.get(), newCol.setName(name.get()));
    }

    /**
     * @param data a table
     * @author chungpv-1008
     */
    public void split(Table data) {
        DateColumn col = data.dateColumn(name.get());
        StringColumn dayOfWeek = StringColumn.create(name.get() + " (Day of week)", col.dayOfWeek().lowerCase().asList());
        IntColumn dayOfMonth = col.dayOfMonth();
        dayOfMonth.setName(name.get() + " (Day of month)");
        StringColumn month = StringColumn.create(name.get() + " (Month)", col.month().lowerCase().asList());
        IntColumn year = col.year();
        year.setName(name.get() + " (Year)");
        data.addColumns(dayOfWeek, dayOfMonth, month, year);
    }

    private DoubleColumn getDoubleColumn(Table data) {
        DoubleColumn newCol = DoubleColumn.create(name.get());
        ColumnType type = data.column(name.get()).type();
        if (type.equals(ColumnType.INTEGER)) {
            newCol = data.intColumn(name.get()).asDoubleColumn();
        } else if (type.equals(ColumnType.FLOAT)) {
            newCol = data.floatColumn(name.get()).asDoubleColumn();
        } else if (type.equals(ColumnType.DOUBLE)) {
            newCol = data.doubleColumn(name.get()).asDoubleColumn();
        } else if (type.equals(ColumnType.LONG)) {
            newCol = data.longColumn(name.get()).asDoubleColumn();
        } else if (type.equals(ColumnType.SHORT)) {
            newCol = data.shortColumn(name.get()).asDoubleColumn();
        }
        return newCol;
    }
}
