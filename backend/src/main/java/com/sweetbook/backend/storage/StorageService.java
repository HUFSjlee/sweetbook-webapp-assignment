package com.sweetbook.backend.storage;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    StoredFile store(String folder, MultipartFile file) throws IOException;

    StoredFile storeBytes(String folder, String filename, String contentType, byte[] bytes) throws IOException;

    byte[] readBytes(String storagePath) throws IOException;

    void delete(String storagePath) throws IOException;

    record StoredFile(
            String storagePath,
            String publicUrl,
            String contentType,
            long size
    ) {
    }
}
