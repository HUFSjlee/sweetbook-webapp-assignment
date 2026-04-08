package com.sweetbook.backend.photo.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record PhotoReorderItemRequest(
        @NotNull(message = "사진 ID는 필수입니다.")
        UUID photoId,

        @NotNull(message = "정렬 순서는 필수입니다.")
        Integer sortOrder
) {
}
