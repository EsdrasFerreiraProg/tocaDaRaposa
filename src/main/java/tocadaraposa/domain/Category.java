package tocadaraposa.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "categories")
@SuppressWarnings("unused")
public class Category extends AbstractEntity{

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "imagename", nullable = false)
    private String imagename;

    @JsonIgnore
    @OneToMany(mappedBy = "category")
    private List<Product> products;

    public Category() {
        super();
    }

    public Category(Long id){
        super.setId(id);
    }

    public Category(Long id, String title) {
        super.setId(id);
        this.title = title;
    }

    public String getImagename() {
        return imagename;
    }

    public void setImagename(String imagename) {
        this.imagename = imagename;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Category{" +
                "title='" + title + '\'' +
                ", id=" + getId() +
                '}';
    }
}
