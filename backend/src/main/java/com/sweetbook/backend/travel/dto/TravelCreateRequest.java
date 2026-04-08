package com.sweetbook.backend.travel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record TravelCreateRequest(
        @NotBlank(message = "여행 제목은 필수입니다.")
        @Size(max = 100, message = "여행 제목은 100자 이하여야 합니다.")
        String title,

        @Size(max = 300, message = "소개는 300자 이하여야 합니다.")
        String description,

        LocalDate startDate,
        LocalDate endDate,

        @Size(max = 50, message = "분위기 값이 너무 깁니다.")
        String mood
) {
}
