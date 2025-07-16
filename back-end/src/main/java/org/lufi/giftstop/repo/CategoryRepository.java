package org.lufi.giftstop.repo;

import org.lufi.giftstop.dto.CategoryDto;
import org.lufi.giftstop.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentIsNull();
}