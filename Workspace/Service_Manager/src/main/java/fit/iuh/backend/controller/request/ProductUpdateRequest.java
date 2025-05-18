package fit.iuh.backend.controller.request;

import fit.iuh.backend.model.Review;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequest {
    private String name;
    private String description;
    private Integer stock;
    private Double price;
    private Integer gender;
    private String material;
    private Integer goldKarat;
    private String color;
    private String brand;
    private Integer viewCount;
    private Long categoryId;
    private Long collectionId;


}
