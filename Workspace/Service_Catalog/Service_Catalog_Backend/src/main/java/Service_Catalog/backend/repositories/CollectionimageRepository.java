package Service_Catalog.backend.repositories;

import Service_Catalog.backend.entities.Collectionimage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CollectionimageRepository extends JpaRepository<Collectionimage, Integer> {
    @Query("SELECT c FROM Collectionimage c WHERE c.collectionId.id = ?1")
    List<Collectionimage> findAllByCollectionId(Integer collectionId);
}