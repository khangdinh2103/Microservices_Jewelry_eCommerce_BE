package Service_Catalog.backend.repositories;

import Service_Catalog.backend.entities.Product;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
        @Query("SELECT p FROM Product p WHERE p.category.id = ?1")
        List<Product> findAllByCategoryId(Integer categoryId);

        @Query("SELECT p.brand from Product p where p.category.id = ?1 group by p.brand")
        List<String> getAllBrandByCategoryId(Integer categoryId);

        @Query("SELECT p.material from Product p where p.category.id = ?1 group by p.material")
        List<String> getAllMaterialByCategoryId(Integer categoryId);

        @Query("SELECT p.size from Product p where p.category.id = ?1 group by p.size")
        List<String> getAllSizeByCategoryId(Integer categoryId);

        @Query("SELECT p.goldKarat from Product p where p.category.id = ?1 group by p.goldKarat")
        List<String> getAllGoldKaratByCategoryId(Integer categoryId);

        @Query("SELECT p.color from Product p where p.category.id = ?1 group by p.color")
        List<String> getAllColorByCategoryId(Integer categoryId);

        @Query("SELECT p FROM Product p WHERE p.collection.id = ?1")
        List<Product> findAllByCollectionId(Integer collectionId);

        @Query("SELECT p FROM Product p WHERE " +
                        "(p.collection.id = :collectionId OR " +
                        "(p.brand = :brand AND p.category.id = :categoryId) OR " +
                        "p.category.id = :categoryId) " +
                        "AND p.id <> :productId " +
                        "ORDER BY " +
                        "CASE WHEN p.collection.id = :collectionId THEN 1 ELSE " +
                        "CASE WHEN p.brand = :brand AND p.category.id = :categoryId THEN 2 ELSE 3 END END")
        List<Product> findSimilarProducts(@Param("productId") Integer productId,
                        @Param("collectionId") Integer collectionId,
                        @Param("brand") String brand,
                        @Param("categoryId") Integer categoryId,
                        Pageable pageable);
                        
        @Query("SELECT p FROM Product p ORDER BY p.createdAt DESC")                
        List<Product> getNewArrivalsProducts(Pageable pageable);

        List<Product> findByPriceLessThanEqual(Double maxPrice);

        List<Product> findByPriceBetween(Double minPrice, Double maxPrice);

        List<Product> findByPriceGreaterThanEqual(Double minPrice);
}