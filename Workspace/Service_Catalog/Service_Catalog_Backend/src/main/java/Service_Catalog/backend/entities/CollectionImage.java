package Service_Catalog.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "collection_images")
public class CollectionImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "collection_id")
    private Collection collection;

    @Column(name = "image_url", nullable = false, length = Integer.MAX_VALUE)
    private String imageUrl;

    @Column(name = "is_primary")
    private Boolean isPrimary;

    @Column(name = "sort_order")
    private Integer sortOrder;

    // Phương thức tương thích ngược
    @Deprecated
    public Collection getCollectionId() {
        return this.collection;
    }

    @Deprecated
    public void setCollectionId(Collection collection) {
        this.collection = collection;
    }

    @Deprecated
    public Boolean getIsThumbnail() {
        return this.isPrimary;
    }

    @Deprecated
    public void setIsThumbnail(Boolean isThumbnail) {
        this.isPrimary = isThumbnail;
    }
}