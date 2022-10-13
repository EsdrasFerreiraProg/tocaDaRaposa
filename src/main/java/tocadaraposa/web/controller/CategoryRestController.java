package tocadaraposa.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tocadaraposa.domain.Category;
import tocadaraposa.service.CategoryService;
import tocadaraposa.service.util.FileUploadUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@SuppressWarnings("unused")
public class CategoryRestController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private FileUploadUtil fileUploadUtil;

    @GetMapping("/all")
    public List<Category> getAllCategory() throws RuntimeException{
        return categoryService.buscarTodasCategorias();
    }

    @GetMapping("/byid/{id}")
    public Category getCategoryById(@PathVariable("id") Long id) throws RuntimeException{
        return categoryService.findById(id);
    }

    @GetMapping("/image/{image}")
    public byte[] getImage(@PathVariable("image") String image) throws IOException, NoSuchFieldException {
        if(image != null && image.trim().length() > 0){
            return Files.readAllBytes(fileUploadUtil.returnFilePath(image, "category"));
        }else{
            return null;
        }
    }

}
