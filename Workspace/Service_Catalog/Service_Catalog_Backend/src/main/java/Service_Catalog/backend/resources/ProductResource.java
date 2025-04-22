package Service_Catalog.backend.resources;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Service_Catalog.backend.dto.ProductDto;
import Service_Catalog.backend.dto.ProductImageDto;
import Service_Catalog.backend.entities.Product;
import Service_Catalog.backend.entities.ProductImage;
import Service_Catalog.backend.services.CategoryService;
import Service_Catalog.backend.services.CollectionService;
import Service_Catalog.backend.services.ProductImageService;
import Service_Catalog.backend.services.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductResource {
    @Autowired
    private ProductService productService;

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductImageService productImageService;

    @GetMapping("")
    public List<ProductDto> showProductList() {
        List<Product> products = productService.getAllProducts();
        return products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ProductDto showProductDetail(@PathVariable Integer id) {
        return convertToDto(productService.getProductById(id));
    }

    @GetMapping("/category/{categoryId}")
    public List<ProductDto> showProductListByCategory(@PathVariable Integer categoryId) {
        List<Product> products = productService.getAllByCategoryId(categoryId);
        return products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/category/{categoryId}/brands")
    public List<String> showBrandListByCategory(@PathVariable Integer categoryId) {
        return productService.getAllBrandByCategoryId(categoryId);
    }

    @GetMapping("/category/{categoryId}/materials")
    public List<String> showMaterialListByCategory(@PathVariable Integer categoryId) {
        return productService.getAllMaterialByCategoryId(categoryId);
    }

    @GetMapping("/category/{categoryId}/sizes")
    public List<String> showSizeListByCategory(@PathVariable Integer categoryId) {
        return productService.getAllSizeByCategoryId(categoryId);
    }

    @GetMapping("/category/{categoryId}/goldkarats")
    public List<String> showGoldKaratListByCategory(@PathVariable Integer categoryId) {
        return productService.getAllGoldKaratByCategoryId(categoryId);
    }

    @GetMapping("/category/{categoryId}/colors")
    public List<String> showColorListByCategory(@PathVariable Integer categoryId) {
        return productService.getAllColorByCategoryId(categoryId);
    }

    @GetMapping("/collection/{collectionId}")
    public List<ProductDto> showProductListByCollection(@PathVariable Integer collectionId) {
        List<Product> products = productService.getAllByCollectionId(collectionId);
        return products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/bestselling")
    public List<ProductDto> showBestSellingProduct() {
        List<Product> products = productService.getTop10BestSellingProducts();
        return products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}/similar")
    public List<ProductDto> showSimilarProduct(@PathVariable Integer id) {
        List<Product> products = productService.getSimilarProducts(id);
        return products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Các phương thức CRUD khác

    // Conversion methods
    private ProductDto convertToDto(Product product) {
        if (product == null)
            return null;

        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setCode(product.getCode());
        dto.setDescription(product.getDescription());
        dto.setQuantity(product.getQuantity());
        dto.setPrice(product.getPrice());
        dto.setStatus(product.getStatus());
        dto.setGender(product.getGender());
        dto.setMaterial(product.getMaterial());
        dto.setGoldKarat(product.getGoldKarat());
        dto.setColor(product.getColor());
        dto.setBrand(product.getBrand());
        dto.setSize(product.getSize());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());

        // Set category info
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        }

        // Set collection info
        if (product.getCollection() != null) {
            dto.setCollectionId(product.getCollection().getId());
            dto.setCollectionName(product.getCollection().getName());
        }

        // Set images
        if (product.getProductImages() != null) {
            dto.setProductImages(product.getProductImages().stream()
                    .map(this::convertToImageDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private ProductImageDto convertToImageDto(ProductImage image) {
        if (image == null)
            return null;

        ProductImageDto dto = new ProductImageDto();
        dto.setId(image.getId());
        dto.setImageUrl(image.getImageUrl());
        dto.setIsPrimary(image.getIsPrimary());
        dto.setSortOrder(image.getSortOrder());
        return dto;
    }
}
