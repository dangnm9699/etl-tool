package app.connection;

import app.custom.Workspace;
import app.model.Column;
import app.model.Driver;
import app.model.MetadataDB;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConnectorDatabase implements Connector {
    private Connection connection;
    private MetadataDB metadata;

    public ConnectorDatabase(MetadataDB metadata) {
        this.metadata = metadata;
    }

    public Connection getConnection() {
        return this.connection;
    }

    public void setMetadata(MetadataDB metadata) {
        this.metadata = metadata;
    }

    @Override
    public void String() {
        System.out.println("Database Connector");
    }

    @Override
    public void Connect() {
        String url = "";
        String driver = "";
        switch (metadata.getDriver()) {
            case MySQL:
                driver = "com.mysql.cj.jdbc.Driver";
                url = "jdbc:$driver://$host$port/$db".
                        replace("$driver", "mysql").
                        replace("$host", metadata.getHost()).
                        replace("$port", metadata.getPort().equals("") ? "" : ":" + metadata.getPort()).
                        replace("$db", metadata.getDatabase());
                break;
            case PostgreSQL:
                driver = "org.postgresql.Driver";
                url = "jdbc:$type://$host:$port/$db".
                        replace("$type", "postgresql").
                        replace("$host", metadata.getHost()).
                        replace("$port", metadata.getPort()).
                        replace("$db", metadata.getDatabase());
                break;
            case SqlServer:
                driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
                url = "jdbc:$type://$host:$port;databaseName=$db".
                        replace("$type", "sqlserver").
                        replace("$host", metadata.getHost()).
                        replace("$port", metadata.getPort()).
                        replace("$db", metadata.getDatabase());
                break;
        }
        try {
            System.out.println(url);
            Class.forName(driver);
            connection = DriverManager.getConnection(url, metadata.getUsername(), metadata.getPassword());
        } catch (Exception exception) {
            Workspace.appendLn(exception.getMessage());
            System.out.println("Some errors occurred while connecting DB");
            connection = null;
        }
    }

    public Table retrieveData(String tableName, List<String> selectedField) throws SQLException {
        StringBuilder fields = new StringBuilder();
        if (metadata.getDriver().equals(Driver.SqlServer)) {
            for (String s : selectedField) fields.append("[").append(s).append("]").append(",");
            fields.deleteCharAt(fields.length() - 1).append(" FROM ").append("[").append("$table").append("]");
        } else {
            for (String s : selectedField) fields.append("`").append(s).append("`").append(",");
            fields.deleteCharAt(fields.length() - 1).append(" FROM ").append("`").append("$table").append("`");
        }
        ResultSet resultSet = execute(
                "SELECT " + fields,
                "$table",
                tableName);
        return Table.read().db(resultSet, tableName);
    }

    public Table retrieveData(String tableName, List<String> selectedField, int limit) throws SQLException {
        StringBuilder fields = new StringBuilder("SELECT ");
        if (metadata.getDriver().equals(Driver.SqlServer)) {
            fields.append("TOP ").append(limit).append(" ");
            for (String s : selectedField) fields.append("[").append(s).append("]").append(",");
            fields.deleteCharAt(fields.length() - 1).append(" FROM ").append("[").append("$table").append("]");
        } else {
            for (String s : selectedField) fields.append("`").append(s).append("`").append(",");
            fields.deleteCharAt(fields.length() - 1).append(" FROM ").append("`").append("$table").append("`").append(" LIMIT ").append(limit);
        }
        ResultSet resultSet = execute(
                fields.toString(),
                "$table",
                tableName);
        return Table.read().db(resultSet, tableName);
    }

    public List<String> retrieveFieldName(String tableName) throws SQLException {
        String preQuery = "SELECT * FROM " + (metadata.getDriver().equals(Driver.SqlServer) ? "[$table]" : "`$table`") + " WHERE 0 = 1";
        String target = "$table";
        ResultSet resultSet = execute(
                preQuery,
                target,
                tableName);
        Table table = Table.read().db(resultSet);
        return new ArrayList<>(table.columnNames());
    }

    public List<Column> retrieveColumns(String tableName) throws SQLException {
        List<Column> columns = new ArrayList<>();
        String preQuery = "SELECT * FROM " + (metadata.getDriver().equals(Driver.SqlServer) ? "[$table]" : "`$table`") + " WHERE 0 = 1";
        String target = "$table";
        ResultSet resultSet = execute(
                preQuery,
                target,
                tableName
        );
        Table table = Table.read().db(resultSet);
        List<String> names = table.columnNames();
        ColumnType[] types = table.columnTypes();
        for (int i = 0; i < names.size(); i++) {
            columns.add(
                    new Column(
                            names.get(i),
                            types[i]
                    ));
        }
        return columns;
    }

    public List<String> retrieveTableNames() throws SQLException {
        List<String> tableNames = new ArrayList<>();
        String preQuery = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE "
                + (metadata.getDriver().equals(Driver.SqlServer) ? "TABLE_CATALOG" : "TABLE_SCHEMA")
                + " = '$schema'";
        String target = "$schema";
        ResultSet resultSet = execute(
                preQuery,
                target,
                metadata.getDatabase()
        );
        while (resultSet.next()) {
            String table = resultSet.getString(1);
            tableNames.add(table);
        }
        return tableNames;
    }

    public void create(String sql) throws SQLException {
        assert connection != null;
        Statement statement = connection.createStatement();
        statement.executeUpdate(sql);
    }

    public void insert(Table table, String tableName) throws SQLException {
        assert connection != null;
        String sql = "INSERT INTO $table ($columns) VALUES ($values)";
        List<String> columns = table.columnNames();
        String tableNameHolder = metadata.getDriver().equals(Driver.SqlServer)
                ? "[$tableName]".replace("$tableName", tableName)
                : "`$tableName`".replace("$tableName", tableName);

        StringBuilder columnsHolder = new StringBuilder();
        if (metadata.getDriver().equals(Driver.SqlServer)) {
            for (String column : columns) {
                columnsHolder.append("[$columns],".replace("$columns", column));
            }
        } else {
            for (String column : columns) {
                columnsHolder.append("`$columns`,".replace("$columns", column));
            }
        }
        columnsHolder.deleteCharAt(columnsHolder.length() - 1);

        StringBuilder values = new StringBuilder();
        values.append("?,".repeat(columns.size()));
        values.deleteCharAt(values.length() - 1);

        sql = sql
                .replace("$table", tableNameHolder)
                .replace("$columns", columnsHolder.toString())
                .replace("$values", values.toString());

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < table.rowCount(); i++) {
            for (int j = 0; j < table.columnCount(); j++) {
                ColumnType type = table.column(j).type();
                if (table.column(j).isMissing(i)) preparedStatement.setNull(j + 1, Types.NULL);
                else if (type.equals(ColumnType.INTEGER)) {
                    preparedStatement.setInt(j + 1, table.row(i).getInt(j));
                } else if (type.equals(ColumnType.DOUBLE)) {
                    preparedStatement.setDouble(j + 1, table.row(i).getDouble(j));
                } else if (type.equals(ColumnType.STRING)) {
                    preparedStatement.setString(j + 1, table.row(i).getString(j));
                } else if (type.equals(ColumnType.LOCAL_DATE)) {
                    preparedStatement.setDate(j + 1, Date.valueOf(table.row(i).getDate(j)));
                } else {
                    throw new RuntimeException("This data type is not supported");
                }
            }
            preparedStatement.addBatch();
            if (i % 1000 == 0 || i == table.rowCount() - 1) {
                preparedStatement.executeBatch();
            }
        }
    }

    public ResultSet execute(String preQuery, String target, String replacement) {
        assert connection != null;
        String query = preQuery.replace(target, replacement);
        ResultSet resultSet = null;
        try {
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
        } catch (SQLException sqlException) {
            Workspace.appendLn(sqlException.getMessage());
            sqlException.printStackTrace();
        }
        return resultSet;
    }
}
