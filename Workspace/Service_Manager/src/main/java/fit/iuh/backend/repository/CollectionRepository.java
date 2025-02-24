package fit.iuh.backend.repository;

import fit.iuh.backend.model.Category;
import fit.iuh.backend.model.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Long> {
    Optional<Collection> findById(Long id);
}

