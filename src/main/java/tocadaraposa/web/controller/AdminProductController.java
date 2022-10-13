package tocadaraposa.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tocadaraposa.domain.Category;
import tocadaraposa.domain.Product;
import tocadaraposa.domain.dto.ProductDTO;
import tocadaraposa.service.CategoryService;
import tocadaraposa.service.ProductService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin/product")
@SuppressWarnings("unused")
public class AdminProductController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;

    @GetMapping()
    public String productManager(ProductDTO productdto, ModelMap map){
        List<Category> categories = categoryService.buscarTodasCategorias();
        List<Product> products = productService.getAllProducts();

        map.addAttribute("categories", categories);
        map.addAttribute("products", products);
        if(!map.containsAttribute("productdto")) map.addAttribute("productdto", productdto);

        return "admin/products";
    }

    @PostMapping("/salvar")
    public String saveProduct(@Valid ProductDTO productdto, BindingResult result,
                              RedirectAttributes attr) {
        if(result.hasErrors() || productdto.getImage().isEmpty()){
            productService.prepareSaveErrorResponse(productdto, result, attr);
        }else{
            if(productService.saveProduct(productdto, attr)){
                attr.addFlashAttribute("sucesso", "Produto Inserido!");
            }else{
                attr.addFlashAttribute("productdto", productdto);
            }
        }
        return "redirect:/admin/product";
    }

    @PostMapping("/editar")
    public String editProduct(@Valid ProductDTO productdto, BindingResult result,
                              RedirectAttributes attr) {
        if(result.hasErrors() || (productdto.getImage().isEmpty() && productdto.getId() == null)){
            productService.prepareSaveErrorResponse(productdto, result, attr);
        }else{
            if(productService.updateProduct(productdto, attr)){
                attr.addFlashAttribute("sucesso", "Produto atualizado!");
            }else{
                attr.addFlashAttribute("productdto", productdto);
            }
        }
        return "redirect:/admin/product";
    }

    @GetMapping("/excluir/{id}")
    public ResponseEntity<Boolean> excluirProduto(@PathVariable("id") Long id){
        productService.deleteProductById(id);
        return ResponseEntity.ok(true);
    }

    @GetMapping("/editar/{id}")
    public String preEditarProduto(@PathVariable("id") Long id, RedirectAttributes attr){
        Product p = productService.findById(id);
        ProductDTO dto = new ProductDTO(p);
        attr.addFlashAttribute("productdto", dto);
        return "redirect:/admin/product";
    }

}
