package io.github.lussac7.cupcake.order.entity;

import io.github.lussac7.cupcake.product.entity.Product;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_name", nullable = false, length = 150)
    private String customerName;

    @Column(name = "customer_email", nullable = false, length = 254)
    private String customerEmail;

    @Column(name = "delivery_address", nullable = false, columnDefinition = "TEXT")
    private String deliveryAddress;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<OrderItem> items = new ArrayList<>();

    protected Order() {
        // Required by JPA.
    }

    public Order(
            String customerName,
            String customerEmail,
            String deliveryAddress
    ) {
        this.customerName = requireText(
                customerName,
                "Customer name cannot be blank."
        );

        this.customerEmail = requireText(
                customerEmail,
                "Customer email cannot be blank."
        );

        this.deliveryAddress = requireText(
                deliveryAddress,
                "Delivery address cannot be blank."
        );
    }

    public void addItem(Product product, int quantity) {
        Objects.requireNonNull(product, "Product must not be null.");

        if (!product.isActive()) {
            throw new IllegalArgumentException(
                    "Inactive products cannot be added to an order."
            );
        }

        boolean alreadyAdded = items.stream()
                .anyMatch(item -> sameProduct(item.getProduct(), product));

        if (alreadyAdded) {
            throw new IllegalArgumentException(
                    "Product already exists in this order."
            );
        }

        OrderItem item = new OrderItem(this, product, quantity);

        items.add(item);

        recalculateTotal();
    }

    private boolean sameProduct(Product first, Product second) {
        if (first == second) {
            return true;
        }

        return first.getId() != null
                && first.getId().equals(second.getId());
    }

    private void recalculateTotal() {
        this.total = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @PrePersist
    private void validateBeforePersist() {
        if (items.isEmpty()) {
            throw new IllegalStateException(
                    "An order must contain at least one item."
            );
        }

        recalculateTotal();
    }

    private static String requireText(String value, String message) {
        Objects.requireNonNull(value, message);

        String normalized = value.trim();

        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(message);
        }

        return normalized;
    }

    public Long getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public List<OrderItem> getItems() {
        return List.copyOf(items);
    }
}