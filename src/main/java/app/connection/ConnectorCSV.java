package app.connection;

import app.model.Column;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.io.csv.CsvWriteOptions;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class ConnectorCSV implements Connector {
    private String path;
    private boolean hasLabel;

    public ConnectorCSV(String path) {
        this.path = path;
        this.hasLabel = false;
    }

    public boolean getHasLabel() {
        return this.hasLabel;
    }

    public void setHasLabel(boolean t) {
        this.hasLabel = t;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Table retrieveData(boolean hasLabel, List<String> selectedField,int maxRow) throws IOException {
        if (hasLabel) {
//            List<String>
            CsvReadOptions.Builder builder =
                    CsvReadOptions.builder(this.path)                                        // table is tab-delimited
                            .header(true);
            CsvReadOptions options = builder.build();// no header
            Table table = Table.read().usingOptions(options);
            table.retainColumns(selectedField.toArray(new String[selectedField.size()]));
            System.out.println(table.first(3));
            if(maxRow!=0){
                table = table.first(maxRow);
            }
            return table;
        } else {
            CsvReadOptions.Builder builder =
                    CsvReadOptions.builder(this.path)                                        // table is tab-delimited
                            .header(false);
            CsvReadOptions options = builder.build();// no header
            Table table = Table.read().usingOptions(options);
            for (int i = 0; i < table.columnCount(); i++) {
                table.column(i).setName("column " + i);
            }
            table.retainColumns(selectedField.toArray(new String[selectedField.size()]));
            System.out.println(table.first(3));
            if(maxRow!=0){
                table = table.first(maxRow);
            }
            return table;
        }
    }

    public List<String> retrieveFieldName() throws IOException {
        if (hasLabel) {
            CsvReadOptions.Builder builder =
                    CsvReadOptions.builder(this.path)                                        // table is tab-delimited
                            .header(true)
                            .sampleSize(0)
                            .sample(true);
            CsvReadOptions options = builder.build();// no header
            Table table = Table.read().usingOptions(options);
            return table.columnNames();
        } else {
            CsvReadOptions.Builder builder =
                    CsvReadOptions.builder(this.path)                                        // table is tab-delimited
                            .header(false)
                            .sampleSize(0)
                            .sample(true);
            CsvReadOptions options = builder.build();// no header
            Table table = Table.read().usingOptions(options);
            List<String> field = new ArrayList<String>();
            for (Integer i = 0; i < table.columnCount(); i++) {
                field.add("column " + i);
            }
            return field;

        }

    }

    public List<Column> retrieveColumns() throws IOException {
        List<Column> columns = new ArrayList<>();
        if (this.hasLabel) {
            CsvReadOptions.Builder builder =
                    CsvReadOptions.builder(this.path)// table is tab-delimited
                            .header(true)
                            .sampleSize(10)
                            .sample(true);
            CsvReadOptions options = builder.build();// no header
            Table table = Table.read().usingOptions(options);
            List<String> names = table.columnNames();
            ColumnType[] types = table.columnTypes();
            for (int i = 0; i < names.size(); i++) {
                columns.add(
                        new Column(
                                names.get(i),
                                types[i]
                        ));
            }
        } else {
            CsvReadOptions.Builder builder =
                    CsvReadOptions.builder(this.path)                                        // table is tab-delimited
                            .header(false)
                            .header(true)
                            .sampleSize(10)
                            .sample(true);
            CsvReadOptions options = builder.build();// no header
            Table table = Table.read().usingOptions(options);
            ColumnType[] types = table.columnTypes();
            for (Integer i = 0; i < types.length; i++) {
                columns.add(
                        new Column(
                                "column " + i,
                                types[i]
                        ));
            }
        }

        return columns;
    }


    public void insert(Table table) throws IOException {
        Table newTable = Table.create();
        assert path != null;
        File file = new File(path);
        file.createNewFile();
//        List<String> listName = this.retrieveFieldName();
        List<Column> columns = this.retrieveColumns();

        for (int i = 0; i < columns.size(); i++) {

            if (!table.columnNames().contains(columns.get(i).getName())) {
                System.out.println("herer " + columns.get(i).getName());
                tech.tablesaw.columns.Column nc1 = table.column(0).emptyCopy(table.column(0).size()).setName(columns.get(i).getName());
//                nc1.set(nc1.isMissing(),"null");
                newTable.addColumns(nc1);
            } else {
                tech.tablesaw.columns.Column nc2 = table.column(columns.get(i).getName());
                System.out.println("herer type " + columns.get(i).getType());
                if (columns.get(i).getType().equals(ColumnType.INTEGER)) {

                    nc2 = nc2.asStringColumn().parseFloat().asIntColumn();
                } else if (columns.get(i).getType().equals(ColumnType.FLOAT)) {
                    nc2 = nc2.asStringColumn().parseFloat();
                } else if (columns.get(i).getType().equals(ColumnType.DOUBLE)) {
                    nc2 = nc2.asStringColumn().parseFloat().asDoubleColumn();
                } else if (columns.get(i).getType().equals(ColumnType.STRING)) {
                    nc2 = nc2.asStringColumn();
                }
                newTable.addColumns(nc2);
            }
        }

        OutputStream out = Files.newOutputStream(file.toPath(), StandardOpenOption.APPEND, StandardOpenOption.WRITE);
        newTable.write().usingOptions(CsvWriteOptions.builder(out).header(false).build());
    }


    @Override
    public void String() {

    }

    @Override
    public void Connect() {

    }
}
