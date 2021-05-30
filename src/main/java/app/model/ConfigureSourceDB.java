package app.model;

import java.util.List;

public class ConfigureSourceDB {
    private Driver driver;
    private String host;
    private String port;
    private String username;
    private String password;
    private String database;
    private String table;
    private List<MappingSelect> selects;

    public ConfigureSourceDB() {
    }

    public MetadataDB getMetadata() {
        return new MetadataDB(
                this.driver, this.host, this.port,
                this.username, this.password, this.database
        );
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public List<MappingSelect> getSelects() {
        return selects;
    }

    public void setSelects(List<MappingSelect> selects) {
        this.selects = selects;
    }

    public List<Field> getFields() {
        return MappingSelectUtils.getFields(this.getSelects());
    }
}
