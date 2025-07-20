package org.lufi.giftstop.repo;

import org.lufi.giftstop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findBySlug(String slug);
    List<Product> findAllByOrderByCreatedAtDesc();
}