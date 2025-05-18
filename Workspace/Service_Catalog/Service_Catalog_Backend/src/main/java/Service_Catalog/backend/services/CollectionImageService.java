package Service_Catalog.backend.services;

import Service_Catalog.backend.entities.CollectionImage;
import Service_Catalog.backend.repositories.CollectionImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectionImageService {
    @Autowired
    private CollectionImageRepository collectionimageRepository;

    public void addCollectionimage(CollectionImage collectionimage) {
        collectionimageRepository.save(collectionimage);
    }

    public void updateCollectionimage(CollectionImage collectionimage) {
        collectionimageRepository.save(collectionimage);
    }

    public void deleteCollectionimage(CollectionImage collectionimage) {
        collectionimageRepository.delete(collectionimage);
    }

    public CollectionImage getCollectionimageById(Integer id) {
        return collectionimageRepository.findById(id).orElse(null);
    }

    public Iterable<CollectionImage> getAllCollectionimages() {
        return collectionimageRepository.findAll();
    }

    public List<CollectionImage> getAllById(List<Integer> ids) {
        return collectionimageRepository.findAllById(ids);
    }

    public List<CollectionImage> getAllByCollectionId(Integer collectionId) {
        return collectionimageRepository.findAllByCollectionId(collectionId);
    }
}
