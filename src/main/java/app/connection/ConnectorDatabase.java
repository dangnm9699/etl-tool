package app.connection;

import app.model.Dataset;
import app.model.Driver;
import app.model.Field;
import app.model.MetadataDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
            exception.printStackTrace();
            System.out.println("Some errors occurred while connecting DB");
            connection = null;
        }
    }

    public Dataset retrieveData(String tableName) throws SQLException {
        Dataset dataset = new Dataset();
        List<List<Object>> data = new ArrayList<>();
        List<String> fields = this.retrieveFieldName(tableName);
        ResultSet resultSet = execute(
                "SELECT * FROM $table",
                "$table",
                tableName);
        while (resultSet.next()) {
            ObservableList<Object> row = FXCollections.observableArrayList();
            for (int i = 0; i < fields.size(); i++) {
                row.add(resultSet.getObject(fields.get(i)));
            }
            data.add(row);
        }
        dataset.setData(data);
        return dataset;
    }

    public List<String> retrieveFieldName(String tableName) throws SQLException {
        List<String> fields = new ArrayList<>();
        ResultSet resultSet = execute(
                "SELECT COLUMN_NAME, " + (metadata.getDriver().equals(Driver.SqlServer) ? "DATA_TYPE" : "COLUMN_TYPE") +" FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='$table';",
                "$table",
                tableName);
        while (resultSet.next()) {
            String field = resultSet.getString(1);
            fields.add(field);
        }
        return fields;
    }

    public List<String> retrieveTables() throws SQLException {
        List<String> tables = new ArrayList<>();
        ResultSet resultSet = execute(
                "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE "+ (metadata.getDriver().equals(Driver.SqlServer) ? "TABLE_CATALOG" : "TABLE_SCHEMA") + " = '$schema';",
                "$schema",
                metadata.getDatabase()
        );
        while (resultSet.next()) {
            String table = resultSet.getString(1);
            tables.add(table);
        }
        return tables;
    }

    public ResultSet execute(String preQuery, String target, String replacement) {
        assert connection != null;
        String query = preQuery.replace(target, replacement);
        ResultSet resultSet = null;
        try {
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return resultSet;
    }
}
