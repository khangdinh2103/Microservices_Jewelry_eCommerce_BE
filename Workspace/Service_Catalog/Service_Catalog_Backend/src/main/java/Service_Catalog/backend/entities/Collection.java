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
@Table(name = "collection")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Collection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "collectionid", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @OneToMany(mappedBy = "collectionId")
    @JsonIgnoreProperties({"collectionId"})
    private List<Collectionimage> collectionImages = new ArrayList<>();

    @OneToMany(mappedBy = "collectionId")
    private List<Product> products = new ArrayList<>();

}