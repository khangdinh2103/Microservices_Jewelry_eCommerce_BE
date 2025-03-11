package fit.iuh.backend.service.impl;

import ch.qos.logback.core.util.StringUtil;
import fit.iuh.backend.controller.request.ImageRequest;
import fit.iuh.backend.controller.request.ProductCreationRequest;
import fit.iuh.backend.controller.request.ProductUpdateRequest;
import fit.iuh.backend.controller.response.ImageResponse;
import fit.iuh.backend.controller.response.ProductPageResponse;
import fit.iuh.backend.controller.response.ProductResponse;
import fit.iuh.backend.model.*;
import fit.iuh.backend.repository.CategoryRepository;
import fit.iuh.backend.repository.CollectionRepository;
import fit.iuh.backend.repository.ProductImageRepository;
import fit.iuh.backend.repository.ProductRepository;
import fit.iuh.backend.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.OpenApiResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j(topic = "PRODUCT-SERVICE")
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CollectionRepository collectionRepository;
    private final ProductImageRepository productImageRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, CollectionRepository collectionRepository, ProductImageRepository productImageRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.collectionRepository = collectionRepository;
        this.productImageRepository = productImageRepository;
    }

    @Override
    public ProductPageResponse getAllProducts(String keyword , String column,String direction, int page, int size) {
        String columnName = StringUtils.hasLength(column) ? column : "productId";
        Sort.Direction sortDirection = Sort.Direction.DESC;

        if (StringUtils.hasLength(direction) && direction.equalsIgnoreCase("asc")) {
            sortDirection = Sort.Direction.ASC;
        }
        Sort.Order order = new Sort.Order(sortDirection, columnName);



        Pageable pageable = PageRequest.of(page, size, Sort.by(order));

        Page<Product> products;
        if (StringUtils.hasLength(keyword)) {
            keyword = "%" + keyword.toLowerCase() + "%";
            products = productRepository.searchByKeyword(keyword, pageable);
        } else {
            products = productRepository.findAll(pageable);
        }


        List<ProductResponse> productList = products.stream()
                .map(product -> {

                    List<ImageResponse> imageResponses = product.getImageSet().stream()
                            .filter(image -> image.isThumbnail())
                            .map(image -> new ImageResponse(image.getImageId(), image.getImageURL(), image.isThumbnail()))
                            .collect(Collectors.toList());

                    return ProductResponse.builder()
                            .productId(product.getProductId())
                            .name(product.getName())
                            .description(product.getDescription())
                            .stock(product.getStock())
                            .price(product.getPrice())
                            .gender(product.getGender())
                            .material(product.getMaterial())
                            .goldKarat(product.getGoldKarat())
                            .color(product.getColor())
                            .brand(product.getBrand())
                            .viewCount(product.getViewCount())
                            .categoryId(product.getCategory().getCategoryId())
                            .collectionId(product.getCollection().getCollectionId())
                            .imageSet(imageResponses)
                            .createdAt(product.getCreatedAt())
                            .updatedAt(product.getUpdatedAt())
                            .build();
                })
                .toList();
        ProductPageResponse response = new ProductPageResponse();
        response.setPageNumber(page);
        response.setPageSize(size);
        response.setTotalElements(products.getTotalElements());
        response.setTotalPages(products.getTotalPages());
        response.setProducts(productList);
        return response;
    }


    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new OpenApiResourceNotFoundException("Product not found with id: " + id));
        List<ImageResponse> imageResponses = product.getImageSet().stream()
                .map(image -> new ImageResponse(image.getImageId(), image.getImageURL(), image.isThumbnail()))
                .collect(Collectors.toList());
        return ProductResponse.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .description(product.getDescription())
                .stock(product.getStock())
                .price(product.getPrice())
                .gender(product.getGender())
                .material(product.getMaterial())
                .goldKarat(product.getGoldKarat())
                .color(product.getColor())
                .brand(product.getBrand())
                .viewCount(product.getViewCount())
                .categoryId(product.getCategory().getCategoryId())
                .collectionId(product.getCollection().getCollectionId())
                .imageSet(imageResponses)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .reviews(product.getReviews())
                .build();
    }



    @Override
    @Transactional
    public Long createProduct(ProductCreationRequest req) {
        log.info("Creating product: {}", req);

        Product product = new Product();
        product.setName(req.getName());
        product.setDescription(req.getDescription());
        product.setStock(req.getStock());
        product.setPrice(req.getPrice());
        product.setGender(req.getGender());
        product.setMaterial(req.getMaterial());
        product.setGoldKarat(req.getGoldKarat());
        product.setColor(req.getColor());
        product.setBrand(req.getBrand());
        product.setViewCount(0);
        product.setReviews(null);

        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new OpenApiResourceNotFoundException("Category not found with id: " + req.getCategoryId()));
        product.setCategory(category);

        Collection collection = collectionRepository.findById(req.getCollectionId())
                .orElseThrow(() -> new OpenApiResourceNotFoundException("Collection not found with id: " + req.getCollectionId()));
        product.setCollection(collection);

        LocalDateTime now = LocalDateTime.now();
        product.setCreatedAt(now);
        product.setUpdatedAt(now);

        productRepository.save(product);

        log.info("Saved product: {}", product);
        if (req.getImageSet() != null && !req.getImageSet().isEmpty()) {
            List<ProductImage> images = req.getImageSet().stream().map(imageRequest -> {
                ProductImage image = new ProductImage();
                image.setProduct(product);
                image.setImageURL(imageRequest.getUrl());
                image.setThumbnail(imageRequest.isThumbnail());
                return image;
            }).toList();

            productImageRepository.saveAll(images);
            log.info("Saved images for product: {}", product.getProductId());
        }


        return product.getProductId();
    }



    @Override
    @Transactional
    public Long updateProduct(Long productId, ProductUpdateRequest req) {
        log.info("Updating product with id {}: {}", productId, req);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new OpenApiResourceNotFoundException("Product not found with id: " + productId));


        product.setName(req.getName());
        product.setDescription(req.getDescription());
        product.setStock(req.getStock());
        product.setPrice(req.getPrice());
        product.setGender(req.getGender());
        product.setMaterial(req.getMaterial());
        product.setGoldKarat(req.getGoldKarat());
        product.setColor(req.getColor());
        product.setBrand(req.getBrand());


        if (!product.getCategory().getCategoryId().equals(req.getCategoryId())) {
            Category category = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new OpenApiResourceNotFoundException("Category not found with id: " + req.getCategoryId()));
            product.setCategory(category);
        }


        if (!product.getCollection().getCollectionId().equals(req.getCollectionId())) {
            Collection collection = collectionRepository.findById(req.getCollectionId())
                    .orElseThrow(() -> new OpenApiResourceNotFoundException("Collection not found with id: " + req.getCollectionId()));
            product.setCollection(collection);
        }

        product.setUpdatedAt(LocalDateTime.now());

        productRepository.save(product);


        return product.getProductId();
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        log.info("Deleting product with id: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new OpenApiResourceNotFoundException("Product not found with id: " + productId));

        List<ProductImage> images = productImageRepository.findByProduct_ProductId(productId);
        if (!images.isEmpty()) {
            productImageRepository.deleteAll(images);
            log.info("Deleted {} images associated with product {}", images.size(), productId);
        }


        productRepository.delete(product);
        log.info("Successfully deleted product with id: {}", productId);
    }
    @Override
    public ProductPageResponse getOutOfStockProducts(int page,int size){
        int pageNo = 0;
        if (page > 0) {
            pageNo = page - 1;
        }

        Pageable pageable = PageRequest.of(pageNo, size);

        Page<Product> products = productRepository.findByStockEquals(0,pageable);
        List<ProductResponse> productList = products.stream()
                .map(product -> ProductResponse.builder()
                        .productId(product.getProductId())
                        .name(product.getName())
                        .description(product.getDescription())
                        .stock(product.getStock())
                        .price(product.getPrice())
                        .gender(product.getGender())
                        .material(product.getMaterial())
                        .goldKarat(product.getGoldKarat())
                        .color(product.getColor())
                        .brand(product.getBrand())
                        .viewCount(product.getViewCount())
                        .categoryId(product.getCategory().getCategoryId())
                        .collectionId(product.getCollection().getCollectionId())
                        .createdAt(product.getCreatedAt())
                        .updatedAt(product.getUpdatedAt())
                        .build()
                )
                .toList();
        ProductPageResponse response = new ProductPageResponse();
        response.setPageNumber(page);
        response.setPageSize(size);
        response.setTotalElements(products.getTotalElements());
        response.setTotalPages(products.getTotalPages());
        response.setProducts(productList);
        return response;

    }
    @Override
    public ProductPageResponse getLowStockProducts(int threshold,int page,int size) {
        int pageNo = 0;
        if (page > 0) {
            pageNo = page - 1;
        }

        Pageable pageable = PageRequest.of(pageNo, size);
        Page<Product> products = productRepository.findByStockLessThanEqual(threshold,pageable);
        List<ProductResponse> productList = products.stream()
                .map(product -> ProductResponse.builder()
                        .productId(product.getProductId())
                        .name(product.getName())
                        .description(product.getDescription())
                        .stock(product.getStock())
                        .price(product.getPrice())
                        .gender(product.getGender())
                        .material(product.getMaterial())
                        .goldKarat(product.getGoldKarat())
                        .color(product.getColor())
                        .brand(product.getBrand())
                        .viewCount(product.getViewCount())
                        .categoryId(product.getCategory().getCategoryId())
                        .collectionId(product.getCollection().getCollectionId())
                        .createdAt(product.getCreatedAt())
                        .updatedAt(product.getUpdatedAt())
                        .build()
                )
                .toList();
        ProductPageResponse response = new ProductPageResponse();
        response.setPageNumber(page);
        response.setPageSize(size);
        response.setTotalElements(products.getTotalElements());
        response.setTotalPages(products.getTotalPages());
        response.setProducts(productList);
        return response;

    }
    @Override
    @Transactional
    public Long updateProductStock(Long productId, int newStock){
        log.info("Updating stock for product id: {} to {}", productId, newStock);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new OpenApiResourceNotFoundException("Product not found with id: " + productId));

        product.setStock(newStock);
        product.setUpdatedAt(LocalDateTime.now());

        Product updatedProduct = productRepository.save(product);
        log.info("Updated stock for product id: {}", productId);

        return updatedProduct.getProductId();
    }

    @Override
    @Transactional
    public void importProductsFromCSV(MultipartFile file) throws Exception {
        log.info("Starting product import from CSV");

        if (file.isEmpty()) {
            throw new IllegalArgumentException("CSV file is empty");
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            int lineCount = 0;

            line = br.readLine(); // Read the header line
            if (line == null) {
                throw new IllegalArgumentException("CSV file has no content");
            }

            while ((line = br.readLine()) != null) {
                lineCount++;
                try {
                    String[] data = line.split(",");

                    if (data.length < 12) { // Adjust based on the number of expected fields
                        throw new IllegalArgumentException("Invalid CSV format at line " + lineCount);
                    }

                    Product product = new Product();
                    product.setName(data[0].trim());
                    product.setDescription(data[1].trim());
                    product.setStock(Integer.parseInt(data[2].trim()));
                    product.setPrice(Double.parseDouble(data[3].trim()));
                    product.setGender(Integer.parseInt(data[4].trim()));
                    product.setMaterial(data[5].trim());
                    product.setGoldKarat(Integer.parseInt(data[6].trim()));
                    product.setColor(data[7].trim());
                    product.setBrand(data[8].trim());
                    product.setViewCount(0);
                    product.setCreatedAt(LocalDateTime.now());
                    product.setUpdatedAt(LocalDateTime.now());


                    Category category = categoryRepository.findById(Long.parseLong(data[9].trim()))
                            .orElseThrow(() -> new OpenApiResourceNotFoundException("Category not found: " + data[9].trim()));
                    product.setCategory(category);

                    Collection collection = collectionRepository.findById(Long.parseLong(data[10].trim()))
                            .orElseThrow(() -> new OpenApiResourceNotFoundException("Collection not found: " + data[10].trim()));
                    product.setCollection(collection);

                    productRepository.save(product);
                    log.info("Imported product from line {}: {}", lineCount, product);


                    ProductImage productImage = new ProductImage();
                    productImage.setProduct(product);
                    productImage.setImageURL(data[11].trim());
                    productImage.setThumbnail(true);
                    productImageRepository.save(productImage);

                    log.info("Saved image for product at line {}: {}", lineCount);



            } catch (Exception e) {
                    log.error("Error processing line {}: {}", lineCount, line, e);
                    throw new RuntimeException("Error processing CSV file at line " + lineCount, e);
                }
            }
            log.info("Successfully imported {} products from CSV", lineCount);
        }


    }
    @Override
    @Transactional
    public List<Long> addImagesToProduct(Long productId, List<ImageRequest> imageRequests) throws OpenApiResourceNotFoundException {
        log.info("Adding {} images to product id: {}", imageRequests.size(), productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new OpenApiResourceNotFoundException("Product not found with id: " + productId));

        List<Long> savedImageIds = new ArrayList<>();

        for (ImageRequest imageRequest : imageRequests) {
            ProductImage productImage = new ProductImage();
            productImage.setProduct(product);
            productImage.setImageURL(imageRequest.getUrl());
            productImage.setThumbnail(imageRequest.isThumbnail());

            ProductImage savedImage = productImageRepository.save(productImage);
            savedImageIds.add(savedImage.getImageId());
        }

        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);

        log.info("Added {} images to product id: {}", savedImageIds.size(), productId);

        return savedImageIds;
    }

    @Override
    @Transactional
    public void deleteImageFromProduct(Long productId, Long imageId) throws OpenApiResourceNotFoundException {
        log.info("Deleting image id: {} from product id: {}", imageId, productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new OpenApiResourceNotFoundException("Product not found with id: " + productId));

        ProductImage productImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new OpenApiResourceNotFoundException("Image not found with id: " + imageId));

        if (!productImage.getProduct().getProductId().equals(productId)) {
            throw new IllegalArgumentException("Image does not belong to the specified product");
        }

        productImageRepository.delete(productImage);

        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);

        log.info("Deleted image id: {} from product id: {}", imageId, productId);
    }
}



