package com.sweetbook.backend.sweetbook.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

public record BookBuildRequest(
        @NotBlank(message = "판형 UID는 필수입니다.")
        String bookSpecUid,

        @NotBlank(message = "표지 템플릿 UID는 필수입니다.")
        String coverTemplateUid,

        @NotBlank(message = "내지 템플릿 UID는 필수입니다.")
        String contentTemplateUid,

        String blankTemplateUid,

        String coverTitle,
        Map<String, Object> extraOptions
) {
}
