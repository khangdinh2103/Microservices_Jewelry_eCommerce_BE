package Service_Catalog.backend.services;

import Service_Catalog.backend.entities.Collection;
import Service_Catalog.backend.repositories.CollectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectionService {
    @Autowired
    private CollectionRepository collectionRepository;

    public Collection addCollection(Collection collection) {
        return collectionRepository.save(collection);
    }

    public Collection updateCollection(Collection collection) {
        return collectionRepository.save(collection);
    }

    public void deleteCollection(Collection collection) {
        collectionRepository.delete(collection);
    }

    public Collection getCollectionById(Integer id) {
        return collectionRepository.findById(id).orElse(null);
    }

    public List<Collection> getAllCollections() {
        return collectionRepository.findAll();
    }

}
