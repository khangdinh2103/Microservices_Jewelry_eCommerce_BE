package Service_Catalog.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewDto {
    private Integer id;
    private String content;
    private Integer rating;
    private Integer userId;
    private Integer productId;
}