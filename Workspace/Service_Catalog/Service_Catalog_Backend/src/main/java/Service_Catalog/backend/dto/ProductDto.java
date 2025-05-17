package Service_Catalog.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class ProductDto {
    private Integer id;
    private String name;
    private String code;
    private String description;
    private Integer quantity;
    private Double price;
    private String status;
    private Integer gender;
    private String material;
    private Integer goldKarat;
    private String color;
    private String brand;
    private String size;
    private Instant createdAt;
    private Instant updatedAt;
    
    // Thông tin về category
    private Integer categoryId;
    private String categoryName;
    
    // Thông tin về collection
    private Integer collectionId;
    private String collectionName;
    
    // Danh sách hình ảnh
    private List<ProductImageDto> productImages;
}