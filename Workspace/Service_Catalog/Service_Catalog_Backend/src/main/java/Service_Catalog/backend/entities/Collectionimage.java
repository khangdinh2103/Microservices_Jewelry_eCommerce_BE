package Service_Catalog.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "collectionimage")
public class Collectionimage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "imageid", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "collectionid")
    private Collection collectionId;

    @Column(name = "imageurl", nullable = false, length = Integer.MAX_VALUE)
    private String imageUrl;

    @Column(name = "isthumbnail")
    private Boolean isThumbnail;

}