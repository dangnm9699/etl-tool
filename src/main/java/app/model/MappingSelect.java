package app.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import tech.tablesaw.api.ColumnType;

public class MappingSelect {
    private final SimpleStringProperty external;
    private final SimpleStringProperty output;
    private final SimpleBooleanProperty selected;
    private final ColumnType type;

    public MappingSelect(String external, String output, boolean selected, ColumnType type) {
        this.external = new SimpleStringProperty(external);
        this.output = new SimpleStringProperty(output);
        this.selected = new SimpleBooleanProperty(selected);
        this.type = type;
    }

    public String getExternal() {
        return this.external.get();
    }

    public void setExternal(String external) {
        this.external.set(external);
    }

    public SimpleStringProperty externalProperty() {
        return this.external;
    }

    public String getOutput() {
        return this.output.get();
    }

    public void setOutput(String output) {
        this.output.set(output);
    }

    public SimpleStringProperty outputProperty() {
        return this.output;
    }

    public boolean isSelected() {
        return this.selected.get();
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    public SimpleBooleanProperty selectedProperty() {
        return this.selected;
    }

    public ColumnType getType() {
        return this.type;
    }
}
