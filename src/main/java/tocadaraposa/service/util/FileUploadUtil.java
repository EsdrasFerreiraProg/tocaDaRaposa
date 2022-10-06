package tocadaraposa.service.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Component
public class FileUploadUtil {
    public void saveFile(String fileName, MultipartFile file, String folder){
        Path uploadDirectory = Paths.get("uploaded-images/" + folder);
        try(InputStream inputStream = file.getInputStream()){
            Path filePath = uploadDirectory.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar a imagem!");
        }
    }
    public void deleteFile(String fileName, String folder){
        Path uploadDirectory = Paths.get("uploaded-images/" + folder);
        Path filePath = uploadDirectory.resolve(fileName);
        try{
            Files.delete(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao deletar a imagem!");
        }
    }

    public Path returnFilePath(String fileName, String folder){
        Path uploadDirectory = Paths.get("uploaded-images/" + folder);
        Path filePath = uploadDirectory.resolve(fileName);
        return filePath;
    }

}
