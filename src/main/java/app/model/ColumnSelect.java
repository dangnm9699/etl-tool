package app.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import tech.tablesaw.api.ColumnType;

public class ColumnSelect {
    private final SimpleBooleanProperty selected;
    private final SimpleStringProperty column;
    private final ColumnType type;

    public ColumnSelect(boolean selected, String column, ColumnType type) {
        this.selected = new SimpleBooleanProperty(selected);
        this.column = new SimpleStringProperty(column);
        this.type = type;
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

    public String getColumn() {
        return column.get();
    }

    public void setColumn(String column) {
        this.column.set(column);
    }

    public SimpleStringProperty columnProperty() {
        return column;
    }

    public ColumnType getType() {
        return type;
    }
}
