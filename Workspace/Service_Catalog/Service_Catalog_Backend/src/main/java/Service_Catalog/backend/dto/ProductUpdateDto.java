package Service_Catalog.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link Service_Catalog.backend.entities.Product}
 */

@Getter
@Setter
public class ProductUpdateDto implements Serializable {
    Integer id;
    String name;
    String description;
    Integer stock;
    Double price;
    Integer gender;
    String material;
    Integer goldKarat;
    String color;
    String brand;
    Integer categoryId;
    List<Integer> productImageIds;
    Integer collectionId;
    String size;
}