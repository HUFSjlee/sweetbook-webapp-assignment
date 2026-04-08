package com.sweetbook.backend.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

public record OrderCreateRequest(
        @NotBlank(message = "수령인 이름은 필수입니다.")
        String recipientName,

        @NotBlank(message = "주소는 필수입니다.")
        String address1,

        String address2,

        @NotBlank(message = "연락처는 필수입니다.")
        String recipientPhone,

        @NotBlank(message = "우편번호는 필수입니다.")
        String postalCode,

        String memo,

        @NotNull(message = "견적 금액은 필수입니다.")
        Integer estimatedPrice,

        Map<String, Object> extraOptions
) {
}
