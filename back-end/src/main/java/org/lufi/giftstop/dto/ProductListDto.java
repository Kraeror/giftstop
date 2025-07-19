package org.lufi.giftstop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.lufi.giftstop.model.Product;
import org.lufi.giftstop.model.ProductVariant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductListDto {
    private Long id;
    private boolean active;
    private String title;
    private String imageFileName;
    private String price;
    private String salePrice;
    public static ProductListDto from(Product product) {
        ProductVariant defaultVariant = product.getVariants().stream()
                .filter(ProductVariant::isDefaultVariant)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Product without default variant: " + product.getId()));

        return ProductListDto.builder()
                .id(product.getId())
                .title(product.getTitle())
                .imageFileName(product.getImages().getFirst().getFileName())
                .price(defaultVariant.getPrice().toPlainString())
                .salePrice(defaultVariant.getSalePrice() != null ? defaultVariant.getSalePrice().toPlainString() : null)
                .active(defaultVariant.isActive())
                .build();
    }
}
