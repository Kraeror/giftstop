package org.lufi.giftstop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.lufi.giftstop.dto.CategoryDto;
import org.lufi.giftstop.model.Category;
import org.lufi.giftstop.repo.CategoryRepository;
import org.lufi.giftstop.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;
    private final ObjectMapper objectMapper;

    /**
     * Връща всички root категории с техните рекурсивни подкатегории.
     */
    @GetMapping
    public List<CategoryDto> getAllAsTree() {
        return categoryService.getAllAsTree();
    }

    /**
     * Създава нова категория (може да бъде и подкатегория).
     */
    @PostMapping
    public ResponseEntity<CategoryDto> create(@RequestBody CategoryDto dto) {
        Category saved = categoryService.create(dto);
        return ResponseEntity.ok(objectMapper.convertValue(saved, CategoryDto.class));
    }

    /**
     * Изтрива категория и всичките ѝ подкатегории.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Променя името на съществуваща категория.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<CategoryDto> update(@PathVariable Long id, @RequestBody CategoryDto dto) {
        Category updated = categoryService.update(id, dto);
        return ResponseEntity.ok(objectMapper.convertValue(updated, CategoryDto.class));
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateCategoryWithForm(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String slug,
            @RequestParam(required = false) String metaTitle,
            @RequestParam(required = false) String metaDescription,
            @RequestPart(required = false) MultipartFile image
    ) {
        try {
            Category category = categoryService.getById(id); // създай метод ако го няма

            category.setName(name);
            category.setSlug(slug);
            category.setMetaTitle(metaTitle);
            category.setMetaDescription(metaDescription);

            if (image != null && !image.isEmpty()) {
                // Записваме файла и обновяваме imageUrl
                String filename = UUID.randomUUID() + "-" + image.getOriginalFilename();
                Path path = Paths.get("uploads/categories/" + filename);
                Files.createDirectories(path.getParent());
                Files.write(path, image.getBytes());

                category.setImageUrl("/uploads/categories/" + filename);
            }

            categoryService.save(category); // използвай `save` или `update`, както ти е логиката
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Грешка при обновяване на категорията");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getDtoById(id));
    }
}
