package Service_Catalog.backend.resources;

import Service_Catalog.backend.dto.CategoryDto;
import Service_Catalog.backend.entities.Category;
import Service_Catalog.backend.entities.Product;
import Service_Catalog.backend.services.CategoryService;
import Service_Catalog.backend.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
public class CategoryResource {
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private ProductService productService;

    @GetMapping("")
    public List<CategoryDto> showCategoryList() {
        List<Category> categories = categoryService.getAllCategories();
        return categories.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public CategoryDto showCategoryDetail(@PathVariable Integer id) {
        return convertToDto(categoryService.getCategoryById(id));
    }
    
    @PostMapping("")
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryDto categoryDto) {
        Category category = convertToEntity(categoryDto);
        Category savedCategory = categoryService.addCategory(category);
        return ResponseEntity.ok(convertToDto(savedCategory));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Integer id, @RequestBody CategoryDto categoryDto) {
        Category category = convertToEntity(categoryDto);
        category.setId(id);
        Category updatedCategory = categoryService.updateCategory(category);
        return ResponseEntity.ok(convertToDto(updatedCategory));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
        Category category = categoryService.getCategoryById(id);
        if (category != null) {
            categoryService.deleteCategory(category);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    // Conversion methods
    private CategoryDto convertToDto(Category category) {
        if (category == null) return null;
        
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setUrl(category.getUrl());
        
        // Chỉ lấy ID của các sản phẩm thay vì toàn bộ sản phẩm
        if (category.getProducts() != null) {
            dto.setProductIds(category.getProducts().stream()
                    .map(Product::getId)
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    private Category convertToEntity(CategoryDto dto) {
        if (dto == null) return null;
        
        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setUrl(dto.getUrl());
        
        // Không cần set products vì nó sẽ được quản lý bởi mối quan hệ JPA
        
        return category;
    }
}