package fit.iuh.backend.controller.response;

import fit.iuh.backend.model.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


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
