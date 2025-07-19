package org.lufi.giftstop.service.impl;

import lombok.RequiredArgsConstructor;
import org.lufi.giftstop.dto.CreateProductRequest;
import org.lufi.giftstop.model.ProductVariant;
import org.lufi.giftstop.model.ProductVariantField;
import org.lufi.giftstop.repo.ProductVariantFieldRepository;
import org.lufi.giftstop.service.ProductVariantFieldService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductVariantFieldServiceImpl implements ProductVariantFieldService {

    private final ProductVariantFieldRepository fieldRepository;

    @Override
    public List<ProductVariantField> createFieldsForVariant(
            ProductVariant variant,
            List<CreateProductRequest.ProductVariantDto.ProductVariantFieldDto> fieldDtos) {

        List<ProductVariantField> result = new ArrayList<>();

        for (CreateProductRequest.ProductVariantDto.ProductVariantFieldDto dto : fieldDtos) {
            ProductVariantField field = ProductVariantField.builder()
                    .name(dto.getName())
                    .type(dto.getType())
                    .itemsJson(dto.getItemsJson())
                    .variant(variant)
                    .build();
            result.add(field);
        }

        return fieldRepository.saveAll(result);
    }
}
