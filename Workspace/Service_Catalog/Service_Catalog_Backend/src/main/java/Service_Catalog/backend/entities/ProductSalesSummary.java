package Service_Catalog.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "product_sales_summary")
public class ProductSalesSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "quantity_sold")
    private Integer quantitySold;

    @Column(name = "order_date")
    private LocalDate orderDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "product_id")
    @JsonIgnoreProperties("productImages")
    private Product product;

    // Phương thức tương thích ngược
    @Deprecated
    public Product getProductId() {
        return this.product;
    }

    @Deprecated
    public void setProductId(Product product) {
        this.product = product;
    }
}