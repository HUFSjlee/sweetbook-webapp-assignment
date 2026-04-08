package com.sweetbook.backend.sweetbook.controller;

import com.sweetbook.backend.common.ApiResponse;
import com.sweetbook.backend.sweetbook.dto.BookBuildRequest;
import com.sweetbook.backend.sweetbook.service.BookBuildService;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SweetBookController {

    private final BookBuildService bookBuildService;

    @GetMapping("/bookspecs")
    public ResponseEntity<String> getBookSpecs() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"success\":true,\"data\":" + bookBuildService.getBookSpecsJson() + ",\"message\":null}");
    }

    @GetMapping("/templates")
    public ResponseEntity<String> getTemplates(@RequestParam(required = false) String bookSpecUid) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"success\":true,\"data\":" + bookBuildService.getTemplatesJson(bookSpecUid) + ",\"message\":null}");
    }

    @PostMapping("/travels/{travelId}/build")
    public ApiResponse<Map<String, Object>> build(
            @PathVariable UUID travelId,
            @Valid @RequestBody BookBuildRequest request
    ) {
        return ApiResponse.ok(bookBuildService.build(travelId, request));
    }
}
