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
    private List<Integer> collectionImageIds;
    private List<Integer> productIds;
}
