package org.lufi.giftstop.service;

import org.json.JSONException;
import org.lufi.giftstop.dto.CreateProductRequest;
import org.lufi.giftstop.dto.ProductListDto;
import org.lufi.giftstop.dto.ProductResponse;
import org.lufi.giftstop.model.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    Product createProduct(CreateProductRequest request, List<MultipartFile> productImages, List<MultipartFile> variantParts, List<MultipartFile> variantImages) throws IOException, JSONException;
    List<ProductListDto> getAllForAdmin();
    void updateActiveStatus(Long productId, boolean active);
    void deleteProductById(Long id);
    void updateProduct(Long id,
                  CreateProductRequest request,
                  List<MultipartFile> productImages,
                  List<MultipartFile> variantParts,
                  List<MultipartFile> variantImages) throws IOException;
    ProductResponse getProductById(Long id);

}
