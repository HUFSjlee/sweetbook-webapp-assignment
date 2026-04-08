package com.sweetbook.backend.integration.supabase;

import com.sweetbook.backend.common.exception.BadRequestException;
import com.sweetbook.backend.common.exception.ExternalApiException;
import com.sweetbook.backend.config.SupabaseProperties;
import com.sweetbook.backend.storage.StorageService;
import java.io.IOException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

@Service
@ConditionalOnProperty(name = "app.storage.mode", havingValue = "supabase")
public class SupabaseStorageService implements StorageService {

    private final RestClient restClient;
    private final SupabaseProperties properties;

    public SupabaseStorageService(
            @Qualifier("supabaseRestClient") RestClient restClient,
            SupabaseProperties properties
    ) {
        this.restClient = restClient;
        this.properties = properties;
    }

    @Override
    public StoredFile store(String folder, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new BadRequestException("업로드할 사진이 비어 있습니다.");
        }

        return storeBytes(folder, file.getOriginalFilename(), file.getContentType(), file.getBytes());
    }

    @Override
    public StoredFile storeBytes(String folder, String filename, String contentType, byte[] bytes) {
        String extension = extractExtension(filename);
        String storagePath = folder + "/" + UUID.randomUUID() + extension;

        try {
            restClient.post()
                    .uri("/storage/v1/object/{bucket}/{path}", properties.getStorageBucket(), storagePath)
                    .header("x-upsert", "true")
                    .contentType(resolveMediaType(contentType))
                    .body(bytes)
                    .retrieve()
                    .toBodilessEntity();

            return new StoredFile(
                    storagePath,
                    buildPublicUrl(storagePath),
                    contentType,
                    bytes.length
            );
        } catch (RestClientException exception) {
            throw new ExternalApiException("Supabase Storage 업로드에 실패했습니다.", exception);
        }
    }

    @Override
    public byte[] readBytes(String storagePath) {
        try {
            return restClient.get()
                    .uri("/storage/v1/object/{bucket}/{path}", properties.getStorageBucket(), storagePath)
                    .retrieve()
                    .body(byte[].class);
        } catch (RestClientException exception) {
            throw new ExternalApiException("Supabase Storage 파일 조회에 실패했습니다.", exception);
        }
    }

    @Override
    public void delete(String storagePath) {
        try {
            restClient.delete()
                    .uri("/storage/v1/object/{bucket}/{path}", properties.getStorageBucket(), storagePath)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException exception) {
            throw new ExternalApiException("Supabase Storage 파일 삭제에 실패했습니다.", exception);
        }
    }

    private String extractExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return ".bin";
        }
        return originalFilename.substring(originalFilename.lastIndexOf('.'));
    }

    private MediaType resolveMediaType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
        return MediaType.parseMediaType(contentType);
    }

    private String buildPublicUrl(String storagePath) {
        return properties.getUrl() + "/storage/v1/object/public/" + properties.getStorageBucket() + "/" + storagePath;
    }
}
