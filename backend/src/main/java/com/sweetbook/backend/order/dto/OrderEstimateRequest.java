package com.sweetbook.backend.order.dto;

import java.util.Map;

public record OrderEstimateRequest(
        Map<String, Object> extraOptions
) {
}
