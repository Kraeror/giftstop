package org.lufi.giftstop.service;

import org.lufi.giftstop.dto.ProductListDto;
import org.lufi.giftstop.dto.ProductResponse;
import org.lufi.giftstop.model.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    Product createProduct(Product product);
    void assignImages(Product product, List<MultipartFile> productImages, List<MultipartFile> variantImages);
    List<ProductListDto> getAllForAdmin();
    void updateActiveStatus(Long productId, boolean active);
    void deleteProductById(Long id);
    /*void updateProduct(Long id, CreateProductRequest dto, List<MultipartFile> images,
                       List<CreateProductRequest.ProductVariantDto> variants, List<MultipartFile> variantImages);*/
    ProductResponse getProductById(Long id);

}
