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
public class ProductRestController {

    @Autowired
    private ProductService productService;

    @GetMapping("/all")
    public List<Product> getAllProducts(){
        return productService.getAllProducts();
    }

    @GetMapping("/bycategory/{category}")
    public List<Product> getProductsByTitle(@PathVariable("category") String category){
        return productService.getProductsByCategory(category);
    }

    @GetMapping("/byid/{id}")
    public Product getProductById(@PathVariable("id") Long id){
        return productService.findById(id);
    }

    @GetMapping("/image/{image}")
    public byte[] getImage(@PathVariable("image") String image) throws IOException {
        if(image != null || image.trim().length() > 0){
            return Files.readAllBytes(FileUploadUtil.returnFilePath(image, "product"));
        }else{
            return null;
        }
    }
}
