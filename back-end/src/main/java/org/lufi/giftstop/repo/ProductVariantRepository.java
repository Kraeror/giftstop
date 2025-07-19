package org.lufi.giftstop.repo;

import org.lufi.giftstop.model.Product;
import org.lufi.giftstop.model.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    void deleteAllByProduct(Product product);
}