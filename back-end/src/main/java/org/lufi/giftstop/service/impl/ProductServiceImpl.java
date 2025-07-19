package org.lufi.giftstop.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.lufi.giftstop.dto.CreateProductRequest;
import org.lufi.giftstop.dto.ProductListDto;
import org.lufi.giftstop.dto.ProductResponse;
import org.lufi.giftstop.model.*;
import org.lufi.giftstop.repo.CategoryRepository;
import org.lufi.giftstop.repo.ImageRepository;
import org.lufi.giftstop.repo.ProductRepository;
import org.lufi.giftstop.repo.ProductVariantRepository;
import org.lufi.giftstop.service.FileStorageService;
import org.lufi.giftstop.service.ProductService;
import org.lufi.giftstop.service.ProductVariantFieldService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;
    private final FileStorageService fileStorageService;
    private final ProductVariantFieldService productVariantFieldService;
    private final CategoryRepository categoryRepository;
    private final ProductVariantRepository variantRepository;

    @Override
    @Transactional
    public Product createProduct(CreateProductRequest request, List<MultipartFile> productImages, List<MultipartFile> variantParts, List<MultipartFile> variantImages) throws IOException {
        List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());

        Product product = Product.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .slug(request.getSlug())
                .categories(categories)
                .images(new ArrayList<>())
                .build();

        List<CreateProductRequest.ProductVariantDto> variantDtos = request.getVariants();
        List<ProductVariant> variants = new ArrayList<>();
        if (variantParts != null) {
            for (int i = 0; i < variantParts.size(); i++) {
                MultipartFile variantPart = variantParts.get(i);

                String json = new String(variantPart.getBytes());

                // Ръчно парсване от JSON (само с прости операции)
                // Тук приемаме, че всеки ред е валиден JSON
                // Реално – frontend вече е пратил всички полета в request.getVariants()
                CreateProductRequest.ProductVariantDto dto = variantDtos.get(i);

                ProductVariant variant = ProductVariant.builder()
                        .name(dto.getTitle())
                        .price(dto.getPrice())
                        .salePrice(dto.getSalePrice())
                        .active(dto.isActive())
                        .imageFileName(dto.getImageFileName())
                        .defaultVariant(dto.isDefaultVariant())
                        .product(product)
                        .build();

                variants.add(variant);
            }
        }

        product.setVariants(variants);
        Product savedProduct = productRepository.save(product);

        // Обработка на customFields
        for (int i = 0; i < variants.size(); i++) {
            CreateProductRequest.ProductVariantDto dto = variantDtos.get(i);
            ProductVariant variant = savedProduct.getVariants().get(i);
            if (dto.getCustomFields() != null) {
                productVariantFieldService.createFieldsForVariant(variant, dto.getCustomFields());
            }
        }

        assignImages(savedProduct, productImages, variantImages);

        return savedProduct;
    }


    private void assignImages(Product product, List<MultipartFile> productImages, List<MultipartFile> variantImages) {
        if (product.getImages() == null) {
            product.setImages(new ArrayList<>());
        }

        if (productImages != null) {
            for (MultipartFile file : productImages) {
                String fileName = fileStorageService.storeProductFile(product.getId(), file);
                Image image = Image.builder()
                        .fileName(fileName)
                        .product(product)
                        .build();
                product.getImages().add(image);
            }
        }

        if (variantImages != null) {
            List<ProductVariant> variants = product.getVariants();
            for (int i = 0; i < variantImages.size() && i < variants.size(); i++) {
                MultipartFile file = variantImages.get(i);
                ProductVariant variant = variants.get(i);
                if (!variant.isDefaultVariant()) {
                    String fileName = fileStorageService.storeProductFile(product.getId(), file);
                    variant.setImageFileName(fileName);
                }
            }
        }
    }

    @Override
    public List<ProductListDto> getAllForAdmin() {
        return productRepository.findAll().stream().map(product -> {
            ProductVariant defaultVariant = product.getVariants().stream()
                    .filter(ProductVariant::isDefaultVariant)
                    .findFirst()
                    .orElse(null);

            String imageFileName = null;
            String price = null;
            String salePrice = null;
            boolean active = false;

            if (defaultVariant != null) {
                imageFileName = defaultVariant.getImageFileName();
                price = defaultVariant.getPrice() != null ? defaultVariant.getPrice().toPlainString() : null;
                salePrice = defaultVariant.getSalePrice() != null ? defaultVariant.getSalePrice().toPlainString() : null;
                active = defaultVariant.isActive();
            }

            if ((imageFileName == null || imageFileName.isEmpty()) && !product.getImages().isEmpty()) {
                imageFileName = product.getImages().getFirst().getFileName();
            }

            return ProductListDto.builder()
                    .id(product.getId())
                    .title(product.getTitle())
                    .imageFileName(imageFileName)
                    .price(price)
                    .salePrice(salePrice)
                    .active(active)
                    .build();
        }).toList();
    }

    @Override
    @Transactional
    public void updateActiveStatus(Long productId, boolean active) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Продуктът не е намерен"));

        for (ProductVariant variant : product.getVariants()) {
            if (variant.isDefaultVariant()) {
                variant.setActive(active);
                break;
            }
        }
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Продуктът не е намерен"));

        return ProductResponse.fromEntity(product);
    }

    @Override
    @Transactional
    public void deleteProductById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Продукт с ID " + id + " не съществува");
        }
        productRepository.deleteById(id);
    }


    @Override
    @Transactional
    public void updateProduct(Long id,
                              CreateProductRequest request,
                              List<MultipartFile> productImages,
                              List<MultipartFile> variantParts,
                              List<MultipartFile> variantImages) throws IOException {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Продуктът не е намерен"));

        List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());

        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setSlug(request.getSlug());
        product.setCategories(categories);

        // Изтриване на старите изображения и варианти
        if (product.getImages() != null) {
            product.getImages().clear();
        } else {
            product.setImages(new ArrayList<>());
        }

        variantRepository.deleteAllByProduct(product);

        // Създаване на нови варианти
        List<CreateProductRequest.ProductVariantDto> variantDtos = request.getVariants();
        List<ProductVariant> variants = new ArrayList<>();

        if (variantParts != null) {
            for (int i = 0; i < variantParts.size(); i++) {
                MultipartFile variantPart = variantParts.get(i);
                String json = new String(variantPart.getBytes());
                CreateProductRequest.ProductVariantDto dto = variantDtos.get(i);

                ProductVariant variant = ProductVariant.builder()
                        .name(dto.getTitle())
                        .price(dto.getPrice())
                        .salePrice(dto.getSalePrice())
                        .active(dto.isActive())
                        .imageFileName(dto.getImageFileName())
                        .defaultVariant(dto.isDefaultVariant())
                        .product(product)
                        .build();

                variants.add(variant);
            }
        }

        product.setVariants(variants);
        Product savedProduct = productRepository.save(product);

        // Обработка на custom полета
        for (int i = 0; i < variants.size(); i++) {
            CreateProductRequest.ProductVariantDto dto = variantDtos.get(i);
            ProductVariant variant = savedProduct.getVariants().get(i);
            if (dto.getCustomFields() != null) {
                productVariantFieldService.createFieldsForVariant(variant, dto.getCustomFields());
            }
        }

        // Запис на изображения през FileStorageService
        assignImages(savedProduct, productImages, variantImages);
    }
}
