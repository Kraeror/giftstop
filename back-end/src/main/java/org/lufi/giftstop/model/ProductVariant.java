package org.lufi.giftstop.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private BigDecimal price;
    private BigDecimal salePrice;
    private boolean active;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private String imageFileName;
}
