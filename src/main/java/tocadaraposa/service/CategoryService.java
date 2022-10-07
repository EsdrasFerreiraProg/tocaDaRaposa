package tocadaraposa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tocadaraposa.domain.Category;
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

    @Autowired
    private FileUploadUtil fileUploadUtil;

    private String encryptImagesMessageDigestType = "MD5";

    public String getEncryptImagesMessageDigestType() {
        return encryptImagesMessageDigestType;
    }

    public void setEncryptImagesMessageDigestType(String encryptImagesMessageDigestType) {
        this.encryptImagesMessageDigestType = encryptImagesMessageDigestType;
    }

    @Transactional(readOnly = true)
    public List<Category> buscarTodasCategorias(){
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Category findById(Long id) throws RuntimeException{
        return categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Erro ao procurar Categoria"));
    }

    @Transactional(readOnly = false)
    public void deleteCategoryById(Long id) throws RuntimeException{
        Category c = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Produto não encontrado!"));
        fileUploadUtil.deleteFile(c.getImagename(), "category");
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
    public boolean saveCategory(CategoryDTO categorydto, RedirectAttributes attr) throws RuntimeException{

        if(categoryRepository.findByExactName(categorydto.getTitle()).isPresent()){
            attr.addFlashAttribute("falha", "Esse nome de categoria ja existe!");
            return false;
        }

        Category c = new Category();
        c.setTitle(categorydto.getTitle());
        c.setImagename("dump");

        //Save to generate ID
        c = categoryRepository.save(c);

        String imagename = "category-" + String.valueOf(c.getId());
        imagename = saveImage(c, imagename);

        fileUploadUtil.saveFile(imagename + ".png", categorydto.getImage(), "category");
        c.setImagename(imagename + ".png");

        //Save again to update image path
        categoryRepository.save(c);
        return true;
    }

    private String saveImage(Category c, String imagename){
        try {
            MessageDigest m = MessageDigest.getInstance(encryptImagesMessageDigestType);
            m.update(imagename.getBytes(),0,imagename.length());
            imagename = new BigInteger(1,m.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao salvar a imagem no servidor!");
        }
        return imagename;
    }

    @Transactional(readOnly = true)
    public boolean existByName(String name){
        return categoryRepository.findByExactName(name).isPresent();
    }

    @Transactional(readOnly = true)
    public boolean uniqueNameDiferentOfOriginal(String name, Long id) throws RuntimeException{
        Category c = (Category) categoryRepository.findByExactName(name).orElse(null);
        if(c == null) return true;
        Category cid = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Id de categoria não encontrado"));
        return c.getTitle().equals(cid.getTitle());
    }

    @Transactional(readOnly = false)
    public boolean editCategory(CategoryDTO categorydto, RedirectAttributes attr) throws RuntimeException{

        if(!uniqueNameDiferentOfOriginal(categorydto.getTitle(), categorydto.getId())){
            attr.addFlashAttribute("falha", "Esse nome de categoria ja existe!");
            return false;
        }

        Category c = categoryRepository.findById(categorydto.getId()).orElseThrow(
                () -> new RuntimeException("Categoria não encontrada")
        );
        c.setTitle(categorydto.getTitle());

        if(categorydto.getImage() != null && !categorydto.getImage().isEmpty()) {
            String imagename = "category-" + String.valueOf(c.getId());
            imagename = saveImage(c, imagename);

            fileUploadUtil.saveFile(imagename + ".png", categorydto.getImage(), "category");
            c.setImagename(imagename + ".png");
        }

        categoryRepository.save(c);
        return true;
    }

    @Transactional(readOnly = true)
    public boolean hasChilds(Long id) {
        return !categoryRepository.findFirstHasProducts(id, PageRequest.of(0,1)).isEmpty();
    }
}
