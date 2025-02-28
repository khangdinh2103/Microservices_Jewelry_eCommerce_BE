package Service_Catalog.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductCreateDto {
    private String name;
    private String description;
    private Integer stock;
    private Double price;
    private Integer gender;
    private String material;
    private Integer goldKarat;
    private String color;
    private String brand;
    private Integer categoryId;
    private List<Integer> productImageIds;
    private Integer collectionId;
    private String size;
}