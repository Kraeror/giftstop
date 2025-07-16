package org.lufi.giftstop.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductRequest {

    private String title;
    private String description;
    private String slug;
    private BigDecimal price;
    private BigDecimal salePrice;
    private boolean active;

    private List<Long> categoryIds;

    private List<String> imageFileNames;

    private List<ProductVariantDto> variants;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductVariantDto {
        private String title;
        private BigDecimal price;
        private BigDecimal salePrice;
        private boolean active;
        private String imageFileName;
    }
}