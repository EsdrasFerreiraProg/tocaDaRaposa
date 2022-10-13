package tocadaraposa.domain;

import org.springframework.format.annotation.NumberFormat;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
@SuppressWarnings("unused")
public class Product extends AbstractEntity{

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @NumberFormat(style = NumberFormat.Style.CURRENCY, pattern = "#,##0.00")
    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "active")
    private boolean active;

    @Column(name = "imagepath", nullable = false)
    private String imagename;

    public Product(long id){
        super.setId(id);
    }

    public Product(){}

    public String getName() {
        return name;
    }

    public String getActiveMessage() {
        return active ? "Em estoque" : "Em falta";
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

    public String getImagename() {
        return imagename;
    }

    public void setImagename(String imagename) {
        this.imagename = imagename;
    }
}
