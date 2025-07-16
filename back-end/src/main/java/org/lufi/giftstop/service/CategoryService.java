package org.lufi.giftstop.service;

import org.lufi.giftstop.dto.CategoryDto;
import org.lufi.giftstop.model.Category;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getAllAsTree();
    Category create(CategoryDto dto);
    Category update(Long id, CategoryDto dto);
    void delete(Long id);
    Category getById(Long id);
    CategoryDto getDtoById(Long id);
    Category save(Category category);
}
