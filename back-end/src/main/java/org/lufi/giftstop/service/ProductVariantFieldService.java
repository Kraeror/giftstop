package org.lufi.giftstop.service;

import org.lufi.giftstop.dto.CreateProductRequest;
import org.lufi.giftstop.model.ProductVariant;
import org.lufi.giftstop.model.ProductVariantField;

import java.util.List;

public interface ProductVariantFieldService {
    List<ProductVariantField> createFieldsForVariant(ProductVariant variant, List<CreateProductRequest.ProductVariantDto.ProductVariantFieldDto> fieldDtos);
}
