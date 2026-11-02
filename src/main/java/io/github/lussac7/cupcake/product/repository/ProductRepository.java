package io.github.lussac7.cupcake.product.repository;

import io.github.lussac7.cupcake.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByActiveTrue();
}