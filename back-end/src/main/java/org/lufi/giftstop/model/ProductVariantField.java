package org.lufi.giftstop.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;       // Име на полето, напр. „Цвят“
    private String type;       // Тип на полето: Текст, Число, Цвят, Падащо меню и т.н.

    @Column(length = 3000)
    private String itemsJson;  // JSON масив с елементи (ако е dropdown/цветове)

    @ManyToOne
    @JoinColumn(name = "variant_id")
    private ProductVariant variant;
}
