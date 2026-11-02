package io.github.lussac7.cupcake.product.service;

import io.github.lussac7.cupcake.product.dto.ProductCreateRequest;
import io.github.lussac7.cupcake.product.dto.ProductResponse;
import io.github.lussac7.cupcake.product.dto.ProductUpdateRequest;
import io.github.lussac7.cupcake.product.entity.Product;
import io.github.lussac7.cupcake.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductResponse> findAllActive() {
        return productRepository.findAllByActiveTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse findActiveById(Long id) {
        Product product = getActiveProductOrThrow(id);

        return toResponse(product);
    }

    @Transactional
    public ProductResponse create(ProductCreateRequest request) {
        Product product = new Product(
                request.name(),
                request.description(),
                request.price(),
                request.imageUrl()
        );

        Product savedProduct = productRepository.save(product);

        return toResponse(savedProduct);
    }

    @Transactional
    public ProductResponse update(Long id, ProductUpdateRequest request) {
        Product product = getProductOrThrow(id);

        product.updateDetails(
                request.name(),
                request.description(),
                request.price(),
                request.imageUrl()
        );

        return toResponse(product);
    }

    @Transactional
    public void deactivate(Long id) {
        Product product = getProductOrThrow(id);

        product.deactivate();
    }

    @Transactional
    public void activate(Long id) {
        Product product = getProductOrThrow(id);

        product.activate();
    }

    private Product getProductOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Product not found with id: " + id
                ));
    }

    private Product getActiveProductOrThrow(Long id) {
        Product product = getProductOrThrow(id);

        if (!product.isActive()) {
            throw new NoSuchElementException(
                    "Active product not found with id: " + id
            );
        }

        return product;
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getImageUrl(),
                product.isActive()
        );
    }
}