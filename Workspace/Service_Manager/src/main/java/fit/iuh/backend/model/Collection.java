package fit.iuh.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "collections")
public class Collection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long collectionId;

    private String name;
    private String description;

    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL)
    private List<CollectionImage> imageSet;

    @OneToMany(mappedBy = "collection")
    private List<Product> products;

    public Long getCollectionId() {
        return collectionId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setCollectionId(Long collectionId) {
        this.collectionId = collectionId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }
}
