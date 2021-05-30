package app.model;

import javafx.beans.property.SimpleStringProperty;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

import java.util.ArrayList;
import java.util.List;

public class NumericDerivedColumn {
    private final SimpleStringProperty name;
    private String[] expression;
    private int o_id;

    public NumericDerivedColumn() {
        name = new SimpleStringProperty("");
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

    public String[] getExpression() {
        return expression;
    }

    public void setExpression(String[] expression) {
        this.expression = expression;
    }

    /**
     * @param table a table
     * @return new double-type column
     * @author VuKhacChinh
     */
    public DoubleColumn calculate(Table table) {
        String[] suffix = toSuffix(expression);
        List<Double> newCol = new ArrayList<>();
        Double[] st = new Double[suffix.length];
        for (Row row : table) {
            int id = -1;
            for (int j = 0; j < this.o_id; ++j) {
                String c = suffix[j];
                if ('0' <= c.charAt(0) && c.charAt(0) <= '9') {
                    st[++id] = Double.parseDouble(c);
                } else {
                    if (c.startsWith("column_")) {
                        String colName = c.substring(7);
                        ColumnType type = table.column(colName).type();
                        Object colObj = row.getObject(colName);
                        if (colObj == null) {
                            st[++id] = null;
                        } else {
                            if (type.equals(ColumnType.INTEGER)) {
                                st[++id] = (double) ((int) colObj);
                            }
                            if (type.equals(ColumnType.DOUBLE)) {
                                st[++id] = (double) colObj;
                            }
                            if (type.equals(ColumnType.LONG)) {
                                st[++id] = (double) ((long) colObj);
                            }
                            if (type.equals(ColumnType.SHORT)) {
                                st[++id] = (double) ((short) colObj);
                            }
                            if (type.equals(ColumnType.FLOAT)) {
                                st[++id] = (double) ((float) colObj);
                            }
                        }
                    } else {
                        Double x2 = st[id--];
                        Double x1 = st[id];
                        Double result = null;
                        if (x1 != null && x2 != null) {
                            if (c.charAt(0) == '+') result = x1 + x2;
                            if (c.charAt(0) == '-') result = x1 - x2;
                            if (c.charAt(0) == '*') result = x1 * x2;
                            if (c.charAt(0) == '/') result = x1 / x2;
                            if (c.charAt(0) == '%') result = x1 % x2;
                        }
                        st[id] = result;
                    }
                }
            }
            newCol.add(st[0]);
        }
        return DoubleColumn.create(getName(), newCol);
    }

    private String[] toSuffix(String[] expr) {
        String[] output = new String[expr.length];
        String[] st = new String[expr.length];
        int id = -1;
        this.o_id = 0;
        for (String i : expr) {
            if (i.equals("(")) {
                st[++id] = "(";
            }
            if (i.equals(")")) {
                while (id >= 0 && !st[id].equals("(")) {
                    output[this.o_id++] = st[id--];
                }
                id--;
            }
            if (i.charAt(0) != '+' && i.charAt(0) != '-' && i.charAt(0) != '*' && i.charAt(0) != '/'
                    && i.charAt(0) != '%' && i.charAt(0) != '(' && i.charAt(0) != ')') {
                output[this.o_id++] = i;
            }
            if (i.charAt(0) == '+' || i.charAt(0) == '-' || i.charAt(0) == '*' || i.charAt(0) == '/' || i.charAt(0) == '%') {
                while (id >= 0 && Utils.getPriority(i) <= Utils.getPriority(st[id])) {
                    output[this.o_id++] = st[id--];
                }
                st[++id] = i;
            }
        }
        while (id >= 0) output[this.o_id++] = st[id--];
        return output;
    }
}
