package Service_Catalog.backend.repositories;

import Service_Catalog.backend.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
}