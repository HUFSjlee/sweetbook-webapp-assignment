package com.sweetbook.backend.photo.controller;

import com.sweetbook.backend.common.ApiResponse;
import com.sweetbook.backend.photo.dto.PhotoReorderItemRequest;
import com.sweetbook.backend.photo.dto.PhotoResponse;
import com.sweetbook.backend.photo.dto.PhotoUpdateRequest;
import com.sweetbook.backend.photo.service.PhotoService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;

    @PostMapping("/api/travels/{travelId}/photos")
    public ApiResponse<PhotoResponse> upload(
            @PathVariable UUID travelId,
            @RequestPart("file") MultipartFile file,
            @RequestParam(required = false) String comment,
            @RequestParam(required = false) String location
    ) {
        return ApiResponse.ok(photoService.upload(travelId, file, comment, location));
    }

    @GetMapping("/api/travels/{travelId}/photos")
    public ApiResponse<List<PhotoResponse>> findByTravel(@PathVariable UUID travelId) {
        return ApiResponse.ok(photoService.findByTravel(travelId));
    }

    @PutMapping("/api/photos/{photoId}")
    public ApiResponse<PhotoResponse> update(
            @PathVariable UUID photoId,
            @Valid @RequestBody PhotoUpdateRequest request
    ) {
        return ApiResponse.ok(photoService.update(photoId, request));
    }

    @DeleteMapping("/api/photos/{photoId}")
    public ApiResponse<Void> delete(@PathVariable UUID photoId) {
        photoService.delete(photoId);
        return ApiResponse.ok("사진을 삭제했습니다.");
    }

    @PutMapping("/api/travels/{travelId}/photos/reorder")
    public ApiResponse<List<PhotoResponse>> reorder(
            @PathVariable UUID travelId,
            @Valid @RequestBody List<PhotoReorderItemRequest> request
    ) {
        return ApiResponse.ok(photoService.reorder(travelId, request));
    }
}
