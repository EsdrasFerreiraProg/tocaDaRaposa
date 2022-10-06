package tocadaraposa.service;

import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import tocadaraposa.ApplicationConfigTest;
import tocadaraposa.domain.Category;
import tocadaraposa.domain.dto.CategoryDTO;
import tocadaraposa.repository.CategoryRepository;
import tocadaraposa.service.util.FileUploadUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryServiceTest extends ApplicationConfigTest {

    @MockBean
    private CategoryRepository categoryRepository;

    @MockBean
    private FileUploadUtil fileUploadUtil;

    @Autowired
    private CategoryService categoryService;

    @Test
    public void shouldFindAllCategories(){
        List<Category> mockedList = new ArrayList<>();
        Mockito.when(categoryRepository.findAll()).thenReturn(mockedList);

        assertNotNull(categoryService.buscarTodasCategorias());

        Mockito.verify(categoryRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void shouldFindById(){
        final long testID = 1L;
        Optional<Category> c = Optional.of(new Category());

        Mockito.when(categoryRepository.findById(ArgumentMatchers.eq(testID))).thenReturn(c);

        assertNotNull(categoryService.findById(testID));
        Mockito.verify(categoryRepository, Mockito.times(1)).findById(ArgumentMatchers.eq(testID));
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowRuntimeExceptionWhenNotFindCategory(){
        final long testID = 1L;
        categoryService.findById(testID);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowRuntimeExceptionWhenNotFindCategoryToDelete(){
        final long testID = 1L;
        categoryService.deleteCategoryById(testID);
    }

    @Test
    public void shouldDeleteCategory(){
        final long testID = 1L;
        Optional<Category> c = Optional.of(new Category());
        c.get().setImagename("teste");
        Mockito.when(categoryRepository.findById(ArgumentMatchers.eq(testID))).thenReturn(c);

        Mockito.doNothing().when(fileUploadUtil).deleteFile(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doNothing().when(categoryRepository).deleteById(ArgumentMatchers.any(Long.class));

        categoryService.deleteCategoryById(testID);

        Mockito.verify(fileUploadUtil, Mockito.times(1)).deleteFile(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.verify(categoryRepository, Mockito.times(1)).deleteById(ArgumentMatchers.any(Long.class));
    }


    @Test
    public void shouldReturnFalseWhenCategoryNameAlreadyExists(){
        final String testName = "teste category";
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setTitle(testName);

        RedirectAttributesModelMap attr = Mockito.mock(RedirectAttributesModelMap.class);
        Mockito.when(attr.addFlashAttribute(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(attr);

        Optional<Category> mockedCategory = Optional.of(new Category());
        Mockito.when(categoryRepository.findByExactName(ArgumentMatchers.eq(testName))).thenReturn(mockedCategory);

        assertFalse(categoryService.saveCategory(categoryDTO, attr));
        Mockito.verify(categoryRepository, Mockito.times(1)).findByExactName(ArgumentMatchers.eq(testName));
    }

    @Test(expected = RuntimeException.class)
    public void shouldNotSaveImageWhenCannotEncryptImageName(){
        final String testName = "teste category";
        final long testID = 1L;

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setTitle(testName);
        categoryDTO.setImage(ArgumentMatchers.any(MultipartFile.class));

        RedirectAttributesModelMap attr = Mockito.mock(RedirectAttributesModelMap.class);
        Mockito.when(attr.addFlashAttribute(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(attr);

        Optional<Category> mockedCategory = Optional.empty();
        Mockito.when(categoryRepository.findByExactName(ArgumentMatchers.eq(testName))).thenReturn(mockedCategory);

        Category c = new Category();
        c.setTitle(testName);
        c.setId(testID);
        Mockito.when(categoryRepository.save(ArgumentMatchers.any(Category.class))).thenReturn(c);
        categoryService.setEncryptImagesMessageDigestType("error");
        categoryService.saveCategory(categoryDTO, attr);
    }

    @Test
    public void shouldSaveCategory(){
        final String testName = "teste category";
        final long testID = 1L;

        CategoryDTO categoryDTO = Mockito.mock(CategoryDTO.class);
        Mockito.when(categoryDTO.getTitle()).thenReturn(testName);
        Mockito.when(categoryDTO.getImage()).thenReturn(Mockito.mock(MultipartFile.class));

        RedirectAttributesModelMap attr = Mockito.mock(RedirectAttributesModelMap.class);
        Mockito.when(attr.addFlashAttribute(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(attr);

        Optional<Category> mockedCategory = Optional.empty();
        Mockito.when(categoryRepository.findByExactName(ArgumentMatchers.eq(testName))).thenReturn(mockedCategory);

        Category c = new Category();
        c.setTitle(testName);
        c.setId(testID);

        Mockito.when(categoryRepository.save(ArgumentMatchers.any(Category.class))).thenReturn(c);
        Mockito.doNothing().when(fileUploadUtil)
                .saveFile(  ArgumentMatchers.anyString(),
                            ArgumentMatchers.any(MultipartFile.class),
                            ArgumentMatchers.anyString());

        assertTrue(categoryService.saveCategory(categoryDTO, attr));
        Mockito.verify(categoryRepository, Mockito.times(1)).findByExactName(ArgumentMatchers.anyString());
        Mockito.verify(categoryRepository, Mockito.times(2)).save(ArgumentMatchers.any(Category.class));
        Mockito.verify(fileUploadUtil, Mockito.times(1))
                .saveFile(  ArgumentMatchers.anyString(),
                            ArgumentMatchers.any(MultipartFile.class),
                            ArgumentMatchers.anyString());
    }

    @Test
    public void shouldReturnFalseWhenCategoryDontExistByExactName(){
        final String testName = "teste";
        Mockito.when(categoryRepository.findByExactName(ArgumentMatchers.eq(testName))).thenReturn(Optional.empty());
        assertFalse(categoryService.existByName(testName));
        Mockito.verify(categoryRepository, Mockito.times(1)).findByExactName(ArgumentMatchers.eq(testName));
    }

    @Test
    public void shouldReturnTrueWhenCategoryExistsByExactName(){
        final String testName = "teste";
        Mockito.when(categoryRepository.findByExactName(ArgumentMatchers.eq(testName))).thenReturn(Optional.of(new Category()));
        assertTrue(categoryService.existByName(testName));
        Mockito.verify(categoryRepository, Mockito.times(1)).findByExactName(ArgumentMatchers.eq(testName));
    }

}
