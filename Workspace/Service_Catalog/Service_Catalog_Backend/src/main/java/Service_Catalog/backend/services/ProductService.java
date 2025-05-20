package Service_Catalog.backend.services;

import Service_Catalog.backend.dto.ProductDto;
import Service_Catalog.backend.dto.ProductImageDto;
import Service_Catalog.backend.entities.Product;
import Service_Catalog.backend.entities.ProductImage;
import Service_Catalog.backend.entities.ProductSalesSummary;
import Service_Catalog.backend.repositories.ProductRepository;
import Service_Catalog.backend.repositories.ProductSalesSummaryRepository;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductSalesSummaryRepository productSalesSummaryRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_KEY_PRODUCT = "product";
    private static final long CACHE_TTL = 3600;

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

    @Transactional(readOnly = true)
    public ProductDto getProductById(Integer id) {
        String cacheKey = CACHE_KEY_PRODUCT + "_" + id;
        System.out.println("Cache key: " + cacheKey);

        Product product = null;
        // Lấy từ cache
        try {
            ProductDto productDto = (ProductDto) redisTemplate.opsForValue().get(cacheKey);
            System.out.println("Cache value: " + productDto);
            if (productDto != null) {
                System.out.println("Product found in cache: " + id);
                return productDto;
            }
        } catch (Exception e) {
            System.out.println("Cache error: " + e.getMessage());
        }

        // Không có trong cache -> lấy từ database
        System.out.println("Product not found in cache, fetching from database: " + id);
        product = productRepository.findById(id).orElse(null);

//        if (product != null) {
//            try {
//                //chuyển thành dto để lưu trữ trong cache
//                ProductDto dto = convertToDto(product);
//                redisTemplate.opsForValue().set(cacheKey, dto, CACHE_TTL, TimeUnit.SECONDS);
//            } catch (Exception e) {
//                System.out.println("Failed to cache product: " + e.getMessage());
//            }
//        }

        return convertToDto(product);
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

    @Transactional(readOnly = true)
    public List<ProductDto> getAllByCategoryIdDto(Integer categoryId) {
        String cacheKey = CACHE_KEY_PRODUCT + "_category_" + categoryId;
        System.out.println("Cache key: " + cacheKey);
        
        try {
            List<ProductDto> cachedDtos = (List<ProductDto>) redisTemplate.opsForValue().get(cacheKey);
            System.out.println("Cache value: " + cachedDtos);
            if (cachedDtos != null) {
                System.out.println("Products found in cache for category: " + categoryId);
                return cachedDtos;
            }
        } catch (Exception e) {
            System.out.println("Cache error: " + e.getMessage());
        }

        System.out.println("Products not found in cache, fetching from database for category: " + categoryId);
        List<Product> products = productRepository.findAllByCategoryId(categoryId);
        
        if (products != null && !products.isEmpty()) {
            try {
                List<ProductDto> dtos = products.stream()
                    .map(this::convertToDto)
                    .toList();
                redisTemplate.opsForValue().set(cacheKey, dtos, CACHE_TTL, TimeUnit.SECONDS);
                return dtos;
            } catch (Exception e) {
                System.out.println("Failed to cache products: " + e.getMessage());
            }
        }
        
        return products.stream()
            .map(this::convertToDto)
            .toList();
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

    public List<Product> getNewArrivalsProducts(Integer limit) {
        PageRequest pageRequest = PageRequest.of(0, limit);
        return productRepository.getNewArrivalsProducts(pageRequest);
    }

    public List<Product> getSimilarProducts(Integer productId, Integer limit) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return null;
        }

        PageRequest pageRequest = PageRequest.of(0, limit);
        return productRepository.findSimilarProducts(
                productId,
                product.getCollectionId() != null ? product.getCollectionId().getId() : null,
                product.getBrand(),
                product.getCategoryId() != null ? product.getCategoryId().getId() : null,
                pageRequest);
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

    private ProductDto convertToDto(Product product) {
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

        // Handle relationships
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
        }

        if (product.getCollection() != null) {
            dto.setCollectionId(product.getCollection().getId());
        }

        // Handle product images
        if (product.getProductImages() != null) {
            List<ProductImageDto> imageDtos = new ArrayList<>();
            for (ProductImage image : product.getProductImages()) {
                ProductImageDto imageDto = new ProductImageDto();
                imageDto.setId(image.getId());
                imageDto.setImageUrl(image.getImageUrl());
                imageDto.setIsPrimary(image.getIsPrimary());
                imageDto.setSortOrder(image.getSortOrder());
                imageDtos.add(imageDto);
            }
            dto.setProductImages(imageDtos);
        }

        return dto;
    }
}
