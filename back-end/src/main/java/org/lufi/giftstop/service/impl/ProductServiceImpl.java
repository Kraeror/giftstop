package org.lufi.giftstop.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.lufi.giftstop.dto.ProductListDto;
import org.lufi.giftstop.dto.ProductResponse;
import org.lufi.giftstop.model.Image;
import org.lufi.giftstop.model.Product;
import org.lufi.giftstop.model.ProductVariant;
import org.lufi.giftstop.repo.ImageRepository;
import org.lufi.giftstop.repo.ProductRepository;
import org.lufi.giftstop.service.FileStorageService;
import org.lufi.giftstop.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public void assignImages(Product product, List<MultipartFile> productImages, List<MultipartFile> variantImages) {
        List<Image> images = new ArrayList<>();

        if (productImages != null) {
            for (MultipartFile file : productImages) {
                String path = fileStorageService.storeProductFile(product.getId(), file);
                images.add(Image.builder().fileName(path).product(product).build());
            }
        }

        if (variantImages != null && !product.getVariants().isEmpty()) {
            for (int i = 0; i < variantImages.size(); i++) {
                MultipartFile file = variantImages.get(i);
                if (file != null && i < product.getVariants().size()) {
                    String path = fileStorageService.storeProductFile(product.getId(), file);
                    ProductVariant variant = product.getVariants().get(i);
                    variant.setImageFileName(path);
                }
            }
        }

        imageRepository.saveAll(images);
    }

    public List<ProductListDto> getAllForAdmin() {
        return productRepository.findAll().stream().map(product -> {
            String imageFileName = null;

            if (!product.getVariants().isEmpty()) {
                imageFileName = product.getVariants().getFirst().getImageFileName();
            }

            if (imageFileName == null && !product.getImages().isEmpty()) {
                imageFileName = product.getImages().getFirst().getFileName();
            }

            return ProductListDto.builder()
                    .id(product.getId())
                    .title(product.getTitle())
                    .imageFileName(imageFileName)
                    .price(product.getPrice().toString())
                    .salePrice(product.getSalePrice().toString())
                    .active(product.isActive())
                    .build();
        }).toList();
    }

    @Override
    @Transactional
    public void updateActiveStatus(Long productId, boolean active) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Продуктът не е намерен"));

        product.setActive(active);
        productRepository.save(product);
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


    /*@Override
public void updateProduct(Long id, CreateProductRequest dto, List<MultipartFile> images,
                          List<CreateProductRequest.ProductVariantDto> variantsDto, List<MultipartFile> variantImages) {

    Product product = productRepo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Продуктът не е намерен"));

    product.setTitle(dto.getTitle());
    product.setDescription(dto.getDescription());
    product.setSlug(dto.getSlug());
    product.setPrice(dto.getPrice());
    product.setSalePrice(dto.getSalePrice());
    product.setActive(dto.isActive());

    // Категории
    List<Category> categories = categoryRepo.findAllById(dto.getCategoryIds());
    List<Subcategory> subcategories = subcategoryRepo.findAllById(dto.getSubcategoryIds());
    product.setCategories(new HashSet<>(categories));
    product.setSubcategories(new HashSet<>(subcategories));

    // Изтриваме старите варианти (ако не искаш това – добави логика за merge/update)
    productVariantRepo.deleteAllByProduct(product);

    List<ProductVariant> newVariants = new ArrayList<>();
    for (int i = 0; i < variantsDto.size(); i++) {
        CreateProductRequest.ProductVariantDto variantDto = variantsDto.get(i);
        ProductVariant variant = new ProductVariant();
        variant.setTitle(variantDto.getTitle());
        variant.setPrice(variantDto.getPrice());
        variant.setSalePrice(variantDto.getSalePrice());
        variant.setActive(variantDto.isActive());
        variant.setProduct(product);

        // Снимка на варианта
        if (variantImages != null && i < variantImages.size()) {
            MultipartFile file = variantImages.get(i);
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path variantPath = Path.of(uploadDir, "products", String.valueOf(id));
            Files.createDirectories(variantPath);
            file.transferTo(variantPath.resolve(filename));
            variant.setImageFileName(filename);
        }

        newVariants.add(variant);
    }
    product.setVariants(newVariants);

    // Снимки на продукта
    if (images != null) {
        for (MultipartFile file : images) {
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Path.of(uploadDir, "products", String.valueOf(id));
            Files.createDirectories(path);
            file.transferTo(path.resolve(filename));
            // Запиши в product.getImageFileNames() или както съхраняваш
            product.getImageFileNames().add(filename);
        }
    }

    productRepo.save(product);
}
*/
}
