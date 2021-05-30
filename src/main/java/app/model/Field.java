package app.model;

public class Field {
    private final String name;
    private final DataType dataType;

    public Field(String name, DataType dataType) {
        this.name = name;
        this.dataType = dataType;
    }

    public String getName() {
        return name;
    }

    public DataType getDataType() {
        return dataType;
    }
}
