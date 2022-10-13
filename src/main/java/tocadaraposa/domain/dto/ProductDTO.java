package tocadaraposa.domain.dto;

import org.springframework.format.annotation.NumberFormat;
import org.springframework.web.multipart.MultipartFile;
import tocadaraposa.domain.Category;
import tocadaraposa.domain.Product;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@SuppressWarnings("unused")
public class ProductDTO {

    private Long id;
    @NotEmpty(message = "O nome não pode estar vazio")
    private String name;
    @NotNull(message = "Escolha uma categoria!")
    private Category category;
    @NotNull(message = "Digite um preço")
    @NumberFormat(style = NumberFormat.Style.CURRENCY, pattern = "#,##0.00")
    private BigDecimal price;
    private boolean active;
    @NotNull(message = "Seleciona uma imagem")
    private MultipartFile image;

    public ProductDTO(){}

    public ProductDTO(Product p) {
        id = p.getId();
        name = p.getName();
        category = p.getCategory();
        price = p.getPrice();
        active = p.isActive();
        image = null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "ProductDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category=" + category +
                ", price=" + price +
                ", active=" + active;
    }
}
