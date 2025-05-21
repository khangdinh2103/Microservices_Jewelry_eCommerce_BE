package fit.iuh.backend.controller.response;

import java.time.LocalDateTime;
import java.util.List;

import fit.iuh.backend.model.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long productId;
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
    private List<ImageResponse> imageSet;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Review> reviews;


}
