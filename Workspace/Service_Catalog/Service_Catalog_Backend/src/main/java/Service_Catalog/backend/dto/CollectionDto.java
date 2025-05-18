package Service_Catalog.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CollectionDto {
    private Integer id;
    private String name;
    private String description;
    private List<CollectionImageDto> collectionImages;
    private List<Integer> productIds;
}