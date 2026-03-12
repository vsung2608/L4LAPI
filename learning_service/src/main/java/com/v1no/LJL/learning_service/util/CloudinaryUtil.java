package com.v1no.LJL.learning_service.util;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.v1no.LJL.learning_service.model.dto.response.FileUploadResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CloudinaryUtil {
     private final Cloudinary cloudinary;

    public FileUploadResponse uploadImage(MultipartFile file) throws IOException {
        return uploadFile(file.getInputStream(), "image", "images", true);
    }

    public FileUploadResponse uploadImage(byte[] fileBytes) throws IOException {
        validateBytes(fileBytes);
        return uploadFile(fileBytes, "image", "images", true);
    }

    private FileUploadResponse uploadFile(Object fileData, String resourceType,
                                          String folder, boolean optimize) throws IOException {
        Map<String, Object> params = ObjectUtils.asMap(
                "resource_type", resourceType,
                "folder", folder
        );

        if (optimize) {
            params.put("quality", "auto:good");
            params.put("fetch_format", "auto");
        }

        Map<?, ?> uploadResult = cloudinary.uploader().upload(fileData, params);
        return mapToResponse(uploadResult);
    }

    public void deleteFile(String publicId, String resourceType) throws IOException {
        Map<?, ?> deleteResult = cloudinary.uploader().destroy(publicId,
                ObjectUtils.asMap("resource_type", resourceType));

        if (!"ok".equals(String.valueOf(deleteResult.get("result")))) {
            log.error("Failed to delete file. PublicId: {}, Result: {}", publicId, deleteResult);
            throw new IOException("Failed to delete file with publicId: " + publicId);
        }
    }

    private void validateBytes(byte[] fileBytes) throws IOException {
        if (fileBytes == null || fileBytes.length == 0) {
            throw new IOException("File bytes is empty or null");
        }
    }

    private FileUploadResponse mapToResponse(Map<?, ?> uploadResult) {
        Object secureUrl = uploadResult.get("secure_url");
        Object publicId = uploadResult.get("public_id");

        if (secureUrl == null || publicId == null) {
            log.error("Missing required fields in upload result: {}", uploadResult);
            throw new RuntimeException("Upload failed: missing secure_url or public_id");
        }

        return new FileUploadResponse(secureUrl.toString(), publicId.toString());
    }
}
