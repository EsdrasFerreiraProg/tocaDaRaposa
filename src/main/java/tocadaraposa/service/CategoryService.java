package tocadaraposa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tocadaraposa.domain.Category;
import tocadaraposa.domain.Product;
import tocadaraposa.domain.dto.CategoryDTO;
import tocadaraposa.repository.CategoryRepository;
import tocadaraposa.service.util.FileUploadUtil;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
public class CategoryService{

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Category> buscarTodasCategorias(){
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Category findById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> {throw new RuntimeException("Erro ao procurar Categoria");});
    }

    @Transactional(readOnly = false)
    public void deleteProductById(Long id) {
        Category c = categoryRepository.findById(id).orElseThrow(() -> {
            throw new RuntimeException("Produto não encontrado!");
        });
        FileUploadUtil.deleteFile(c.getImagename(), "category");
        categoryRepository.deleteById(id);
    }

    public void prepareSaveErrorResponse(CategoryDTO categorydto, BindingResult result, RedirectAttributes attr) {
        result.getFieldErrors().forEach(e -> attr.addFlashAttribute(e.getField(), e.getDefaultMessage()));
        if((categorydto.getImage().isEmpty() && categorydto.getId() == null))
            attr.addFlashAttribute("image", "É necessário uma imagem!");
        attr.addFlashAttribute("categorydto", categorydto);
        attr.addFlashAttribute("falha", "Erro ao inserir categoria!");
    }

    @Transactional(readOnly = false)
    public boolean saveCategory(CategoryDTO categorydto, RedirectAttributes attr) {

        if(existByName(categorydto.getTitle())){
            attr.addFlashAttribute("falha", "Esse nome de categoria ja existe!");
            return false;
        }

        Category c = new Category();

        c.setTitle(categorydto.getTitle());
        c.setImagename("dump");
        c = categoryRepository.save(c);

        String imagename = "category-" + String.valueOf(c.getId());
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(imagename.getBytes(),0,imagename.length());
            imagename = new BigInteger(1,m.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao salvar a imagem no servidor!");
        }
        FileUploadUtil.saveFile(imagename + ".png", categorydto.getImage(), "category");
        c.setImagename(imagename + ".png");
        categoryRepository.save(c);
        return true;
    }

    @Transactional(readOnly = true)
    public boolean existByName(String name){
        return categoryRepository.findByExactName(name).isPresent();
    }

    @Transactional(readOnly = true)
    public boolean uniqueNameDiferentOfOriginal(String name, Long id){
        Category c = (Category) categoryRepository.findByExactName(name).orElse(null);
        if(c == null) return true;
        Category cid = categoryRepository.findById(id).orElseThrow(() -> {throw new RuntimeException("Id de categoria não encontrado");});
        return c.getTitle().equals(cid.getTitle());
    }

    @Transactional(readOnly = false)
    public boolean editCategory(CategoryDTO categorydto, RedirectAttributes attr) {

        if(!uniqueNameDiferentOfOriginal(categorydto.getTitle(), categorydto.getId())){
            attr.addFlashAttribute("falha", "Esse nome de categoria ja existe!");
            return false;
        }

        Category c = new Category();

        c.setTitle(categorydto.getTitle());

        if(categorydto.getImage() != null && !categorydto.getImage().isEmpty()) {
            String imagename = "category-" + String.valueOf(c.getId());
            try {
                MessageDigest m = MessageDigest.getInstance("MD5");
                m.update(imagename.getBytes(), 0, imagename.length());
                imagename = new BigInteger(1, m.digest()).toString(16);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Erro ao salvar a imagem no servidor!");
            }
            FileUploadUtil.saveFile(imagename + ".png", categorydto.getImage(), "category");
            c.setImagename(imagename + ".png");
        }
        categoryRepository.save(c);
        return true;
    }

    @Transactional(readOnly = true)
    public boolean hasChilds(Long id) {
        return !categoryRepository.findFirst(id, PageRequest.of(0,1)).isEmpty();
    }
}
