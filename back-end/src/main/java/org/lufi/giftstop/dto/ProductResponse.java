package org.lufi.giftstop.dto;

import lombok.Data;
import org.lufi.giftstop.model.Product;
import org.lufi.giftstop.model.ProductVariant;
import org.lufi.giftstop.model.Category;
import org.lufi.giftstop.model.ProductVariantField;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductResponse {
    private Long id;
    private String title;
    private String description;
    private String slug;
    private List<Long> categoryIds;
    private List<String> imageFileNames;
    private List<ProductVariantDto> variants;

    public static ProductResponse fromEntity(Product product) {
        ProductResponse dto = new ProductResponse();
        dto.setId(product.getId());
        dto.setTitle(product.getTitle());
        dto.setDescription(product.getDescription());
        dto.setSlug(product.getSlug());
        dto.setImageFileNames(product.getImageFileNames());
        dto.setVariants(product.getVariants().stream().map(ProductVariantDto::fromEntity).toList());
        dto.setCategoryIds(
                product.getCategories().stream()
                        .map(Category::getId)

                        .toList()
        );
        return dto;
    }

    @Data
    public static class ProductVariantDto {
        private String title;
        private BigDecimal price;
        private BigDecimal salePrice;
        private boolean active;
        private String imageFileName;
        private boolean defaultVariant;
        private List<CustomFieldDto> customFields;

        public static ProductVariantDto fromEntity(ProductVariant variant) {
            ProductVariantDto dto = new ProductVariantDto();
            dto.setTitle(variant.getName());
            dto.setPrice(variant.getPrice());
            dto.setSalePrice(variant.getSalePrice());
            dto.setActive(variant.isActive());
            dto.setImageFileName(variant.getImageFileName());
            dto.setDefaultVariant(variant.isDefaultVariant()); // отговаря на новото поле в модела
            dto.setCustomFields(
                    variant.getCustomFields().stream()
                            .map(CustomFieldDto::fromEntity)
                            .toList()
            );
            return dto;
        }
    }
    @Data
    public static class CustomFieldDto {
        private String type;
        private String name;
        private String items;

        public static CustomFieldDto fromEntity(ProductVariantField field) {
            CustomFieldDto dto = new CustomFieldDto();
            dto.setType(field.getType());
            dto.setName(field.getName());
            dto.setItems(field.getItemsJson());
            return dto;
        }
    }
}
