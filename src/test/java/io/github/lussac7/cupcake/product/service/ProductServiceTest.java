package io.github.lussac7.cupcake.product.service;

import io.github.lussac7.cupcake.product.dto.ProductCreateRequest;
import io.github.lussac7.cupcake.product.dto.ProductResponse;
import io.github.lussac7.cupcake.product.dto.ProductUpdateRequest;
import io.github.lussac7.cupcake.product.entity.Product;
import io.github.lussac7.cupcake.product.repository.ProductRepository;
import io.github.lussac7.cupcake.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product activeProduct;

    @BeforeEach
    void setUp() {
        activeProduct = new Product(
                "Chocolate",
                "Cupcake de chocolate",
                new BigDecimal("8.50"),
                null
        );
    }

    @Test
    void shouldReturnAllActiveProducts() {
        when(productRepository.findAllByActiveTrue())
                .thenReturn(List.of(activeProduct));

        List<ProductResponse> result = productService.findAllActive();

        assertEquals(1, result.size());
        assertEquals("Chocolate", result.getFirst().name());
        assertEquals(new BigDecimal("8.50"), result.getFirst().price());

        verify(productRepository).findAllByActiveTrue();
    }

    @Test
    void shouldFindActiveProductById() {
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(activeProduct));

        ProductResponse result = productService.findActiveById(1L);

        assertEquals("Chocolate", result.name());
        assertTrue(result.active());

        verify(productRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenActiveProductDoesNotExist() {
        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.findActiveById(99L)
        );
    }

    @Test
    void shouldCreateProduct() {
        ProductCreateRequest request = new ProductCreateRequest(
                "Vanilla",
                "Cupcake de baunilha",
                new BigDecimal("7.50"),
                null
        );

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponse result = productService.create(request);

        assertEquals("Vanilla", result.name());
        assertEquals("Cupcake de baunilha", result.description());
        assertEquals(new BigDecimal("7.50"), result.price());
        assertTrue(result.active());

        verify(productRepository).save(any(Product.class));
    }

    @Test
    void shouldUpdateProduct() {
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(activeProduct));

        ProductUpdateRequest request = new ProductUpdateRequest(
                "Chocolate Premium",
                "Cupcake especial de chocolate",
                new BigDecimal("10.00"),
                null
        );

        ProductResponse result = productService.update(1L, request);

        assertEquals("Chocolate Premium", result.name());
        assertEquals(
                "Cupcake especial de chocolate",
                result.description()
        );
        assertEquals(new BigDecimal("10.00"), result.price());

        verify(productRepository).findById(1L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void shouldDeactivateProduct() {
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(activeProduct));

        productService.deactivate(1L);

        assertFalse(activeProduct.isActive());

        verify(productRepository).findById(1L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void shouldActivateProduct() {
        activeProduct.deactivate();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(activeProduct));

        productService.activate(1L);

        assertTrue(activeProduct.isActive());

        verify(productRepository).findById(1L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonexistentProduct() {
        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        ProductUpdateRequest request = new ProductUpdateRequest(
                "Chocolate",
                "Descrição",
                new BigDecimal("8.50"),
                null
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.update(99L, request)
        );
    }
}