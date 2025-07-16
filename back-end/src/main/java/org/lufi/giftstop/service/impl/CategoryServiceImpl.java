package org.lufi.giftstop.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.lufi.giftstop.dto.CategoryDto;
import org.lufi.giftstop.model.Category;
import org.lufi.giftstop.repo.CategoryRepository;
import org.lufi.giftstop.service.CategoryService;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    @Override
    public List<CategoryDto> getAllAsTree() {
        List<Category> roots = categoryRepository.findByParentIsNull();
        return roots.stream()
                .map(this::toDto)
                .toList();
    }

    private CategoryDto toDto(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setSlug(category.getSlug());
        dto.setMetaTitle(category.getMetaTitle());
        dto.setMetaDescription(category.getMetaDescription());
        dto.setImageUrl(category.getImageUrl());
        dto.setParentId(category.getParent() != null ? category.getParent().getId() : null);

        // Вложени подкатегории, без да сериализираш products или други свързани обекти
        dto.setChildren(
                category.getChildren().stream()
                        .map(this::toDto)
                        .collect(Collectors.toList())
        );

        return dto;
    }

    @Override
    public Category create(CategoryDto dto) {
        Category category = new Category();
        mapDtoToCategory(dto, category);

        if (dto.getParentId() != null) {
            Category parent = categoryRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent not found"));
            category.setParent(parent);
        }

        return categoryRepository.save(category);
    }

    @Override
    public Category update(Long id, CategoryDto dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        mapDtoToCategory(dto, category);
        return categoryRepository.save(category);
    }

    private void mapDtoToCategory(CategoryDto dto, Category category) {
        category.setName(dto.getName());
        category.setSlug(dto.getSlug());
        category.setMetaTitle(dto.getMetaTitle());
        category.setMetaDescription(dto.getMetaDescription());
        category.setImageUrl(dto.getImageUrl());
    }

    @Override
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public Category getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Override
    public CategoryDto getDtoById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return toDto(category);
    }

    @Override
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

}
