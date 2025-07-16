package org.lufi.giftstop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.lufi.giftstop.dto.CreateProductRequest;
import org.lufi.giftstop.dto.ProductListDto;
import org.lufi.giftstop.dto.ProductResponse;
import org.lufi.giftstop.dto.UpdateProductActiveRequest;
import org.lufi.giftstop.model.*;
import org.lufi.giftstop.repo.CategoryRepository;
import org.lufi.giftstop.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AdminProductController {

    private final ProductService productService;
    private final CategoryRepository categoryRepository;
    private final ObjectMapper objectMapper;

    @PatchMapping("{id}/active")
    public ResponseEntity<Void> updateProductActive(
            @PathVariable Long id,
            @RequestBody UpdateProductActiveRequest request
    ) {
        productService.updateActiveStatus(id, request.isActive());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping
    public ResponseEntity<List<ProductListDto>> getAllProductsForAdmin() {
        List<ProductListDto> products = productService.getAllForAdmin();
        return ResponseEntity.ok(products);
    }

    /*@PutMapping("/api/admin/products/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestPart("product") @Valid CreateProductRequest productDto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestPart(value = "variants", required = false) List<CreateProductRequest.ProductVariantDto> variants,
            @RequestPart(value = "variantImages", required = false) List<MultipartFile> variantImages
    ) {
        try {
            productService.updateProduct(id, productDto, images, variants, variantImages);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Продуктът не е намерен.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Грешка при обновяване на продукта.");
        }
    }*/

    @GetMapping("{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Product> createProduct(
            @RequestPart("product") MultipartFile productPart,
            @RequestPart(value = "images", required = false) List<MultipartFile> productImages,
            @RequestPart(value = "variants", required = false) List<MultipartFile> variantParts,
            @RequestPart(value = "variantImages", required = false) List<MultipartFile> variantImages
    ) throws Exception {

        // 1. Парсиране на основния продукт
        CreateProductRequest request = objectMapper.readValue(productPart.getInputStream(), CreateProductRequest.class);

        List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());

        Product product = Product.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .salePrice(request.getSalePrice())
                .slug(request.getSlug())
                .active(request.isActive())
                .categories(categories)
                .build();

        // 2. Добавяне на варианти (без изображения)
        List<ProductVariant> variants = new ArrayList<>();
        if (variantParts != null) {
            for (MultipartFile variantPart : variantParts) {
                CreateProductRequest.ProductVariantDto dto = objectMapper.readValue(variantPart.getInputStream(), CreateProductRequest.ProductVariantDto.class);

                ProductVariant variant = ProductVariant.builder()
                        .name(dto.getTitle())
                        .price(dto.getPrice())
                        .salePrice(dto.getSalePrice())
                        .active(dto.isActive())
                        .product(product)
                        .imageFileName(dto.getImageFileName())
                        .build();

                variants.add(variant);
            }
        }

        product.setVariants(variants);

        // 3. Съхраняване на продукта в базата
        Product savedProduct = productService.createProduct(product);

        // 4. Качване и присвояване на изображения
        productService.assignImages(savedProduct, productImages, variantImages);

        return ResponseEntity.ok(savedProduct);
    }
}
