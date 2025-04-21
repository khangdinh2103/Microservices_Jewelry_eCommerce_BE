package Service_Catalog.backend.repositories;

import Service_Catalog.backend.entities.CollectionImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CollectionImageRepository extends JpaRepository<CollectionImage, Integer> {
    @Query("SELECT c FROM CollectionImage c WHERE c.collection.id = ?1")
    List<CollectionImage> findAllByCollectionId(Integer collectionId);
}