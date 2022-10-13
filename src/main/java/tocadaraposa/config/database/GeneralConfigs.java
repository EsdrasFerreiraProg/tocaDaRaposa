package tocadaraposa.config.database;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GeneralConfigs {

    public static void initDatabaseConfiguration(){
        System.out.println("###### CHECKING IF DATABASE EXIST TO MAKE INITIAL APP CONFIGURATIONS\n");

        try {

            DatabaseConfigObject dbConfig = readDatabaseConfigObjectFile();
            CreateDataBase createDataBase = new CreateDataBase(
                    new MySqlConnection(dbConfig.getUser(), dbConfig.getPassword()), "script.sql");

            replaceDatabaseInfoOnAppProperties(dbConfig);
            makeImageDir();

            if(!createDataBase.checkIfDatabaseExists( "raposa")){
                try {
                    createDataBase.createDataBaseAndTables();
                } catch (IOException | NullPointerException e) {
                    System.out.println("DB LOG -> Não foi possivel executar o script do banco de dados!");
                    createDataBase.closeConnection();
                    throw new RuntimeException(e);
                }
            }
            createDataBase.closeConnection();

        } catch (IOException e) {
            System.out.println("DB LOG -> Não foi possivel executar o arquivo de configuração do banco de dados(json)!");
            throw new RuntimeException(e);
        }
        System.out.println("\nEND OF DATABASE CHECK###########\n");
    }

    private static DatabaseConfigObject readDatabaseConfigObjectFile() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        final File initialFile = new File("databaseConfig.json");
        final InputStream is =
                new DataInputStream(new FileInputStream(initialFile));
        return mapper.readValue(is, DatabaseConfigObject.class);
    }

    private static void replaceDatabaseInfoOnAppProperties(DatabaseConfigObject dbConfig) throws IOException {
        DatabaseConnect c = new MySqlConnection(dbConfig);
        Path origem = Paths.get("TEMPLATE-application.properties");
        Path destino = Paths.get("application.properties");

        Charset charset = StandardCharsets.UTF_8;
        String content = Files.readString(origem);

        content = content.replaceAll("<<URL>>", c.getUrl());
        content = content.replaceAll("<<USER>>", c.getUser());
        content = content.replaceAll("<<PASSWORD>>", c.getPassword());

        Files.write(destino, content.getBytes(charset));
    }

    private static void makeImageDir(){
        File directory = new File("uploaded-images/product");
        if (!directory.exists()) {
            System.out.println("DB LOG -> Creating uploaded-images/product directory!");
            if(!directory.mkdirs()){
                System.out.println("!!!!!!!! NÃO FOI POSSIVEL CRIAR A PASTA DOS PRODUTOS !!!!!!!!!!");
            }
        }
        directory = new File("uploaded-images/category");
        if (!directory.exists()) {
            System.out.println("DB LOG -> Creating uploaded-images/category directory!");
            if(!directory.mkdirs()){
                System.out.println("!!!!!!!! NÃO FOI POSSIVEL CRIAR A PASTA DAS CATEGORIAS !!!!!!!!!!");
            }
        }
    }
}
