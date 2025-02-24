package fit.iuh.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private String name;
    private String description;
    private Integer stock;
    private Double price;
    private Integer gender;
    private String material;
    private Integer goldKarat;
    private String color;
    private String brand;
    private Integer viewCount;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "collection_id")
    private Collection collection;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductImage> imageSet;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "product")
    private List<Review> reviews;

    @OneToMany(mappedBy = "product")
    private List<OrderDetail> orderDetails;

    @OneToMany(mappedBy = "product")
    private List<CartItem> cartItems;



    public Long getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getStock() {
        return stock;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getGender() {
        return gender;
    }

    public String getMaterial() {
        return material;
    }

    public Integer getGoldKarat() {
        return goldKarat;
    }

    public String getColor() {
        return color;
    }

    public String getBrand() {
        return brand;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public Category getCategory() {
        return category;
    }

    public Collection getCollection() {
        return collection;
    }

    public List<ProductImage> getImageSet() {
        return imageSet;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public void setGoldKarat(Integer goldKarat) {
        this.goldKarat = goldKarat;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    public void setImageSet(List<ProductImage> imageSet) {
        this.imageSet = imageSet;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

}