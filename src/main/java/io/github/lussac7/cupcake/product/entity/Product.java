package io.github.lussac7.cupcake.product.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(nullable = false)
    private boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Product() {
        // Required by JPA.
    }

    public Product(
            String name,
            String description,
            BigDecimal price,
            String imageUrl
    ) {
        this.name = requireText(name, "Product name cannot be blank.");
        this.description = requireText(
                description,
                "Product description cannot be blank."
        );
        this.price = requirePositivePrice(price);
        this.imageUrl = normalizeImageUrl(imageUrl);
        this.active = true;
    }

    public void updateDetails(
            String name,
            String description,
            BigDecimal price,
            String imageUrl
    ) {
        String validatedName = requireText(
                name,
                "Product name cannot be blank."
        );

        String validatedDescription = requireText(
                description,
                "Product description cannot be blank."
        );

        BigDecimal validatedPrice = requirePositivePrice(price);
        String validatedImageUrl = normalizeImageUrl(imageUrl);

        this.name = validatedName;
        this.description = validatedDescription;
        this.price = validatedPrice;
        this.imageUrl = validatedImageUrl;
    }

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    private static String requireText(String value, String message) {
        Objects.requireNonNull(value, message);

        String normalized = value.trim();

        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(message);
        }

        return normalized;
    }

    private static BigDecimal requirePositivePrice(BigDecimal price) {
        Objects.requireNonNull(price, "Product price cannot be null.");

        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Product price must be greater than zero."
            );
        }

        return price;
    }

    private static String normalizeImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return null;
        }

        return imageUrl.trim();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}