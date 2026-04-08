package com.sweetbook.backend.storage;

import com.sweetbook.backend.config.StorageProperties;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@ConditionalOnProperty(name = "app.storage.mode", havingValue = "local", matchIfMissing = true)
public class LocalStorageService implements StorageService {

    private final Path rootPath;
    private final String publicBaseUrl;

    public LocalStorageService(
            StorageProperties properties,
            @Value("${app.public-base-url}") String publicBaseUrl
    ) throws IOException {
        this.rootPath = Paths.get(properties.getLocalRoot()).toAbsolutePath().normalize();
        this.publicBaseUrl = publicBaseUrl;
        Files.createDirectories(this.rootPath);
    }

    @Override
    public StoredFile store(String folder, MultipartFile file) throws IOException {
        return storeBytes(folder, file.getOriginalFilename(), file.getContentType(), file.getBytes());
    }

    @Override
    public StoredFile storeBytes(String folder, String filename, String contentType, byte[] bytes) throws IOException {
        String extension = extensionOf(filename);
        Path folderPath = rootPath.resolve(folder).normalize();
        Files.createDirectories(folderPath);

        String generatedName = UUID.randomUUID() + extension;
        Path targetPath = folderPath.resolve(generatedName).normalize();
        Files.write(targetPath, bytes);

        String relativePath = rootPath.relativize(targetPath).toString().replace('\\', '/');
        return new StoredFile(
                relativePath,
                publicBaseUrl + "/uploads/" + relativePath,
                contentType,
                bytes.length
        );
    }

    @Override
    public byte[] readBytes(String storagePath) throws IOException {
        return Files.readAllBytes(resolve(storagePath));
    }

    @Override
    public void delete(String storagePath) throws IOException {
        Files.deleteIfExists(resolve(storagePath));
    }

    private Path resolve(String storagePath) {
        return rootPath.resolve(storagePath).normalize();
    }

    private String extensionOf(String filename) {
        if (filename == null || !filename.contains(".")) {
            return ".bin";
        }
        return filename.substring(filename.lastIndexOf('.'));
    }
}
