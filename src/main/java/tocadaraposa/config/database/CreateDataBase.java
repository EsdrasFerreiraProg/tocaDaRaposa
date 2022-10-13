package tocadaraposa.config.database;

import org.apache.commons.io.FileUtils;

import java.util.ArrayList;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

public class CreateDataBase {
    private final ArrayList<String> sqlTables = new ArrayList<>();
    private final String scriptName;
    private boolean initialized = false;
    private final DatabaseConnect con;

    public CreateDataBase(DatabaseConnect con, String scriptName){
        this.con = con;
        this.scriptName = scriptName;
        this.con.connect();
    }

    private boolean createTable(String sql) {
        try {
            Statement stmt = con.getConnection().createStatement();
            stmt.execute(sql);
            stmt.close();
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

    @SuppressWarnings("unused")
    public static boolean checkIfDatabaseExists(String user, String password, String db) {
        try {
            DatabaseConnect con2 = new MySqlConnection(user, password);
            con2.connect();
            Statement stmt = con2.getConnection().createStatement();
            stmt.execute("use " + db + ";");
            stmt.close();
            con2.closeConnection();
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

    @SuppressWarnings("unused")
    public boolean checkIfDatabaseExists() {
        try {
            Statement stmt = con.getConnection().createStatement();
            stmt.execute("use " + con.getDatabaseName() + ";");
            stmt.close();
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

    public boolean checkIfDatabaseExists(String name) {
        try {
            Statement stmt = con.getConnection().createStatement();
            stmt.execute("use " + name + ";");
            stmt.close();
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean createDataBaseAndTables() throws NullPointerException, IOException{
        if(!initialized) {
            readTextFileWithSqlInstructions();
            initialized = true;
        }
        System.out.println("DB_LOG -> Creating database!");
        return sqlTables.stream().noneMatch(t -> (!createTable(t)));
    }

    private void readTextFileWithSqlInstructions() throws NullPointerException, IOException{
        String x = FileUtils.readFileToString(FileUtils.getFile("sql/" + scriptName), (Charset) null);
        sqlTables.addAll(Arrays.asList(x.split(";")));
    }

    public void closeConnection(){
        this.con.closeConnection();
    }

    @SuppressWarnings("unused")
    public DatabaseConnect getDatabaseConnect(){
        return con;
    }

}