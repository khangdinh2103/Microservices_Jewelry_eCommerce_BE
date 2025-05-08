package Service_Catalog.backend.services;

import Service_Catalog.backend.entities.Product;
import Service_Catalog.backend.entities.ProductSalesSummary;
import Service_Catalog.backend.repositories.ProductRepository;
import Service_Catalog.backend.repositories.ProductSalesSummaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductSalesSummaryRepository productSalesSummaryRepository;

    public Product addProduct(Product product) {
        product.setCreatedAt(Instant.now());
        product.setUpdatedAt(Instant.now());
        return productRepository.save(product);
    }

    public Product updateProduct(Product product) {
        product.setUpdatedAt(Instant.now());
        return productRepository.save(product);
    }

    public Product deleteProduct(Product product) {
        product.setStock(-1);
        product.setUpdatedAt(Instant.now());
        return productRepository.save(product);
    }

    public Product getProductById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getAllByIds(List<Integer> ids) {
        return productRepository.findAllById(ids);
    }

    public List<Product> getAllByCategoryId(Integer categoryId) {
        return productRepository.findAllByCategoryId(categoryId);
    }

    public List<String> getAllBrandByCategoryId(Integer categoryId) {
        return productRepository.getAllBrandByCategoryId(categoryId);
    }

    public List<String> getAllMaterialByCategoryId(Integer categoryId) {
        return productRepository.getAllMaterialByCategoryId(categoryId);
    }

    public List<String> getAllSizeByCategoryId(Integer categoryId) {
        return productRepository.getAllSizeByCategoryId(categoryId);
    }

    public List<String> getAllGoldKaratByCategoryId(Integer categoryId) {
        return productRepository.getAllGoldKaratByCategoryId(categoryId);
    }

    public List<String> getAllColorByCategoryId(Integer categoryId) {
        return productRepository.getAllColorByCategoryId(categoryId);
    }

    public List<Product> getAllByCollectionId(Integer collectionId) {
        return productRepository.findAllByCollectionId(collectionId);
    }

    public List<Product> getTop10BestSellingProducts() {
        Instant threeMonthsAgo = Instant.now().minus(3, ChronoUnit.MONTHS);
        return productSalesSummaryRepository.findTop10BestSellingProducts(threeMonthsAgo);
    }

    public List<Product> getSimilarProducts(Integer productId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return null;
        }
        return productRepository.findSimilarProducts(
                productId,
                product.getCollectionId() != null ? product.getCollectionId().getId() : null,
                product.getBrand(),
                product.getCategoryId() != null ? product.getCategoryId().getId() : null);
    }

    // In ProductService implementation
    public List<Product> getProductsBelowPrice(Double maxPrice) {
        return productRepository.findByPriceLessThanEqual(maxPrice);
    }

    public List<Product> getProductsBetweenPrices(Double minPrice, Double maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }

    public List<Product> getProductsAbovePrice(Double minPrice) {
        return productRepository.findByPriceGreaterThanEqual(minPrice);
    }

}
