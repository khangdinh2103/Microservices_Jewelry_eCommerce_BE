package fit.iuh.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "collection_images")
public class CollectionImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    @ManyToOne
    @JoinColumn(name = "collection_id")
    private Collection collection;

    private String imageURL;
    private boolean isThumbnail;
}
