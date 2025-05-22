package Service_Catalog.backend.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductImageDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String imageUrl;
    private Boolean isPrimary;
    private Integer sortOrder;
}