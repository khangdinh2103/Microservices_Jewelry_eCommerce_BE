package Service_Catalog.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "product")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "productid", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "gender", nullable = false)
    private Integer gender;

    @Column(name = "material")
    private String material;

    @Column(name = "gold_karat")
    private Integer goldKarat;

    @Column(name = "color", length = 50)
    private String color;

    @Column(name = "brand")
    private String brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "categoryid")
    @JsonIgnoreProperties({"products"})
    private Category categoryId;

    @Column(name = "createdat")
    private Instant createdAt;

    @Column(name = "updatedat")
    private Instant updatedAt;

    @OneToMany(mappedBy = "productId")
    @JsonIgnoreProperties({"productId"})
    private List<Productimage> productImages = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "collectionid")
    @JsonIgnoreProperties({"products", "collectionImages"})
    private Collection collectionId;

    @Column(name = "size")
    private String size;

    @OneToMany(mappedBy = "productId")
    @JsonIgnore
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "productId")
    @JsonIgnore
    private List<ProductSalesSummary> productSalesSummaries = new ArrayList<>();

}