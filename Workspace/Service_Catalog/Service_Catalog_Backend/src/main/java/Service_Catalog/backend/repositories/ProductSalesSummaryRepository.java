package Service_Catalog.backend.repositories;

import Service_Catalog.backend.entities.Product;
import Service_Catalog.backend.entities.ProductSalesSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface ProductSalesSummaryRepository extends JpaRepository<ProductSalesSummary, Integer> {

    @Query("SELECT pss.productId FROM ProductSalesSummary pss WHERE pss.orderDate >= :threeMonthsAgo GROUP BY pss.productId ORDER BY SUM(pss.quantitySold) DESC")
    List<Product> findTop10BestSellingProducts(@Param("threeMonthsAgo") Instant threeMonthsAgo);
}