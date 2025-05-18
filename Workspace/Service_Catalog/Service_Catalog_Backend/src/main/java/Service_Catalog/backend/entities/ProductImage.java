package Service_Catalog.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Getter
@Setter
@Entity
@Table(name = "product_images")
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "product_id")
    @JsonIgnoreProperties("productImages")
    private Product product;

    @Column(name = "image_url", nullable = false, length = Integer.MAX_VALUE)
    private String imageUrl;

    @Column(name = "is_primary")
    private Boolean isPrimary;

    @Column(name = "sort_order")
    private Integer sortOrder;

    // Phương thức tương thích ngược
    @Deprecated
    public Product getProductId() {
        return this.product;
    }

    @Deprecated
    public void setProductId(Product product) {
        this.product = product;
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