package org.lufi.giftstop.service.impl;

import org.lufi.giftstop.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${file.upload-dir}")
    private String baseUploadDir;

    @Override
    public String storeProductFile(Long productId, MultipartFile file) {
        return storeFile(file, "products", productId);
    }

    @Override
    public String storeOrderFile(Long orderId, MultipartFile file) {
        return storeFile(file, "orders", orderId);
    }

    private String storeFile(MultipartFile file, String type, Long id) {
        try {
            Path uploadPath = Paths.get(baseUploadDir, type, id.toString());

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = getExtension(originalFilename);
            String newFileName = UUID.randomUUID() + (extension.isEmpty() ? "" : "." + extension);

            Path filePath = uploadPath.resolve(newFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return newFileName;

        } catch (IOException e) {
            throw new RuntimeException("Грешка при запис на файл", e);
        }
    }

    private String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex > 0) ? fileName.substring(dotIndex + 1) : "";
    }
}
