package app.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class MappingSelect {
    private final SimpleStringProperty external;
    private final SimpleStringProperty output;
    private final SimpleBooleanProperty selected;

    public MappingSelect(String external, String output, boolean selected) {
        this.external = new SimpleStringProperty(external);
        this.output = new SimpleStringProperty(output);
        this.selected = new SimpleBooleanProperty(selected);
    }

    public String getExternal() {
        return external.get();
    }

    public SimpleStringProperty externalProperty() {
        return external;
    }

    public void setExternal(String external) {
        this.external.set(external);
    }

    public String getOutput() {
        return output.get();
    }

    public SimpleStringProperty outputProperty() {
        return output;
    }

    public void setOutput(String output) {
        this.output.set(output);
    }

    public boolean isSelected() {
        return selected.get();
    }

    public SimpleBooleanProperty selectedProperty() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }
}
