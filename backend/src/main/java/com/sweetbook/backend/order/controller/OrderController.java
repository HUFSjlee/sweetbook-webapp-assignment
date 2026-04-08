package com.sweetbook.backend.order.controller;

import com.sweetbook.backend.common.ApiResponse;
import com.sweetbook.backend.order.dto.OrderCreateRequest;
import com.sweetbook.backend.order.dto.OrderEstimateRequest;
import com.sweetbook.backend.order.dto.OrderEstimateResponse;
import com.sweetbook.backend.order.dto.OrderResponse;
import com.sweetbook.backend.order.service.OrderService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/travels/{travelId}/estimate")
    public ApiResponse<OrderEstimateResponse> estimate(
            @PathVariable UUID travelId,
            @RequestBody(required = false) OrderEstimateRequest request
    ) {
        OrderEstimateRequest actualRequest = request == null ? new OrderEstimateRequest(null) : request;
        return ApiResponse.ok(orderService.estimate(travelId, actualRequest));
    }

    @PostMapping("/travels/{travelId}/order")
    public ApiResponse<OrderResponse> create(
            @PathVariable UUID travelId,
            @Valid @RequestBody OrderCreateRequest request
    ) {
        return ApiResponse.ok(orderService.create(travelId, request));
    }

    @GetMapping("/orders")
    public ApiResponse<List<OrderResponse>> findAll() {
        return ApiResponse.ok(orderService.findAll());
    }

    @GetMapping("/orders/{orderId}")
    public ApiResponse<Map<String, Object>> findDetail(@PathVariable UUID orderId) {
        return ApiResponse.ok(orderService.findDetail(orderId));
    }

    @PostMapping("/orders/{orderId}/cancel")
    public ApiResponse<OrderResponse> cancel(@PathVariable UUID orderId) {
        return ApiResponse.ok(orderService.cancel(orderId));
    }
}
