package app.model;

public class MetadataDB {
    private final Driver driver;
    private final String host;
    private final String port;
    private final String username;
    private final String password;
    private final String database;

    public MetadataDB(
            Driver driver, String host,
            String port, String username,
            String password, String database
    ) {
        this.driver = driver;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;
    }

    public Driver getDriver() {
        return driver;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDatabase() {
        return database;
    }
}
