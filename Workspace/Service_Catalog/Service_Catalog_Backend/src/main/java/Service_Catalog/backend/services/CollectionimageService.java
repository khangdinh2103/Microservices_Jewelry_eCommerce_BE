package Service_Catalog.backend.services;

import Service_Catalog.backend.entities.Collectionimage;
import Service_Catalog.backend.repositories.CollectionimageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectionimageService {
    @Autowired
    private CollectionimageRepository collectionimageRepository;

    public void addCollectionimage(Collectionimage collectionimage) {
        collectionimageRepository.save(collectionimage);
    }

    public void updateCollectionimage(Collectionimage collectionimage) {
        collectionimageRepository.save(collectionimage);
    }

    public void deleteCollectionimage(Collectionimage collectionimage) {
        collectionimageRepository.delete(collectionimage);
    }

    public Collectionimage getCollectionimageById(Integer id) {
        return collectionimageRepository.findById(id).orElse(null);
    }

    public Iterable<Collectionimage> getAllCollectionimages() {
        return collectionimageRepository.findAll();
    }

    public List<Collectionimage> getAllById(List<Integer> ids) {
        return collectionimageRepository.findAllById(ids);
    }

    public List<Collectionimage> getAllByCollectionId(Integer collectionId) {
        return collectionimageRepository.findAllByCollectionId(collectionId);
    }
}
