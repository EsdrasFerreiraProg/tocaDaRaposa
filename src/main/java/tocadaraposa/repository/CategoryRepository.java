package tocadaraposa.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tocadaraposa.domain.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE c.title = :name")
    Optional<Category> findByExactName(@Param("name") String name);

    @Query("SELECT c FROM Category c " +
            "INNER JOIN Product p " +
            "ON c.id = p.id WHERE c.id = :id")
    List<Category> findFirst(Long id, Pageable page);
}
