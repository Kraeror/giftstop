package org.lufi.giftstop.repo;

import org.lufi.giftstop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findBySlug(String slug);
}