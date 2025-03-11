package Service_Catalog.backend.repositories;

import Service_Catalog.backend.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
   @Query("SELECT p FROM Product p WHERE p.categoryId.id = ?1")
    List<Product> findAllByCategoryId(Integer categoryId);

    @Query("SELECT p.brand from Product p where p.categoryId.id = ?1 group by p.brand")
   List<String> getAllBrandByCategoryId(Integer categoryId);

    @Query("SELECT p.material from Product p where p.categoryId.id = ?1 group by p.material")
    List<String> getAllMaterialByCategoryId(Integer categoryId);

    @Query("SELECT p.size from Product p where p.categoryId.id = ?1 group by p.size")
    List<String> getAllSizeByCategoryId(Integer categoryId);

    @Query("SELECT p.goldKarat from Product p where p.categoryId.id = ?1 group by p.goldKarat")
    List<String> getAllGoldKaratByCategoryId(Integer categoryId);

    @Query("SELECT p.color from Product p where p.categoryId.id = ?1 group by p.color")
    List<String> getAllColorByCategoryId(Integer categoryId);

    @Query("SELECT p FROM Product p WHERE p.collectionId.id = ?1")
    List<Product> findAllByCollectionId(Integer collectionId);
}