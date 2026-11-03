package io.github.lussac7.cupcake.product.controller;

import io.github.lussac7.cupcake.product.dto.ProductCreateRequest;
import io.github.lussac7.cupcake.product.dto.ProductResponse;
import io.github.lussac7.cupcake.product.dto.ProductUpdateRequest;
import io.github.lussac7.cupcake.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;

import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    void shouldReturnActiveProducts() throws Exception {
        ProductResponse product = new ProductResponse(
                1L,
                "Chocolate",
                "Cupcake de chocolate",
                new BigDecimal("12.50"),
                null,
                true
        );

        when(productService.findAllActive(any()))
                .thenReturn(new PageImpl<>(
                        List.of(product),
                        PageRequest.of(
                                0,
                                20,
                                Sort.by("price").ascending()
                        ),
                        1
                ));

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Chocolate"))
                .andExpect(jsonPath("$.content[0].price").value(12.50))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    void shouldReturnProductById() throws Exception {
        ProductResponse product = new ProductResponse(
                1L,
                "Chocolate",
                "Cupcake de chocolate",
                new BigDecimal("12.50"),
                null,
                true
        );

        when(productService.findActiveById(1L)).thenReturn(product);

        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Chocolate"));
    }

    @Test
    void shouldCreateProduct() throws Exception {
        ProductResponse product = new ProductResponse(
                1L,
                "Chocolate",
                "Cupcake de chocolate",
                new BigDecimal("12.50"),
                null,
                true
        );

        when(productService.create(any(ProductCreateRequest.class)))
                .thenReturn(product);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Chocolate",
                                    "description": "Cupcake de chocolate",
                                    "price": 12.50,
                                    "imageUrl": null
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Chocolate"));
    }

    @Test
    void shouldRejectInvalidProductCreation() throws Exception {
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "",
                                    "description": "",
                                    "price": 0
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateProduct() throws Exception {
        ProductResponse product = new ProductResponse(
                1L,
                "Chocolate Especial",
                "Cupcake especial de chocolate",
                new BigDecimal("15.00"),
                null,
                true
        );

        when(productService.update(
                org.mockito.ArgumentMatchers.eq(1L),
                any(ProductUpdateRequest.class)
        )).thenReturn(product);

        mockMvc.perform(put("/api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Chocolate Especial",
                                    "description": "Cupcake especial de chocolate",
                                    "price": 15.00,
                                    "imageUrl": null
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Chocolate Especial"))
                .andExpect(jsonPath("$.price").value(15.00));
    }

    @Test
    void shouldDeactivateProduct() throws Exception {
        doNothing().when(productService).deactivate(1L);

        mockMvc.perform(delete("/api/v1/products/1"))
                .andExpect(status().isNoContent());

        verify(productService).deactivate(1L);
    }

    @Test
    void shouldApplyPaginationAndSortingParameters() throws Exception {
        when(productService.findAllActive(any()))
                .thenReturn(org.springframework.data.domain.Page.empty());

        mockMvc.perform(get("/api/v1/products")
                        .param("page", "1")
                        .param("size", "5")
                        .param("sort", "name,asc"))
                .andExpect(status().isOk());

        ArgumentCaptor<Pageable> pageableCaptor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(productService).findAllActive(pageableCaptor.capture());

        Pageable pageable = pageableCaptor.getValue();

        assertEquals(1, pageable.getPageNumber());
        assertEquals(5, pageable.getPageSize());
        assertEquals("name", pageable.getSort().getOrderFor("name").getProperty());
        assertEquals(
                org.springframework.data.domain.Sort.Direction.ASC,
                pageable.getSort().getOrderFor("name").getDirection()
        );
    }

    @Test
    void shouldRejectInvalidProductUpdate() throws Exception {
        mockMvc.perform(put("/api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "",
                                "description": "",
                                "price": 0
                            }
                            """))
                .andExpect(status().isBadRequest());
    }

}