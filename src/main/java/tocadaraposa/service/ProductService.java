package tocadaraposa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tocadaraposa.domain.Product;
import tocadaraposa.domain.dto.ProductDTO;
import tocadaraposa.repository.ProductRepository;
import tocadaraposa.service.util.FileUploadUtil;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FileUploadUtil fileUploadUtil;

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(String title) {
        return productRepository.findByCategoryTitle(title);
    }

    @Transactional(readOnly = false)
    public boolean saveProduct(ProductDTO productDTO, RedirectAttributes attr) throws RuntimeException{

        if(existByName(productDTO.getName())){
            attr.addFlashAttribute("falha", "Esse nome de produto ja existe!");
            return false;
        }

        Product p = new Product();

        p.setActive(productDTO.isActive());
        p.setCategory(productDTO.getCategory());
        p.setImagename("dump");
        p.setName(productDTO.getName());
        p.setPrice(productDTO.getPrice());

        p = productRepository.save(p);
        String imagename = "product-" + String.valueOf(p.getId());
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(imagename.getBytes(),0,imagename.length());
            imagename = new BigInteger(1,m.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao salvar a imagem no servidor!");
        }
        fileUploadUtil.saveFile(imagename + ".png", productDTO.getImage(), "product");
        p.setImagename(imagename + ".png");
        productRepository.save(p);

        return true;

    }

    @Transactional(readOnly = true)
    public boolean existByName(String name){
        return productRepository.findByExactName(name).isPresent();
    }

    @Transactional(readOnly = true)
    public boolean uniqueNameDiferentOfOriginal(String name, Long id) throws RuntimeException{
        Product p = (Product) productRepository.findByExactName(name).orElse( null);
        if(p == null) return true;
        Product pid = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Id de produto não encontrado"));
        return p.getName().equals(pid.getName());
    }


    public void prepareSaveErrorResponse(ProductDTO productdto, BindingResult result, RedirectAttributes attr) {
        result.getFieldErrors().forEach(e -> attr.addFlashAttribute(e.getField(), e.getDefaultMessage()));
        if((productdto.getImage().isEmpty() && productdto.getId() == null))
            attr.addFlashAttribute("image", "É necessário uma imagem!");
        attr.addFlashAttribute("productdto", productdto);
        attr.addFlashAttribute("falha", "Erro ao inserir produto!");
    }

    @Transactional(readOnly = false)
    public void deleteProductById(Long id) throws RuntimeException{
        Product p = productRepository.findById(id).orElseThrow(() ->
            new RuntimeException("Produto não encontrado!")
        );
        fileUploadUtil.deleteFile(p.getImagename(), "product");
        productRepository.delete(p);
    }

    @Transactional(readOnly = true)
    public Product findById(Long id) throws RuntimeException{
        return productRepository.findById(id).orElseThrow(() ->
            new RuntimeException("Produto não encontrado!")
        );
    }

    @Transactional(readOnly = false)
    public boolean updateProduct(ProductDTO productDTO, RedirectAttributes attr) throws RuntimeException{

        if(!uniqueNameDiferentOfOriginal(productDTO.getName(), productDTO.getId())){
            attr.addFlashAttribute("falha", "Esse nome de produto ja existe!");
            return false;
        }

        Product p = productRepository.findById(productDTO.getId()).orElseThrow(() -> new RuntimeException("Produto não encontrado!"));

        p.setActive(productDTO.isActive());
        p.setCategory(productDTO.getCategory());
        p.setName(productDTO.getName());
        p.setPrice(productDTO.getPrice());

        if(productDTO.getImage() != null && !productDTO.getImage().isEmpty()){
            String imagename = "product-" + String.valueOf(p.getId());
            try {
                MessageDigest m = MessageDigest.getInstance("MD5");
                m.update(imagename.getBytes(),0,imagename.length());
                imagename = new BigInteger(1,m.digest()).toString(16);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Erro ao salvar a imagem no servidor!");
            }
            fileUploadUtil.saveFile(imagename + ".png", productDTO.getImage(), "product");
            p.setImagename(imagename + ".png");
        }
        productRepository.save(p);
        return true;
    }
}
