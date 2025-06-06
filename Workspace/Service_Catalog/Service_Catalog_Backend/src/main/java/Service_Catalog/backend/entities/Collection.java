package Service_Catalog.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "collections")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Collection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @OneToMany(mappedBy = "collection")
    @JsonIgnoreProperties("collection")  // Quan trọng: ngăn vòng lặp vô hạn
    private List<CollectionImage> collectionImages = new ArrayList<>();

    @OneToMany(mappedBy = "collection")
    @JsonIgnoreProperties("collection")  // Quan trọng: ngăn vòng lặp vô hạn
    private List<Product> products = new ArrayList<>();
}