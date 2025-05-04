package Service_Catalog.backend.services;

import Service_Catalog.backend.entities.Productimage;
import Service_Catalog.backend.repositories.ProductimageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductimageService {
    @Autowired
    private ProductimageRepository productimageRepository;

    public void addProductimage(Productimage productimage) {
        productimageRepository.save(productimage);
    }

    public void updateProductimage(Productimage productimage) {
        productimageRepository.save(productimage);
    }

    public void deleteProductimage(Productimage productimage) {
        productimageRepository.delete(productimage);
    }

    public Productimage getProductimageById(Integer id) {
        return productimageRepository.findById(id).orElse(null);
    }

    public Iterable<Productimage> getAllProductimages() {
        return productimageRepository.findAll();
    }

    public List<Productimage> getAllById(List<Integer> ids) {
        return productimageRepository.findAllById(ids);
    }
}
