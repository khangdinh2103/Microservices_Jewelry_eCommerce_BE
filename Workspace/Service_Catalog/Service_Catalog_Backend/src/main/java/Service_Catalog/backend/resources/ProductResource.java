package Service_Catalog.backend.resources;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Service_Catalog.backend.dto.ProductDto;
import Service_Catalog.backend.dto.ProductImageDto;
import Service_Catalog.backend.entities.Product;
import Service_Catalog.backend.entities.ProductImage;
import Service_Catalog.backend.services.CategoryService;
import Service_Catalog.backend.services.CollectionService;
import Service_Catalog.backend.services.ProductImageService;
import Service_Catalog.backend.services.ProductService;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

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

    @GetMapping("/new-arrivals")
    public List<ProductDto> showNewArrivalsProduct(@RequestParam(defaultValue = "4") Integer limit) {
        List<Product> products = productService.getNewArrivalsProducts(limit);
        return products.stream()
               .map(this::convertToDto)
               .collect(Collectors.toList()); 
    }

    @GetMapping("/{id}/related")
    public List<ProductDto> showSimilarProduct(@PathVariable Integer id, @RequestParam(defaultValue = "4") Integer limit) {
        List<Product> products = productService.getSimilarProducts(id, limit);
        return products.stream()
                .limit(limit)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/price/below/{maxPrice}")
    public List<ProductDto> showProductsBelowPrice(@PathVariable Double maxPrice) {
        List<Product> products = productService.getProductsBelowPrice(maxPrice);
        return products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/price/between/{minPrice}/{maxPrice}")
    public List<ProductDto> showProductsBetweenPrices(@PathVariable Double minPrice, @PathVariable Double maxPrice) {
        List<Product> products = productService.getProductsBetweenPrices(minPrice, maxPrice);
        return products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/price/above/{minPrice}")
    public List<ProductDto> showProductsAbovePrice(@PathVariable Double minPrice) {
        List<Product> products = productService.getProductsAbovePrice(minPrice);
        return products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/category/{categoryId}/random-image")
    public ResponseEntity<?> getRandomProductImageByCategoryId(@PathVariable Integer categoryId) {
        // Lấy tất cả sản phẩm thuộc danh mục
        List<Product> products = productService.getAllByCategoryId(categoryId);
        
        if (products == null || products.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // Chọn sản phẩm ngẫu nhiên
        Random random = new Random();
        Product randomProduct = products.get(random.nextInt(products.size()));
        
        // Lấy hình ảnh của sản phẩm (ưu tiên ảnh chính - isPrimary)
        if (randomProduct.getProductImages() != null && !randomProduct.getProductImages().isEmpty()) {
            // Tìm ảnh là primary (nếu có)
            Optional<ProductImage> primaryImage = randomProduct.getProductImages().stream()
                    .filter(ProductImage::getIsPrimary)
                    .findFirst();
            
            // Nếu có ảnh chính, trả về URL của ảnh đó
            if (primaryImage.isPresent()) {
                return ResponseEntity.ok().body(Map.of("imageUrl", primaryImage.get().getImageUrl()));
            }
            
            // Nếu không có ảnh chính, lấy ảnh đầu tiên
            ProductImage firstImage = randomProduct.getProductImages().get(0);
            return ResponseEntity.ok().body(Map.of("imageUrl", firstImage.getImageUrl()));
        }
        
        // Nếu không có ảnh nào, trả về 404
        return ResponseEntity.notFound().build();
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
