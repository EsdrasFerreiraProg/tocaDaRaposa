package tocadaraposa.service;

import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import tocadaraposa.config.ApplicationConfigTest;
import tocadaraposa.domain.Category;
import tocadaraposa.domain.Product;
import tocadaraposa.domain.dto.CategoryDTO;
import tocadaraposa.domain.dto.ProductDTO;
import tocadaraposa.repository.ProductRepository;
import tocadaraposa.service.util.FileUploadUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProductServiceTest extends ApplicationConfigTest {

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private FileUploadUtil fileUploadUtil;

    @Autowired
    private ProductService productService;

    private void resetEncryptImagePathArgument(){
        productService.setEncryptImagesMessageDigestType("MD5");
    }

    @Test
    public void shouldFindById(){
        final long testID = 1L;
        Optional<Product> c = Optional.of(new Product());

        Mockito.when(productRepository.findById(ArgumentMatchers.eq(testID))).thenReturn(c);

        assertNotNull(productService.findById(testID));
        Mockito.verify(productRepository, Mockito.times(1)).findById(ArgumentMatchers.eq(testID));
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowRuntimeExceptionWhenNotFindProduct(){
        final long testID = 1L;
        productService.findById(testID);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowRuntimeExceptionWhenNotFindProductToDelete(){
        final long testID = 1L;
        productService.deleteProductById(testID);
    }

    @Test
    public void shouldDeleteProduct(){
        final long testID = 1L;
        Optional<Product> c = Optional.of(new Product());
        c.get().setImagename("teste");
        Mockito.when(productRepository.findById(ArgumentMatchers.eq(testID))).thenReturn(c);

        Mockito.doNothing().when(fileUploadUtil).deleteFile(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doNothing().when(productRepository).delete(ArgumentMatchers.any(Product.class));

        productService.deleteProductById(testID);

        Mockito.verify(fileUploadUtil, Mockito.times(1))
                .deleteFile(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.verify(productRepository, Mockito.times(1))
                .delete(ArgumentMatchers.any(Product.class));
    }

    @Test
    public void shouldReturnAllProducts(){
        List<Product> mockedList = new ArrayList<>();
        Mockito.when(productRepository.findAll()).thenReturn(mockedList);
        assertNotNull(productService.getAllProducts());
        Mockito.verify(productRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void shouldReturnAllProductsByCategory(){
        List<Product> mockedList = new ArrayList<>();
        Category testCategory = new Category();
        testCategory.setTitle("test");

        Mockito.when(productRepository.findByCategoryTitle(
                    ArgumentMatchers.eq(testCategory.getTitle())))
                .thenReturn(mockedList);

        assertNotNull(productService.getProductsByCategory(testCategory.getTitle()));

        Mockito.verify(productRepository, Mockito.times(1))
                .findByCategoryTitle(ArgumentMatchers.eq(testCategory.getTitle()));
    }

    @Test
    public void shouldTestIfIsAUniqueName(){
        final String mockedTitle = "title";
        final long mockedID = 1L;

        Product c1 = new Product();
        c1.setName(mockedTitle);
        Product c2= new Product();
        c2.setId(mockedID);
        c2.setName("other title");

        //Quando um objeto diferente tem ja possui esse titulo
        Mockito.when(productRepository.findByExactName(ArgumentMatchers.eq(mockedTitle)))
                .thenReturn(Optional.of(c1));
        Mockito.when(productRepository.findById(ArgumentMatchers.eq(mockedID)))
                .thenReturn(Optional.of(c2));
        assertFalse(productService.uniqueNameDiferentOfOriginal(mockedTitle, mockedID));

        //Quando não existe nenhum objeto com esse nome
        Mockito.when(productRepository.findByExactName(ArgumentMatchers.eq(mockedTitle)))
                .thenReturn(Optional.empty());
        assertTrue(productService.uniqueNameDiferentOfOriginal(mockedTitle, mockedID));

        //Quando existe algum objeto com o titulo mas é o mesmo que está sendo testado
        Mockito.when(productRepository.findByExactName(ArgumentMatchers.eq(mockedTitle)))
                .thenReturn(Optional.of(c1));
        Mockito.when(productRepository.findById(ArgumentMatchers.eq(mockedID)))
                .thenReturn(Optional.of(c1));
        assertTrue(productService.uniqueNameDiferentOfOriginal(mockedTitle, mockedID));

        Mockito.verify(productRepository, Mockito.times(2)).findById(ArgumentMatchers.eq(mockedID));
        Mockito.verify(productRepository, Mockito.times(3)).findByExactName(ArgumentMatchers.eq(mockedTitle));
    }

    @Test
    public void shouldReturnFalseWhenProductDontExistByExactName(){
        final String testName = "teste";
        Mockito.when(productRepository.findByExactName(ArgumentMatchers.eq(testName))).thenReturn(Optional.empty());
        assertFalse(productService.existByName(testName));
        Mockito.verify(productRepository, Mockito.times(1)).findByExactName(ArgumentMatchers.eq(testName));
    }

    @Test
    public void shouldReturnTrueWhenProductExistsByExactName(){
        final String testName = "teste";
        Mockito.when(productRepository.findByExactName(ArgumentMatchers.eq(testName))).thenReturn(Optional.of(new Product()));
        assertTrue(productService.existByName(testName));
        Mockito.verify(productRepository, Mockito.times(1)).findByExactName(ArgumentMatchers.eq(testName));
    }

    @Test
    public void shouldNotSaveWhenProductNameAlreadyExists(){
        final String testName = "teste category";
        ProductDTO categoryDTO = new ProductDTO();
        categoryDTO.setName(testName);

        RedirectAttributesModelMap attr = Mockito.mock(RedirectAttributesModelMap.class);
        Mockito.when(attr.addFlashAttribute(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(attr);

        Optional<Product> mockedProduct = Optional.of(new Product());
        Mockito.when(productRepository.findByExactName(ArgumentMatchers.eq(testName))).thenReturn(mockedProduct);

        assertFalse(productService.saveProduct(categoryDTO, attr));
        Mockito.verify(productRepository, Mockito.times(1))
                .findByExactName(ArgumentMatchers.eq(testName));
    }

    @Test(expected = RuntimeException.class)
    public void shouldNotSaveImageWhenCannotEncryptImageName(){
        final String testName = "teste category";
        final long testID = 1L;

        ProductDTO productDTO = new ProductDTO();
        productDTO.setName(testName);
        productDTO.setImage(ArgumentMatchers.any(MultipartFile.class));

        RedirectAttributesModelMap attr = Mockito.mock(RedirectAttributesModelMap.class);
        Mockito.when(attr.addFlashAttribute(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(attr);

        Optional<Product> mockedProduct = Optional.empty();
        Mockito.when(productRepository.findByExactName(ArgumentMatchers.eq(testName))).thenReturn(mockedProduct);

        Product c = new Product();
        c.setName(testName);
        c.setId(testID);
        Mockito.when(productRepository.save(ArgumentMatchers.any(Product.class))).thenReturn(c);
        productService.setEncryptImagesMessageDigestType("error");
        productService.saveProduct(productDTO, attr);
    }

    @Test
    public void shouldSaveProduct(){
        final String testName = "teste category";
        final long testID = 1L;

        ProductDTO productDTO = Mockito.mock(ProductDTO.class);
        Mockito.when(productDTO.getName()).thenReturn(testName);
        Mockito.when(productDTO.getImage()).thenReturn(Mockito.mock(MultipartFile.class));

        RedirectAttributesModelMap attr = Mockito.mock(RedirectAttributesModelMap.class);
        Mockito.when(attr.addFlashAttribute(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(attr);

        Optional<Product> mockedProduct = Optional.empty();
        Mockito.when(productRepository.findByExactName(ArgumentMatchers.eq(testName))).thenReturn(mockedProduct);

        Product c = new Product();
        c.setName(testName);
        c.setId(testID);

        Mockito.when(productRepository.save(ArgumentMatchers.any(Product.class))).thenReturn(c);
        Mockito.doNothing().when(fileUploadUtil)
                .saveFile(  ArgumentMatchers.anyString(),
                        ArgumentMatchers.any(MultipartFile.class),
                        ArgumentMatchers.anyString());

        resetEncryptImagePathArgument();
        assertTrue(productService.saveProduct(productDTO, attr));

        Mockito.verify(productRepository, Mockito.times(1)).findByExactName(ArgumentMatchers.anyString());
        Mockito.verify(productRepository, Mockito.times(2)).save(ArgumentMatchers.any(Product.class));
        Mockito.verify(fileUploadUtil, Mockito.times(1))
                .saveFile(  ArgumentMatchers.anyString(),
                        ArgumentMatchers.any(MultipartFile.class),
                        ArgumentMatchers.anyString());
    }

    @Test
    public void shouldNotEditWhenProductNameAlreadyExists(){
        final String mockedTitle = "title";
        final long mockedID = 1L;

        Product c1 = new Product();
        c1.setName(mockedTitle);
        Product c2= new Product();
        c2.setId(mockedID);
        c2.setName("other title");

        Mockito.when(productRepository.findByExactName(ArgumentMatchers.eq(mockedTitle)))
                .thenReturn(Optional.of(c1));
        Mockito.when(productRepository.findById(ArgumentMatchers.eq(mockedID)))
                .thenReturn(Optional.of(c2));
        ProductDTO dto = new ProductDTO();
        dto.setName(mockedTitle);
        dto.setId(mockedID);
        RedirectAttributes attr = Mockito.mock(RedirectAttributes.class);
        Mockito.when(attr.addFlashAttribute(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(attr);
        assertFalse(productService.updateProduct(dto, attr));

        Mockito.verify(productRepository, Mockito.times(1)).findById(ArgumentMatchers.eq(mockedID));
        Mockito.verify(productRepository, Mockito.times(1)).findByExactName(ArgumentMatchers.eq(mockedTitle));

    }

    @Test(expected = RuntimeException.class)
    public void shouldNotEditWhenCantFindProduct(){
        final String mockedTitle = "title";
        final long mockedID = 1L;

        Mockito.when(productRepository.findByExactName(ArgumentMatchers.eq(mockedTitle)))
                .thenReturn(Optional.empty());

        ProductDTO dto = new ProductDTO();
        dto.setName(mockedTitle);
        dto.setId(mockedID);
        RedirectAttributes attr = Mockito.mock(RedirectAttributes.class);
        Mockito.when(attr.addFlashAttribute(ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString())).thenReturn(attr);
        productService.updateProduct(dto, attr);
    }

    @Test(expected = RuntimeException.class)
    public void shouldNotEditWhenCantSaveProductImage(){
        final String mockedTitle = "title";
        final long mockedID = 1L;

        Mockito.when(productRepository.findByExactName(ArgumentMatchers.eq(mockedTitle)))
                .thenReturn(Optional.empty());

        ProductDTO dto = new ProductDTO();
        dto.setName(mockedTitle);
        dto.setId(mockedID);

        MultipartFile mf = Mockito.mock(MultipartFile.class);
        dto.setImage(mf);
        Mockito.when(mf.isEmpty()).thenReturn(false);

        RedirectAttributes attr = Mockito.mock(RedirectAttributes.class);
        Mockito.when(attr.addFlashAttribute(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(attr);
        Mockito.when(productRepository.findById(ArgumentMatchers.eq(mockedID)))
                .thenReturn(Optional.of(new Product(mockedID)));

        productService.setEncryptImagesMessageDigestType("error");
        productService.updateProduct(dto, attr);
    }

    @Test
    public void shouldEditCategoryWhenTitleIsNotChanged(){
        final String mockedTitle = "title";
        final long mockedID = 1L;

        Product c1 = new Product();
        c1.setName(mockedTitle);
        c1.setId(mockedID);
        Mockito.when(productRepository.findByExactName(ArgumentMatchers.eq(mockedTitle)))
                .thenReturn(Optional.of(c1));
        Mockito.when(productRepository.findById(ArgumentMatchers.eq(mockedID)))
                .thenReturn(Optional.of(c1));

        ProductDTO dto = new ProductDTO();
        dto.setName(mockedTitle);
        dto.setId(mockedID);

        MultipartFile mf = Mockito.mock(MultipartFile.class);
        dto.setImage(mf);
        Mockito.when(mf.isEmpty()).thenReturn(false);

        RedirectAttributes attr = Mockito.mock(RedirectAttributes.class);
        Mockito.when(attr.addFlashAttribute(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(attr);

        Mockito.when(productRepository.save(ArgumentMatchers.any(Product.class))).thenReturn(c1);
        Mockito.doNothing().when(fileUploadUtil)
                .saveFile(  ArgumentMatchers.anyString(),
                        ArgumentMatchers.any(MultipartFile.class),
                        ArgumentMatchers.anyString());

        resetEncryptImagePathArgument();
        assertTrue(productService.updateProduct(dto, attr));

        Mockito.verify(productRepository, Mockito.times(1))
                .save(ArgumentMatchers.any(Product.class));
        Mockito.verify(fileUploadUtil, Mockito.times(1))
                .saveFile(  ArgumentMatchers.anyString(),
                        ArgumentMatchers.any(MultipartFile.class),
                        ArgumentMatchers.anyString());
        Mockito.verify(productRepository, Mockito.times(2)).findById(ArgumentMatchers.eq(mockedID));
        Mockito.verify(productRepository, Mockito.times(1)).findByExactName(ArgumentMatchers.eq(mockedTitle));
    }

}
