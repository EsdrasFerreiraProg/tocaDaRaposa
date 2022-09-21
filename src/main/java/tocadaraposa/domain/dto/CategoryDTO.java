package tocadaraposa.domain.dto;

import org.springframework.web.multipart.MultipartFile;
import tocadaraposa.domain.Category;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class CategoryDTO {

    private Long id;
    @NotEmpty(message = "O titulo é necessário")
    private String title;
    @NotNull(message = "Seleciona uma imagem")
    private MultipartFile image;

    public CategoryDTO(){}

    public CategoryDTO(Category c){
        this.id = c.getId();
        this.title = c.getTitle();
        this.image = null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}
