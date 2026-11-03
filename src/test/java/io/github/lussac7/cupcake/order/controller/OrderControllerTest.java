package io.github.lussac7.cupcake.order.controller;

import io.github.lussac7.cupcake.order.dto.OrderCreateRequest;
import io.github.lussac7.cupcake.order.dto.OrderResponse;
import io.github.lussac7.cupcake.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;

import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    void shouldCreateOrder() throws Exception {
        OrderResponse order = new OrderResponse(
                1L,
                "João Silva",
                "joao@email.com",
                "Rua das Flores, 100",
                Instant.parse("2026-11-02T15:00:00Z"),
                new BigDecimal("25.00"),
                List.of()
        );

        when(orderService.create(any(OrderCreateRequest.class)))
                .thenReturn(order);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "customerName": "João Silva",
                                    "customerEmail": "joao@email.com",
                                    "deliveryAddress": "Rua das Flores, 100",
                                    "items": [
                                        {
                                            "productId": 1,
                                            "quantity": 2
                                        }
                                    ]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.customerName").value("João Silva"))
                .andExpect(jsonPath("$.total").value(25.00));
    }

    @Test
    void shouldRejectInvalidOrderCreation() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "customerName": "",
                                    "customerEmail": "email-invalido",
                                    "deliveryAddress": "",
                                    "items": []
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFindOrderById() throws Exception {
        OrderResponse order = new OrderResponse(
                1L,
                "João Silva",
                "joao@email.com",
                "Rua das Flores, 100",
                Instant.parse("2026-11-02T15:00:00Z"),
                new BigDecimal("25.00"),
                List.of()
        );

        when(orderService.findById(1L)).thenReturn(order);

        mockMvc.perform(get("/api/v1/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.customerName").value("João Silva"))
                .andExpect(jsonPath("$.customerEmail").value("joao@email.com"))
                .andExpect(jsonPath("$.total").value(25.00));
    }

    @Test
    void shouldReturnPaginatedOrders() throws Exception {
        when(orderService.findAll(any()))
                .thenReturn(org.springframework.data.domain.Page.empty(
                        PageRequest.of(
                                1,
                                5,
                                Sort.by("createdAt").descending()
                        )
                ));

        mockMvc.perform(get("/api/v1/orders")
                        .param("page", "1")
                        .param("size", "5")
                        .param("sort", "createdAt,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.size").value(5));

        ArgumentCaptor<Pageable> pageableCaptor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(orderService).findAll(pageableCaptor.capture());

        Pageable pageable = pageableCaptor.getValue();

        assertEquals(1, pageable.getPageNumber());
        assertEquals(5, pageable.getPageSize());

        assertEquals(
                Sort.Direction.DESC,
                pageable.getSort().getOrderFor("createdAt").getDirection()
        );
    }

    @Test
    void shouldUseDefaultPaginationAndSortingForOrders() throws Exception {
        when(orderService.findAll(any()))
                .thenReturn(org.springframework.data.domain.Page.empty());

        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk());

        ArgumentCaptor<Pageable> pageableCaptor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(orderService).findAll(pageableCaptor.capture());

        Pageable pageable = pageableCaptor.getValue();

        assertEquals(0, pageable.getPageNumber());
        assertEquals(20, pageable.getPageSize());
        assertEquals(
                Sort.Direction.DESC,
                pageable.getSort().getOrderFor("createdAt").getDirection()
        );
    }

    @Test
    void shouldRejectOrderWithInvalidItem() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "customerName": "João Silva",
                                "customerEmail": "joao@email.com",
                                "deliveryAddress": "Rua das Flores, 100",
                                "items": [
                                    {
                                        "productId": null,
                                        "quantity": 0
                                    }
                                ]
                            }
                            """))
                .andExpect(status().isBadRequest());
    }
}