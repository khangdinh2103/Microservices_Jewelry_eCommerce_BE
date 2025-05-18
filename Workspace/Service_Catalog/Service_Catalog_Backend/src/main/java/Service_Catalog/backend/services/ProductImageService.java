package Service_Catalog.backend.services;

import Service_Catalog.backend.entities.ProductImage;
import Service_Catalog.backend.repositories.ProductImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductImageService {
    @Autowired
    private ProductImageRepository productimageRepository;

    public void addProductimage(ProductImage productimage) {
        productimageRepository.save(productimage);
    }

    public void updateProductimage(ProductImage productimage) {
        productimageRepository.save(productimage);
    }

    public void deleteProductimage(ProductImage productimage) {
        productimageRepository.delete(productimage);
    }

    public ProductImage getProductimageById(Integer id) {
        return productimageRepository.findById(id).orElse(null);
    }

    public Iterable<ProductImage> getAllProductimages() {
        return productimageRepository.findAll();
    }

    public List<ProductImage> getAllById(List<Integer> ids) {
        return productimageRepository.findAllById(ids);
    }
}
