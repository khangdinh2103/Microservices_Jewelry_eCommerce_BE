package Service_Catalog.backend.services;

import Service_Catalog.backend.entities.Product;
import Service_Catalog.backend.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

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
}
