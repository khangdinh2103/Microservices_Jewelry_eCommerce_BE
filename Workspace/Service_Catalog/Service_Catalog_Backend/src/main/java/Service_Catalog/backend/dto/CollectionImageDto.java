package Service_Catalog.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CollectionImageDto {
    private Integer id;
    private String imageUrl;
    private Boolean isPrimary;
    private Integer sortOrder;
}