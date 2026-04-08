package com.sweetbook.backend.integration.sweetbook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweetbook.backend.common.exception.ExternalApiException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.http.MediaType;
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
        return get("/book-specs");
    }

    public JsonNode getTemplates(String bookSpecUid) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/templates")
                            .queryParamIfPresent("bookSpecUid", java.util.Optional.ofNullable(bookSpecUid))
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientException exception) {
            throw new ExternalApiException("SweetBook 템플릿 조회에 실패했습니다.", exception);
        }
    }

    public JsonNode getTemplateDetail(String templateUid) {
        return get("/templates/" + templateUid);
    }

    public JsonNode createBook(Map<String, Object> payload) {
        return post("/books", payload, "SweetBook 책 생성에 실패했습니다.");
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

            return restClient.post()
                    .uri("/books/" + bookUid + "/photos")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientException exception) {
            throw new ExternalApiException("SweetBook 사진 업로드에 실패했습니다.", exception);
        }
    }

    public JsonNode createCover(String bookUid, String templateUid, Map<String, Object> parameters) {
        return postMultipart("/books/" + bookUid + "/cover", templateUid, parameters, null, "SweetBook 표지 생성에 실패했습니다.");
    }

    public JsonNode addContents(String bookUid, String templateUid, Map<String, Object> parameters, String breakBefore) {
        Map<String, Object> query = new HashMap<>();
        if (breakBefore != null && !breakBefore.isBlank()) {
            query.put("breakBefore", breakBefore);
        }
        return postMultipart("/books/" + bookUid + "/contents", templateUid, parameters, query, "SweetBook 내지 생성에 실패했습니다.");
    }

    public JsonNode finalizeBook(String bookUid) {
        return post("/books/" + bookUid + "/finalization", Map.of(), "SweetBook 최종화에 실패했습니다.");
    }

    public JsonNode estimateOrder(Map<String, Object> payload) {
        return post("/orders/estimate", payload, "SweetBook 주문 견적 조회에 실패했습니다.");
    }

    public JsonNode createOrder(Map<String, Object> payload) {
        return post("/orders", payload, "SweetBook 주문 생성에 실패했습니다.");
    }

    public JsonNode getOrder(String orderUid) {
        return get("/orders/" + orderUid);
    }

    public JsonNode cancelOrder(String orderUid) {
        return post("/orders/" + orderUid + "/cancel", Map.of(), "SweetBook 주문 취소에 실패했습니다.");
    }

    private JsonNode get(String path) {
        try {
            return restClient.get()
                    .uri(path)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientException exception) {
            throw new ExternalApiException("SweetBook API 호출에 실패했습니다.", exception);
        }
    }

    private JsonNode post(String path, Map<String, Object> payload, String message) {
        try {
            return restClient.post()
                    .uri(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(JsonNode.class);
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

            return restClient.post()
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
                    .body(JsonNode.class);
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
