package com.sweetbook.backend.integration.sweetbook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweetbook.backend.common.exception.ExternalApiException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class SweetBookClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public SweetBookClient(
            @Qualifier("sweetBookRestClient") RestClient restClient,
            ObjectMapper objectMapper
    ) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    public JsonNode getBookSpecs() {
        return get("/book-specs", "SweetBook book spec lookup failed.");
    }

    public JsonNode getTemplates(String bookSpecUid) {
        try {
            String response = restClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/templates")
                            .queryParamIfPresent("bookSpecUid", Optional.ofNullable(bookSpecUid))
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(String.class);
            return parseJson(response, "SweetBook template lookup failed.");
        } catch (RestClientException exception) {
            throw new ExternalApiException("SweetBook template lookup failed.", exception);
        }
    }

    public JsonNode getTemplateDetail(String templateUid) {
        return get("/templates/" + templateUid, "SweetBook template detail lookup failed.");
    }

    public JsonNode getBook(String bookUid) {
        try {
            String response = restClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/books")
                            .queryParam("bookUid", bookUid)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(String.class);
            return parseJson(response, "SweetBook book lookup failed.");
        } catch (RestClientException exception) {
            throw new ExternalApiException("SweetBook book lookup failed.", exception);
        }
    }

    public JsonNode createBook(Map<String, Object> payload) {
        return postJson("/books", payload, "SweetBook book creation failed.");
    }

    public JsonNode uploadPhoto(String bookUid, String filename, String contentType, byte[] bytes) {
        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(contentType == null || contentType.isBlank()
                    ? MediaType.APPLICATION_OCTET_STREAM
                    : MediaType.parseMediaType(contentType));
            headers.setContentDisposition(ContentDisposition.builder("form-data")
                    .name("file")
                    .filename(filename)
                    .build());

            body.add("file", new HttpEntity<>(new NamedByteArrayResource(bytes, filename), headers));

            String response = restClient.post()
                    .uri("/books/" + bookUid + "/photos")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .body(String.class);
            return parseJson(response, "SweetBook photo upload failed.");
        } catch (RestClientException exception) {
            throw new ExternalApiException("SweetBook photo upload failed.", exception);
        }
    }

    public JsonNode createCover(String bookUid, String templateUid, Map<String, Object> parameters) {
        return postMultipart(
                "/books/" + bookUid + "/cover",
                templateUid,
                parameters,
                null,
                "SweetBook cover creation failed."
        );
    }

    public JsonNode addContents(String bookUid, String templateUid, Map<String, Object> parameters, String breakBefore) {
        Map<String, Object> query = new HashMap<>();
        if (breakBefore != null && !breakBefore.isBlank()) {
            query.put("breakBefore", breakBefore);
        }

        return postMultipart(
                "/books/" + bookUid + "/contents",
                templateUid,
                parameters,
                query,
                "SweetBook content insertion failed."
        );
    }

    public JsonNode finalizeBook(String bookUid) {
        return postJson("/books/" + bookUid + "/finalization", Map.of(), "SweetBook finalization failed.");
    }

    public JsonNode estimateOrder(Map<String, Object> payload) {
        return postJson("/orders/estimate", payload, "SweetBook order estimate failed.");
    }

    public JsonNode createOrder(Map<String, Object> payload) {
        return postJson("/orders", payload, "SweetBook order creation failed.");
    }

    public JsonNode getOrder(String orderUid) {
        return get("/orders/" + orderUid, "SweetBook order lookup failed.");
    }

    public JsonNode cancelOrder(String orderUid) {
        try {
            restClient.post()
                    .uri("/orders/" + orderUid + "/cancel")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{}")
                    .retrieve()
                    .toBodilessEntity();
            return objectMapper.createObjectNode();
        } catch (RestClientException exception) {
            throw new ExternalApiException("SweetBook order cancellation failed.", exception);
        }
    }

    private JsonNode get(String path, String message) {
        try {
            String response = restClient.get()
                    .uri(path)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(String.class);
            return parseJson(response, message);
        } catch (RestClientException exception) {
            throw new ExternalApiException(message, exception);
        }
    }

    private JsonNode postJson(String path, Map<String, Object> payload, String message) {
        try {
            String response = restClient.post()
                    .uri(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(String.class);
            return parseJson(response, message);
        } catch (RestClientException exception) {
            throw new ExternalApiException(message, exception);
        }
    }

    private JsonNode postMultipart(
            String path,
            String templateUid,
            Map<String, Object> parameters,
            Map<String, Object> query,
            String message
    ) {
        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("templateUid", templateUid);
            body.add("parameters", objectMapper.writeValueAsString(parameters));

            String response = restClient.post()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path(path);
                        if (query != null) {
                            query.forEach(builder::queryParam);
                        }
                        return builder.build();
                    })
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .body(String.class);
            return parseJson(response, message);
        } catch (ExternalApiException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new ExternalApiException(message, exception);
        }
    }

    private JsonNode parseJson(String response, String message) {
        try {
            return objectMapper.readTree(response);
        } catch (Exception exception) {
            throw new ExternalApiException(message, exception);
        }
    }

    private static final class NamedByteArrayResource extends ByteArrayResource {
        private final String filename;

        private NamedByteArrayResource(byte[] byteArray, String filename) {
            super(byteArray);
            this.filename = filename;
        }

        @Override
        public String getFilename() {
            return filename;
        }
    }
}
