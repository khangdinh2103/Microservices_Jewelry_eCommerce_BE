package Service_Catalog.backend.resrouces;

import Service_Catalog.backend.dto.ProductCreateDto;
import Service_Catalog.backend.dto.ProductUpdateDto;
import Service_Catalog.backend.entities.Category;
import Service_Catalog.backend.entities.Collection;
import Service_Catalog.backend.entities.Product;
import Service_Catalog.backend.entities.Productimage;
import Service_Catalog.backend.services.CategoryService;
import Service_Catalog.backend.services.CollectionService;
import Service_Catalog.backend.services.ProductService;
import Service_Catalog.backend.services.ProductimageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins= "*")
@RestController
@RequestMapping("/api/product")
public class ProductResource {
    @Autowired
    private ProductService productService;
    @Autowired
    private CollectionService collectionService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductimageService productimageService;

    @GetMapping("/listProduct")
    public List<Product> showProductList() {
        return productService.getAllProducts();
    }

    @GetMapping("/detailProduct/{id}")
    public Product showProductDetail(@PathVariable Integer id) {
        return productService.getProductById(id);
    }

    @GetMapping("/listProductByCategory/{categoryId}")
    public List<Product> showProductListByCategory(@PathVariable Integer categoryId) {
        return productService.getAllByCategoryId(categoryId);
    }

    @GetMapping("/listBrandByCategory/{categoryId}")
    public List<String> showBrandListByCategory(@PathVariable Integer categoryId) {
        return productService.getAllBrandByCategoryId(categoryId);
    }

    @GetMapping("/listMaterialByCategory/{categoryId}")
    public List<String> showMaterialListByCategory(@PathVariable Integer categoryId) {
        return productService.getAllMaterialByCategoryId(categoryId);
    }

    @GetMapping("/listSizeByCategory/{categoryId}")
    public List<String> showSizeListByCategory(@PathVariable Integer categoryId) {
        return productService.getAllSizeByCategoryId(categoryId);
    }

    @GetMapping("/listGoldKaratByCategory/{categoryId}")
    public List<String> showGoldKaratListByCategory(@PathVariable Integer categoryId) {
        return productService.getAllGoldKaratByCategoryId(categoryId);
    }

    @PostMapping("/addProduct")
    public ResponseEntity<Product> addProduct(@RequestBody ProductCreateDto productDto) {
        Product product = new Product();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setStock(productDto.getStock());
        product.setPrice(productDto.getPrice());
        product.setGender(productDto.getGender());
        product.setMaterial(productDto.getMaterial());
        product.setGoldKarat(productDto.getGoldKarat());
        product.setColor(productDto.getColor());
        product.setBrand(productDto.getBrand());
        product.setSize(productDto.getSize());

        if (productDto.getCategoryId() != null) {
            Category category = categoryService.getCategoryById(productDto.getCategoryId());
            product.setCategoryId(category);
        }

        if (productDto.getCollectionId() != null) {
            Collection collection = collectionService.getCollectionById(productDto.getCollectionId());
            product.setCollectionId(collection);
        }

        //Khong test them hinh anh, can hoan thien productimage api truoc

        if (productDto.getProductImageIds() != null) {
            List<Productimage> images = productimageService.getAllById(productDto.getProductImageIds());
            product.setProductImages(images);
        }

        Product savedProduct = productService.addProduct(product);
        return ResponseEntity.ok(savedProduct);
    }

    @PutMapping("/updateProduct/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Integer id, @RequestBody ProductUpdateDto productUpdateDTO) {
        Product existingProduct = productService.getProductById(id);
        if (existingProduct == null || !id.equals(productUpdateDTO.getId())) {
            return ResponseEntity.notFound().build();
        }
        if(productUpdateDTO.getName() != null)
            existingProduct.setName(productUpdateDTO.getName());
        if(productUpdateDTO.getDescription() != null)
            existingProduct.setDescription(productUpdateDTO.getDescription());
        if(productUpdateDTO.getStock() != null)
            existingProduct.setStock(productUpdateDTO.getStock());
        if(productUpdateDTO.getPrice() != null)
            existingProduct.setPrice(productUpdateDTO.getPrice());
        if(productUpdateDTO.getGender() != null)
            existingProduct.setGender(productUpdateDTO.getGender());
        if(productUpdateDTO.getSize() != null)
            existingProduct.setSize(productUpdateDTO.getSize());
        if(productUpdateDTO.getMaterial() != null)
            existingProduct.setMaterial(productUpdateDTO.getMaterial());
        if(productUpdateDTO.getColor() != null)
            existingProduct.setColor(productUpdateDTO.getColor());
        if(productUpdateDTO.getBrand() != null)
            existingProduct.setBrand(productUpdateDTO.getBrand());
        if(productUpdateDTO.getGoldKarat() != null)
            existingProduct.setGoldKarat(productUpdateDTO.getGoldKarat());

        if(productUpdateDTO.getCategoryId() != null) {
            Category category = categoryService.getCategoryById(productUpdateDTO.getCategoryId());
            existingProduct.setCategoryId(category);
        }

        if(productUpdateDTO.getCollectionId() != null) {
            Collection collection = collectionService.getCollectionById(productUpdateDTO.getCollectionId());
            existingProduct.setCollectionId(collection);
        }

        //Chua xu ly them hay cap nhat hinh anh product

        return ResponseEntity.ok(productService.updateProduct(existingProduct));
    }

    @DeleteMapping("/deleteProduct/{id}")
    public ResponseEntity<Product> deleteProduct(@PathVariable Integer id) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productService.deleteProduct(product));
    }

}
