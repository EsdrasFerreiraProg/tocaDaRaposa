package tocadaraposa.config.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySqlConnection implements DatabaseConnect{

    protected Connection con;
    private final String url;
    private String user, password, databaseName;

    public MySqlConnection(String user, String password, String databaseName){
        con = null;
        url = "jdbc:mysql://localhost:3306/" + databaseName + "?useTimezone=true&serverTimezone=America/Sao_Paulo&allowPublicKeyRetrieval=true&useSSL=false";
        this.user = user;
        this.password = password;
        this.databaseName = databaseName;
    }

    public MySqlConnection(DatabaseConfigObject dbconfig){
        this(dbconfig.getUser(), dbconfig.getPassword(), dbconfig.getDatabaseName());
    }

    public MySqlConnection(String user, String password){
        con = null;
        url = "jdbc:mysql://localhost:3306/";
        this.user = user;
        this.password = password;
        this.databaseName = "";
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean connect(){
        try {
            con = DriverManager.getConnection(url, user, password);
            System.out.println("DB_LOG -> Database Connected!");
            return true;
        } catch (SQLException ex) {
            System.out.println("DB_LOG -> Error trying to connect on DataBase!");
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public boolean closeConnection(){
        if(con != null){
            try {
                con.close();
                System.out.println("DB_LOG -> Database Disconected!");
                return true;
            } catch (SQLException ex) {
                System.out.println("DB_LOG -> Error trying to Disconnect on DataBase!");
                throw new RuntimeException(ex.getMessage());
            }
        }
        return false;
    }

    @Override
    public Connection getConnection(){
        return this.con;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getDatabaseName() {
        return databaseName;
    }
}
