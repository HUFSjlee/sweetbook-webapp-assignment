package com.sweetbook.backend.travel.controller;

import com.sweetbook.backend.common.ApiResponse;
import com.sweetbook.backend.travel.dto.TravelCreateRequest;
import com.sweetbook.backend.travel.dto.TravelResponse;
import com.sweetbook.backend.travel.dto.TravelUpdateRequest;
import com.sweetbook.backend.travel.service.TravelService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/travels")
public class TravelController {

    private final TravelService travelService;

    @PostMapping
    public ApiResponse<TravelResponse> create(@Valid @RequestBody TravelCreateRequest request) {
        return ApiResponse.ok(travelService.create(request));
    }

    @GetMapping
    public ApiResponse<List<TravelResponse>> findAll() {
        return ApiResponse.ok(travelService.findAll());
    }

    @GetMapping("/{travelId}")
    public ApiResponse<TravelResponse> findById(@PathVariable UUID travelId) {
        return ApiResponse.ok(travelService.findById(travelId));
    }

    @PutMapping("/{travelId}")
    public ApiResponse<TravelResponse> update(
            @PathVariable UUID travelId,
            @Valid @RequestBody TravelUpdateRequest request
    ) {
        return ApiResponse.ok(travelService.update(travelId, request));
    }

    @DeleteMapping("/{travelId}")
    public ApiResponse<Void> delete(@PathVariable UUID travelId) {
        travelService.delete(travelId);
        return ApiResponse.ok("여행 프로젝트를 삭제했습니다.");
    }
}
