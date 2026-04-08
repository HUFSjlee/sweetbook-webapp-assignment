package com.sweetbook.backend.order.dto;

public record OrderEstimateResponse(
        String travelId,
        Integer estimatedPrice,
        Object rawResponse
) {
}
