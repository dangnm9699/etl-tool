package app.model;

import javafx.beans.property.SimpleStringProperty;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.util.ArrayList;
import java.util.List;

public class StringConvert {
    private final SimpleStringProperty name;
    private final SimpleStringProperty type;

    public StringConvert(String name, String type) {
        this.name = new SimpleStringProperty(name);
        this.type = new SimpleStringProperty(type);
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

    public String getType() {
        return type.get();
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public SimpleStringProperty typeProperty() {
        return type;
    }

    /**
     * @param table a table
     * @author chungpv-1008
     */
    public void convert(Table table) {
        StringColumn col = table.stringColumn(name.get());
        String stringConvertType = type.get();
        if (stringConvertType.equals(StringConvertType.Ignore.toString())) return;
        if (stringConvertType.equals(StringConvertType.Lowercase.toString())) {
            StringColumn newCol = col.lowerCase();
            table.replaceColumn(name.get(), newCol.setName(name.get()));
        }
        if (stringConvertType.equals(StringConvertType.Uppercase.toString())) {
            StringColumn newCol = col.upperCase();
            table.replaceColumn(name.get(), newCol.setName(name.get()));
        }
        if (stringConvertType.equals(StringConvertType.Capitalize.toString())) {
            List<String> colVal = new ArrayList<>();
            col.forEach(s -> {
                StringBuilder builder = new StringBuilder(s);
                for (int k = 0; k < builder.length(); k++) {
                    if (k == 0) {
                        builder.setCharAt(k, Character.toUpperCase(builder.charAt(k)));
                    } else if (builder.charAt(k - 1) == ' ' && builder.charAt(k) != ' ') {
                        builder.setCharAt(k, Character.toUpperCase(builder.charAt(k)));
                    } else if (builder.charAt(k - 1) != ' ' && builder.charAt(k) != ' ') {
                        builder.setCharAt(k, Character.toLowerCase(builder.charAt(k)));
                    }
                }
                colVal.add(builder.toString());
            });
            table.replaceColumn(name.get(), StringColumn.create(name.get(), colVal));
        }
    }
}
