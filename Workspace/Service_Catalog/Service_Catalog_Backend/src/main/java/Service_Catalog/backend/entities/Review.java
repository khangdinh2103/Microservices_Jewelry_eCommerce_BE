package Service_Catalog.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "content")
    private String content;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "user_id")
    private Integer userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "product_id")
    private Product product;

    // Phương thức tương thích ngược
    @Deprecated
    public Integer getUserid() {
        return this.userId;
    }

    @Deprecated
    public void setUserid(Integer userId) {
        this.userId = userId;
    }

    @Deprecated
    public Product getProductId() {
        return this.product;
    }

    @Deprecated
    public void setProductId(Product product) {
        this.product = product;
    }
}