package fit.iuh.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "product_images")
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    @Column(columnDefinition = "TEXT")
    private String imageURL;
    private boolean isThumbnail;

    public Long getImageId() {
        return imageId;
    }

    public String getImageURL() {
        return imageURL;
    }

    public boolean isThumbnail() {
        return isThumbnail;
    }
    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setThumbnail(boolean thumbnail) {
        isThumbnail = thumbnail;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }
}
