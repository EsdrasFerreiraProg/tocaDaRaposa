package tocadaraposa.service;

import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import tocadaraposa.config.ApplicationConfig;
import tocadaraposa.domain.Category;
import tocadaraposa.domain.dto.CategoryDTO;
import tocadaraposa.repository.CategoryRepository;
import tocadaraposa.service.util.FileUploadUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unused")
public class CategoryServiceTest extends ApplicationConfig {

    @MockBean
    private CategoryRepository categoryRepository;

    @MockBean
    private FileUploadUtil fileUploadUtil;

    @Autowired
    private CategoryService categoryService;

    private void resetEncryptImagePathArgument(){
        categoryService.setEncryptImagesMessageDigestType("MD5");
    }

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
    public void shouldNotSaveWhenCategoryNameAlreadyExists(){
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

        resetEncryptImagePathArgument();
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

    @Test
    public void shouldTestIfIsAUniqueName(){
        final String mockedTitle = "title";
        final long mockedID = 1L;

        Category c1 = new Category();
        c1.setTitle(mockedTitle);
        Category c2= new Category();
        c2.setId(mockedID);
        c2.setTitle("other title");

        //Quando um objeto diferente tem ja possui esse titulo
        Mockito.when(categoryRepository.findByExactName(ArgumentMatchers.eq(mockedTitle)))
                .thenReturn(Optional.of(c1));
        Mockito.when(categoryRepository.findById(ArgumentMatchers.eq(mockedID)))
                .thenReturn(Optional.of(c2));
        assertFalse(categoryService.uniqueNameDiferentOfOriginal(mockedTitle, mockedID));

        //Quando não existe nenhum objeto com esse nome
        Mockito.when(categoryRepository.findByExactName(ArgumentMatchers.eq(mockedTitle)))
                .thenReturn(Optional.empty());
        assertTrue(categoryService.uniqueNameDiferentOfOriginal(mockedTitle, mockedID));

        //Quando existe algum objeto com o titulo mas é o mesmo que está sendo testado
        Mockito.when(categoryRepository.findByExactName(ArgumentMatchers.eq(mockedTitle)))
                .thenReturn(Optional.of(c1));
        Mockito.when(categoryRepository.findById(ArgumentMatchers.eq(mockedID)))
                .thenReturn(Optional.of(c1));
        assertTrue(categoryService.uniqueNameDiferentOfOriginal(mockedTitle, mockedID));

        Mockito.verify(categoryRepository, Mockito.times(2)).findById(ArgumentMatchers.eq(mockedID));
        Mockito.verify(categoryRepository, Mockito.times(3)).findByExactName(ArgumentMatchers.eq(mockedTitle));
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowRuntimeExceptionOnInvalidIdInUniqueNameDifferentOfOriginal(){
        final String mockedTitle = "title";
        final long mockedID = 1L;

        Category c1 = new Category();
        c1.setTitle(mockedTitle);

        Mockito.when(categoryRepository.findByExactName(ArgumentMatchers.eq(mockedTitle))).thenReturn(Optional.of(c1));
        Mockito.when(categoryRepository.findById(ArgumentMatchers.eq(mockedID))).thenReturn(Optional.empty());

        categoryService.uniqueNameDiferentOfOriginal(mockedTitle, mockedID);
    }

    @Test
    public void shouldTestIfHasChilds(){
        final long mockedID = 1L;
        final PageRequest pageRequestTest = PageRequest.of(0,1);
        final List<Category> mockedList = new ArrayList<>();
        mockedList.add(new Category());

        Mockito.when(categoryRepository
                        .findFirstHasProducts(ArgumentMatchers.eq(mockedID),
                                            ArgumentMatchers.eq(pageRequestTest)))
                        .thenReturn(mockedList);

        assertTrue(categoryService.hasChilds(mockedID));

        Mockito.when(categoryRepository
                        .findFirstHasProducts(ArgumentMatchers.any(Long.class),
                                            ArgumentMatchers.any(PageRequest.class)))
                        .thenReturn(new ArrayList<>());

        assertFalse(categoryService.hasChilds(2L));

        Mockito.verify(categoryRepository, Mockito.times(2))
                .findFirstHasProducts(ArgumentMatchers.any(Long.class),
                                    ArgumentMatchers.any(PageRequest.class));
    }

    @Test
    public void shouldNotEditWhenCategoryNameAlreadyExists(){
        final String mockedTitle = "title";
        final long mockedID = 1L;

        Category c1 = new Category();
        c1.setTitle(mockedTitle);
        Category c2= new Category();
        c2.setId(mockedID);
        c2.setTitle("other title");

        Mockito.when(categoryRepository.findByExactName(ArgumentMatchers.eq(mockedTitle)))
                .thenReturn(Optional.of(c1));
        Mockito.when(categoryRepository.findById(ArgumentMatchers.eq(mockedID)))
                .thenReturn(Optional.of(c2));
        CategoryDTO dto = new CategoryDTO();
        dto.setTitle(mockedTitle);
        dto.setId(mockedID);
        RedirectAttributes attr = Mockito.mock(RedirectAttributes.class);
        Mockito.when(attr.addFlashAttribute(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(attr);
        assertFalse(categoryService.editCategory(dto, attr));

        Mockito.verify(categoryRepository, Mockito.times(1)).findById(ArgumentMatchers.eq(mockedID));
        Mockito.verify(categoryRepository, Mockito.times(1)).findByExactName(ArgumentMatchers.eq(mockedTitle));

    }

    @Test(expected = RuntimeException.class)
    public void shouldNotEditWhenCantFindCategory(){
        final String mockedTitle = "title";
        final long mockedID = 1L;

        Mockito.when(categoryRepository.findByExactName(ArgumentMatchers.eq(mockedTitle)))
                .thenReturn(Optional.empty());

        CategoryDTO dto = new CategoryDTO();
        dto.setTitle(mockedTitle);
        dto.setId(mockedID);

        RedirectAttributes attr = Mockito.mock(RedirectAttributes.class);
        Mockito.when(attr.addFlashAttribute(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(attr);
        assertFalse(categoryService.editCategory(dto, attr));
    }

    @Test(expected = RuntimeException.class)
    public void shouldNotEditWhenCantSaveCategoryImage(){
        final String mockedTitle = "title";
        final long mockedID = 1L;

        Mockito.when(categoryRepository.findByExactName(ArgumentMatchers.eq(mockedTitle)))
                .thenReturn(Optional.empty());

        CategoryDTO dto = new CategoryDTO();
        dto.setTitle(mockedTitle);
        dto.setId(mockedID);

        MultipartFile mf = Mockito.mock(MultipartFile.class);
        dto.setImage(mf);
        Mockito.when(mf.isEmpty()).thenReturn(false);

        RedirectAttributes attr = Mockito.mock(RedirectAttributes.class);
        Mockito.when(attr.addFlashAttribute(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(attr);
        Mockito.when(categoryRepository.findById(ArgumentMatchers.eq(mockedID)))
                .thenReturn(Optional.of(new Category(mockedID)));

        categoryService.setEncryptImagesMessageDigestType("error");
        categoryService.editCategory(dto, attr);
    }

    @Test
    public void shouldEditCategoryWhenTitleIsNotChanged(){
        final String mockedTitle = "title";
        final long mockedID = 1L;

        Category c1 = new Category();
        c1.setTitle(mockedTitle);
        c1.setId(mockedID);
        Mockito.when(categoryRepository.findByExactName(ArgumentMatchers.eq(mockedTitle)))
                .thenReturn(Optional.of(c1));
        Mockito.when(categoryRepository.findById(ArgumentMatchers.eq(mockedID)))
                .thenReturn(Optional.of(c1));

        CategoryDTO dto = new CategoryDTO();
        dto.setTitle(mockedTitle);
        dto.setId(mockedID);

        MultipartFile mf = Mockito.mock(MultipartFile.class);
        dto.setImage(mf);
        Mockito.when(mf.isEmpty()).thenReturn(false);

        RedirectAttributes attr = Mockito.mock(RedirectAttributes.class);
        Mockito.when(attr.addFlashAttribute(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(attr);

        Mockito.when(categoryRepository.save(ArgumentMatchers.any(Category.class))).thenReturn(c1);
        Mockito.doNothing().when(fileUploadUtil)
                .saveFile(  ArgumentMatchers.anyString(),
                        ArgumentMatchers.any(MultipartFile.class),
                        ArgumentMatchers.anyString());

        resetEncryptImagePathArgument();
        assertTrue(categoryService.editCategory(dto, attr));

        Mockito.verify(categoryRepository, Mockito.times(1)).save(ArgumentMatchers.any(Category.class));
        Mockito.verify(fileUploadUtil, Mockito.times(1))
                .saveFile(  ArgumentMatchers.anyString(),
                        ArgumentMatchers.any(MultipartFile.class),
                        ArgumentMatchers.anyString());
        Mockito.verify(categoryRepository, Mockito.times(2)).findById(ArgumentMatchers.eq(mockedID));
        Mockito.verify(categoryRepository, Mockito.times(1)).findByExactName(ArgumentMatchers.eq(mockedTitle));
    }

}
