package tocadaraposa.web.controller;

import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
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
import tocadaraposa.domain.dto.CategoryDTO;
import tocadaraposa.service.CategoryService;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin/category")
public class AdminCategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping()
    public String categoryManager(CategoryDTO categorydto, ModelMap map) throws RuntimeException{
        List<Category> categories = categoryService.buscarTodasCategorias();

        map.addAttribute("categories", categories);
        if(!map.containsAttribute("categorydto")) map.addAttribute("categorydto", categorydto);
        return "admin/categories";
    }

    @PostMapping("/salvar")
    public String saveCategory(@Valid CategoryDTO categorydto, BindingResult result,
                               RedirectAttributes attr) throws IOException, FileSizeLimitExceededException, RuntimeException {

        if(result.hasErrors() || categorydto.getImage().isEmpty()){
            categoryService.prepareSaveErrorResponse(categorydto, result, attr);
        }else{
            if(categoryService.saveCategory(categorydto, attr)){
                attr.addFlashAttribute("sucesso", "Categoria Salva!");
            }else{
                attr.addFlashAttribute("categorydto", categorydto);
            }
        }
        return "redirect:/admin/category";
    }

    @PostMapping("/editar")
    public String editCategory(@Valid CategoryDTO categorydto, BindingResult result,
                               RedirectAttributes attr) throws IOException, FileSizeLimitExceededException, RuntimeException {

        if(result.hasErrors() || (categorydto.getImage().isEmpty() && categorydto.getId() == null)){
            categoryService.prepareSaveErrorResponse(categorydto, result, attr);
        }else{
            if(categoryService.editCategory(categorydto, attr)){
                attr.addFlashAttribute("sucesso", "Categoria Atualizada!");
            }else{
                attr.addFlashAttribute("categorydto", categorydto);
            }
        }
        return "redirect:/admin/category";
    }

    @GetMapping("/excluir/{id}")
    public ResponseEntity<Boolean> excluirProduto(@PathVariable("id") Long id) throws RuntimeException{
        if(categoryService.hasChilds(id)) return ResponseEntity.ok(Boolean.valueOf(false));
        categoryService.deleteProductById(id);
        return ResponseEntity.ok(Boolean.valueOf(true));
    }

    @GetMapping("/editar/{id}")
    public String preEditarCategory(@PathVariable("id") Long id, RedirectAttributes attr) throws RuntimeException{
        Category c = categoryService.findById(id);
        CategoryDTO dto = new CategoryDTO(c);
        attr.addFlashAttribute("categorydto", dto);
        return "redirect:/admin/category";
    }

}
