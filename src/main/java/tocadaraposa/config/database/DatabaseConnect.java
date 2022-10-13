package tocadaraposa.config.database;

import java.sql.Connection;

public interface DatabaseConnect {
    @SuppressWarnings("UnusedReturnValue")
    boolean connect();
    @SuppressWarnings("UnusedReturnValue")
    boolean closeConnection();
    Connection getConnection();
    String getUser();
    String getPassword();
    String getDatabaseName();
    String getUrl();
}