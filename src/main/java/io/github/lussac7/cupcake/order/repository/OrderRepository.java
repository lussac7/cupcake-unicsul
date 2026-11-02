package io.github.lussac7.cupcake.order.repository;

import io.github.lussac7.cupcake.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}