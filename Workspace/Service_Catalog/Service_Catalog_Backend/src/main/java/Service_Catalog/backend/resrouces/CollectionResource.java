package Service_Catalog.backend.resrouces;

import Service_Catalog.backend.dto.CollectionDto;
import Service_Catalog.backend.entities.Collection;
import Service_Catalog.backend.entities.CollectionImage;
import Service_Catalog.backend.entities.Product;
import Service_Catalog.backend.services.CollectionService;
import Service_Catalog.backend.services.CollectionImageService;
import Service_Catalog.backend.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins= "*")
@RestController
@RequestMapping("/api/collection")
public class CollectionResource {
    @Autowired
    private CollectionService collectionService;
    @Autowired
    private CollectionImageService collectionimageService;
    @Autowired
    private ProductService productService;

    @GetMapping("/listCollection")
    public List<Collection> showCollectionList() {
        return collectionService.getAllCollections();
    }

    @GetMapping("/detailCollection/{id}")
    public Collection showCollectionDetail(@PathVariable Integer id) {
        return collectionService.getCollectionById(id);
    }

    @GetMapping("/listImageByCollection/{collectionId}")
    public List<CollectionImage> showImageListByCollection(@PathVariable Integer collectionId) {
        return collectionimageService.getAllByCollectionId(collectionId);
    }

    @PostMapping("/addCollection")
    public ResponseEntity<Collection> addCollection(@RequestBody CollectionDto collectionDto) {
        Collection collection = new Collection();
        collection.setName(collectionDto.getName());
        collection.setDescription(collectionDto.getDescription());

        if(collectionDto.getCollectionImageIds() != null) {
            collection.setCollectionImages(collectionimageService.getAllById(collectionDto.getCollectionImageIds()));
        }

        if(collectionDto.getProductIds() != null) {
            collection.setProducts(productService.getAllByIds(collectionDto.getProductIds()));
        }
        Collection savedCollection = collectionService.addCollection(collection);

        for (Integer productId : collectionDto.getProductIds()) {
            Product product = productService.getProductById(productId);
            product.setCollectionId(savedCollection);
            productService.updateProduct(product);
        }

        return ResponseEntity.ok(savedCollection);
    }

    @PutMapping("/updateCollection/{id}")
    public ResponseEntity<Collection> updateCollection(@RequestBody CollectionDto collectionDto, @PathVariable Integer id) {
        Collection existingCollection = collectionService.getCollectionById(id);
        if(existingCollection == null || !id.equals(collectionDto.getId())) {
            return ResponseEntity.notFound().build();
        }

        if(collectionDto.getName() != null) {
            existingCollection.setName(collectionDto.getName());
        }
        if(collectionDto.getDescription() != null) {
            existingCollection.setDescription(collectionDto.getDescription());
        }
        if(collectionDto.getCollectionImageIds() != null) {
            existingCollection.setCollectionImages(collectionimageService.getAllById(collectionDto.getCollectionImageIds()));
        }
        return ResponseEntity.ok(collectionService.updateCollection(existingCollection));
    }
}
