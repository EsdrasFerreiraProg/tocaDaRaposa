package tocadaraposa.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tocadaraposa.domain.Product;
import tocadaraposa.service.ProductService;
import tocadaraposa.service.util.FileUploadUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@SuppressWarnings("unused")
public class ProductRestController {

    @Autowired
    private ProductService productService;

    @Autowired
    private FileUploadUtil fileUploadUtil;

    @GetMapping("/all")
    public List<Product> getAllProducts() throws RuntimeException{
        return productService.getAllProducts();
    }

    @GetMapping("/bycategory/{category}")
    public List<Product> getProductsByTitle(@PathVariable("category") String category) throws RuntimeException{
        return productService.getProductsByCategory(category);
    }

    @GetMapping("/byid/{id}")
    public Product getProductById(@PathVariable("id") Long id) throws RuntimeException{
        return productService.findById(id);
    }

    @GetMapping("/image/{image}")
    public byte[] getImage(@PathVariable("image") String image) throws IOException, NoSuchFieldException {
        if(image != null && image.trim().length() > 0){
            return Files.readAllBytes(fileUploadUtil.returnFilePath(image, "product"));
        }else{
            return null;
        }
    }
}
