package org.lufi.giftstop.repo;

import org.lufi.giftstop.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findByProductId(Long productId);

    List<Image> findByOrderItemId(Long orderItemId);
}
