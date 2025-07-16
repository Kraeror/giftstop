package org.lufi.giftstop.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storeProductFile(Long productId, MultipartFile file);
    String storeOrderFile(Long orderId, MultipartFile file);
}