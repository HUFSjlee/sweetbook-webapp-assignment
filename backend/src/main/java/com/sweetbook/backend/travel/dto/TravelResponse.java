package com.sweetbook.backend.travel.dto;

import com.sweetbook.backend.travel.entity.Travel;
import com.sweetbook.backend.travel.entity.TravelStatus;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record TravelResponse(
        UUID id,
        String title,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        String mood,
        TravelStatus status,
        String bookUid,
        String coverImageUrl,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static TravelResponse from(Travel travel) {
        return new TravelResponse(
                travel.getId(),
                travel.getTitle(),
                travel.getDescription(),
                travel.getStartDate(),
                travel.getEndDate(),
                travel.getMood(),
                travel.getStatus(),
                travel.getBookUid(),
                travel.getCoverImageUrl(),
                travel.getCreatedAt(),
                travel.getUpdatedAt()
        );
    }
}
