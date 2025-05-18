package fit.iuh.backend.repository;

import fit.iuh.backend.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{
    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN p.category c " +
            "LEFT JOIN p.collection col " +
            "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR CAST(p.price AS string) LIKE CONCAT('%', :keyword, '%') " +
            "OR CAST(p.gender AS string) LIKE CONCAT('%', :keyword, '%') " +
            "OR LOWER(p.material) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR CAST(p.goldKarat AS string) LIKE CONCAT('%', :keyword, '%') " +
            "OR LOWER(p.color) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(col.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Product> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    Page<Product> findByStockLessThanEqual(int threshold,Pageable pageable);
    Page<Product> findByStockEquals(int stock, Pageable pageable);

}