package tocadaraposa.config.database;

import java.sql.Connection;

public interface DatabaseConnect {
    boolean connect();
    boolean closeConnection();
    Connection getConnection();
    String getUser();
    String getPassword();
    String getDatabaseName();
    String getUrl();
}