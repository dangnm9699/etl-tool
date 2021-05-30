package app.model;

import javafx.beans.property.SimpleStringProperty;
import tech.tablesaw.api.ColumnType;

public class Mapping {
    private final SimpleStringProperty input;
    private final SimpleStringProperty output;
    private final ColumnType type;

    public Mapping(String input, String output, ColumnType type) {
        this.input = new SimpleStringProperty(input);
        this.output = new SimpleStringProperty(output);
        this.type = type;
    }

    public String getInput() {
        return input.get();
    }

    public void setInput(String input) {
        this.input.set(input);
    }

    public SimpleStringProperty inputProperty() {
        return input;
    }

    public String getOutput() {
        return output.get();
    }

    public void setOutput(String output) {
        this.output.set(output);
    }

    public SimpleStringProperty outputProperty() {
        return output;
    }

    public ColumnType getType() {
        return type;
    }
}
