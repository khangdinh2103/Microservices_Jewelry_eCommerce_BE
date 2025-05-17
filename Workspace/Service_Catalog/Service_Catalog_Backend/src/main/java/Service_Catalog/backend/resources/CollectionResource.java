package Service_Catalog.backend.resources;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Service_Catalog.backend.dto.CollectionDto;
import Service_Catalog.backend.dto.CollectionImageDto;
import Service_Catalog.backend.entities.Collection;
import Service_Catalog.backend.entities.CollectionImage;
import Service_Catalog.backend.services.CollectionImageService;
import Service_Catalog.backend.services.CollectionService;

@RestController
@RequestMapping("/api/collections")
public class CollectionResource {
    @Autowired
    private CollectionService collectionService;
    
    @Autowired
    private CollectionImageService collectionImageService;

    @GetMapping("")
    public List<CollectionDto> showCollectionList() {
        List<Collection> collections = collectionService.getAllCollections();
        return collections.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public CollectionDto showCollectionDetail(@PathVariable Integer id) {
        return convertToDto(collectionService.getCollectionById(id));
    }

    @GetMapping("/{collectionId}/images")
    public List<CollectionImageDto> showImageListByCollection(@PathVariable Integer collectionId) {
        List<CollectionImage> images = collectionImageService.getAllByCollectionId(collectionId);
        return images.stream()
                .map(this::convertToImageDto)
                .collect(Collectors.toList());
    }
    
    // Các phương thức CRUD khác
    
    // Conversion methods
    private CollectionDto convertToDto(Collection collection) {
        if (collection == null) return null;
        
        CollectionDto dto = new CollectionDto();
        dto.setId(collection.getId());
        dto.setName(collection.getName());
        dto.setDescription(collection.getDescription());
        
        // Set images
        if (collection.getCollectionImages() != null) {
            dto.setCollectionImages(collection.getCollectionImages().stream()
                    .map(this::convertToImageDto)
                    .collect(Collectors.toList()));
        }
        
        // Chỉ lấy ID của các sản phẩm
        if (collection.getProducts() != null) {
            dto.setProductIds(collection.getProducts().stream()
                    .map(product -> product.getId())
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    private CollectionImageDto convertToImageDto(CollectionImage image) {
        if (image == null) return null;
        
        CollectionImageDto dto = new CollectionImageDto();
        dto.setId(image.getId());
        dto.setImageUrl(image.getImageUrl());
        dto.setIsPrimary(image.getIsPrimary());
        dto.setSortOrder(image.getSortOrder());
        return dto;
    }
}