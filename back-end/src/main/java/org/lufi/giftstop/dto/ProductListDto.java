package org.lufi.giftstop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ProductListDto {
    private Long id;
    private String title;
    private String imageFileName;
    private String price;
    private String salePrice;
    private boolean active;
}
