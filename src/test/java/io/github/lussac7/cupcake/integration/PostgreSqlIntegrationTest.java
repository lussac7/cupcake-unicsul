package io.github.lussac7.cupcake.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import io.github.lussac7.cupcake.product.entity.Product;
import io.github.lussac7.cupcake.product.repository.ProductRepository;
import org.springframework.transaction.annotation.Transactional;
import io.github.lussac7.cupcake.order.entity.Order;
import io.github.lussac7.cupcake.order.repository.OrderRepository;

import java.math.BigDecimal;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class PostgreSqlIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer POSTGRES =
            new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldConnectToPostgreSQL() {
        String version = jdbcTemplate.queryForObject(
                "SELECT version()",
                String.class
        );

        assertNotNull(version);
        assertEquals(true, version.contains("PostgreSQL"));
    }

    @Test
    void shouldApplyInitialFlywayMigration() {
        Integer migrationCount = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM flyway_schema_history
                WHERE version = '1'
                  AND success = TRUE
                """,
                Integer.class
        );

        assertEquals(1, migrationCount);
    }

    @Test
    @Transactional
    void shouldPersistAndRetrieveProductUsingJpa() {
        Product product = new Product(
                "Cupcake Integration Test",
                "Product created during integration testing.",
                new BigDecimal("12.50"),
                null
        );

        Product saved = productRepository.save(product);

        Product retrieved = productRepository.findById(saved.getId())
                .orElseThrow();

        assertEquals(saved.getId(), retrieved.getId());
        assertEquals("Cupcake Integration Test", retrieved.getName());
        assertEquals(0, retrieved.getPrice().compareTo(
                new BigDecimal("12.50")
        ));
    }

    @Test
    void shouldEnforceProductPriceConstraintInPostgreSQL() {
        assertThrows(
                org.springframework.dao.DataIntegrityViolationException.class,
                () -> jdbcTemplate.update(
                        """
                        INSERT INTO products (name, description, price)
                        VALUES (?, ?, ?)
                        """,
                        "Invalid Cupcake",
                        "Invalid product price.",
                        new BigDecimal("-5.00")
                )
        );
    }

    @Test
    @Transactional
    void shouldPersistOrderWithItems() {
        Product product = new Product(
                "Cupcake Order Test",
                "Product for order integration testing.",
                new BigDecimal("10.00"),
                null
        );

        Product savedProduct = productRepository.save(product);

        Order order = new Order(
                "Maria Silva",
                "maria@example.com",
                "Rua das Flores, 100"
        );

        order.addItem(savedProduct, 2);

        Order savedOrder = orderRepository.saveAndFlush(order);

        Order retrieved = orderRepository.findById(savedOrder.getId())
                .orElseThrow();

        assertEquals(1, retrieved.getItems().size());
        assertEquals(0, retrieved.getTotal().compareTo(
                new BigDecimal("20.00")
        ));
    }

    @Test
    @Transactional
    void shouldPreserveOrderItemSnapshotAfterProductUpdate() {
        Product product = new Product(
                "Cupcake Original",
                "Original description.",
                new BigDecimal("10.00"),
                null
        );

        Product savedProduct = productRepository.save(product);

        Order order = new Order(
                "Maria Silva",
                "maria@example.com",
                "Rua das Flores, 100"
        );

        order.addItem(savedProduct, 2);

        savedProduct.updateDetails(
                "Cupcake Updated",
                "Updated description.",
                new BigDecimal("15.00"),
                null
        );

        Order savedOrder = orderRepository.saveAndFlush(order);

        Order retrieved = orderRepository.findById(savedOrder.getId())
                .orElseThrow();

        var item = retrieved.getItems().getFirst();

        assertEquals("Cupcake Original", item.getProductName());
        assertEquals(0, item.getUnitPrice().compareTo(
                new BigDecimal("10.00")
        ));
        assertEquals(0, retrieved.getTotal().compareTo(
                new BigDecimal("20.00")
        ));
    }

    @Test
    void shouldEnforceOrderItemProductForeignKey() {
        Order order = new Order(
                "Maria Silva",
                "maria@example.com",
                "Rua das Flores, 100"
        );

        Product product = new Product(
                "Cupcake FK Test",
                "Product for foreign key testing.",
                new BigDecimal("10.00"),
                null
        );

        Product savedProduct = productRepository.saveAndFlush(product);
        order.addItem(savedProduct, 1);
        Order savedOrder = orderRepository.saveAndFlush(order);

        assertThrows(
                org.springframework.dao.DataIntegrityViolationException.class,
                () -> jdbcTemplate.update(
                        """
                        INSERT INTO order_items (
                            order_id, product_id, product_name,
                            unit_price, quantity, subtotal
                        )
                        VALUES (?, ?, ?, ?, ?, ?)
                        """,
                        savedOrder.getId(),
                        999999L,
                        "Cupcake inexistente",
                        new BigDecimal("10.00"),
                        1,
                        new BigDecimal("10.00")
                )
        );
    }

    @Test
    void shouldEnforceOrderItemSubtotalConstraint() {
        Product product = new Product(
                "Cupcake Subtotal Test",
                "Product for subtotal constraint testing.",
                new BigDecimal("10.00"),
                null
        );

        Product savedProduct = productRepository.saveAndFlush(product);

        Order order = new Order(
                "Maria Silva",
                "maria@example.com",
                "Rua das Flores, 100"
        );

        order.addItem(savedProduct, 1);
        Order savedOrder = orderRepository.saveAndFlush(order);

        assertThrows(
                org.springframework.dao.DataIntegrityViolationException.class,
                () -> jdbcTemplate.update(
                        """
                        INSERT INTO order_items (
                            order_id, product_id, product_name,
                            unit_price, quantity, subtotal
                        )
                        VALUES (?, ?, ?, ?, ?, ?)
                        """,
                        savedOrder.getId(),
                        savedProduct.getId(),
                        "Cupcake Subtotal Test",
                        new BigDecimal("10.00"),
                        2,
                        new BigDecimal("15.00")
                )
        );
    }

    @Test
    void shouldEnforceOrderItemOrderForeignKey() {
        Product product = new Product(
                "Cupcake Order FK Test",
                "Product for order foreign key testing.",
                new BigDecimal("10.00"),
                null
        );

        Product savedProduct = productRepository.saveAndFlush(product);

        assertThrows(
                org.springframework.dao.DataIntegrityViolationException.class,
                () -> jdbcTemplate.update(
                        """
                        INSERT INTO order_items (
                            order_id, product_id, product_name,
                            unit_price, quantity, subtotal
                        )
                        VALUES (?, ?, ?, ?, ?, ?)
                        """,
                        -1L,
                        savedProduct.getId(),
                        "Cupcake Order FK Test",
                        new BigDecimal("10.00"),
                        1,
                        new BigDecimal("10.00")
                )
        );
    }

    @Test
    void shouldEnforceUniqueProductPerOrder() {
        Product product = new Product(
                "Cupcake Unique Test",
                "Product for unique constraint testing.",
                new BigDecimal("10.00"),
                null
        );

        Product savedProduct = productRepository.saveAndFlush(product);

        Order order = new Order(
                "Maria Silva",
                "maria@example.com",
                "Rua das Flores, 100"
        );

        order.addItem(savedProduct, 1);

        Order savedOrder = orderRepository.saveAndFlush(order);

        assertThrows(
                org.springframework.dao.DataIntegrityViolationException.class,
                () -> jdbcTemplate.update(
                        """
                        INSERT INTO order_items (
                            order_id, product_id, product_name,
                            unit_price, quantity, subtotal
                        )
                        VALUES (?, ?, ?, ?, ?, ?)
                        """,
                        savedOrder.getId(),
                        savedProduct.getId(),
                        "Cupcake Unique Test",
                        new BigDecimal("10.00"),
                        1,
                        new BigDecimal("10.00")
                )
        );
    }

    @Test
    void shouldCreateProductThroughRestApi() throws Exception {
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "Cupcake Integration Test",
                                "description": "Product created through REST integration test.",
                                "price": 12.50,
                                "imageUrl": "https://example.com/cupcake.jpg"
                            }
                            """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Cupcake Integration Test"))
                .andExpect(jsonPath("$.price").value(12.50));
    }

    @Test
    void shouldRetrieveProductThroughRestApi() throws Exception {
        Product product = new Product(
                "Cupcake REST Retrieval",
                "Product for REST retrieval integration test.",
                new BigDecimal("15.00"),
                null
        );

        Product savedProduct = productRepository.saveAndFlush(product);

        mockMvc.perform(get("/api/v1/products/{id}", savedProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(savedProduct.getId()))
                .andExpect(jsonPath("$.name").value("Cupcake REST Retrieval"))
                .andExpect(jsonPath("$.price").value(15.00))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void shouldCreateOrderThroughRestApi() throws Exception {
        Product product = new Product(
                "Cupcake Order REST",
                "Product for order REST integration test.",
                new BigDecimal("12.50"),
                null
        );

        Product savedProduct = productRepository.saveAndFlush(product);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "customerName": "Maria Silva",
                                "customerEmail": "maria@example.com",
                                "deliveryAddress": "Rua das Flores, 100",
                                "items": [
                                    {
                                        "productId": %d,
                                        "quantity": 2
                                    }
                                ]
                            }
                            """.formatted(savedProduct.getId())))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.customerName").value("Maria Silva"))
                .andExpect(jsonPath("$.customerEmail").value("maria@example.com"))
                .andExpect(jsonPath("$.total").value(25.00))
                .andExpect(jsonPath("$.items.length()").value(1));
    }

    @Test
    void shouldRetrieveOrderThroughRestApi() throws Exception {
        Product product = new Product(
                "Cupcake Order Retrieval",
                "Product for order retrieval integration test.",
                new BigDecimal("15.00"),
                null
        );

        Product savedProduct = productRepository.saveAndFlush(product);

        Order order = new Order(
                "Carlos Souza",
                "carlos@example.com",
                "Avenida Brasil, 200"
        );

        order.addItem(savedProduct, 2);

        Order savedOrder = orderRepository.saveAndFlush(order);

        mockMvc.perform(get("/api/v1/orders/{id}", savedOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(savedOrder.getId()))
                .andExpect(jsonPath("$.customerName").value("Carlos Souza"))
                .andExpect(jsonPath("$.customerEmail").value("carlos@example.com"))
                .andExpect(jsonPath("$.total").value(30.00))
                .andExpect(jsonPath("$.items.length()").value(1));
    }

}