package com.sweetbook.backend.photo.dto;

import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record PhotoUpdateRequest(
        @Size(max = 500, message = "코멘트는 500자 이하여야 합니다.")
        String comment,

        @Size(max = 150, message = "장소는 150자 이하여야 합니다.")
        String location,
        LocalDate takenDate
) {
}
