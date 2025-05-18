package Service_Catalog.backend.repositories;

import Service_Catalog.backend.entities.Collection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectionRepository extends JpaRepository<Collection, Integer> {
}