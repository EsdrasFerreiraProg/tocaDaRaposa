package tocadaraposa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tocadaraposa.domain.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.category.title LIKE :title")
    List<Product> findByCategoryTitle(@Param("title") String title);

    @Query("SELECT p FROM Product p WHERE p.name = :name")
    Optional<Object> findByExactName(@Param("name") String name);
}
