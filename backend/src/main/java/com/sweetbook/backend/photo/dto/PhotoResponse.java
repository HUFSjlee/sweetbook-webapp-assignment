package com.sweetbook.backend.photo.dto;

import com.sweetbook.backend.photo.entity.Photo;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record PhotoResponse(
        UUID id,
        UUID travelId,
        String imageUrl,
        String storagePath,
        String comment,
        String location,
        LocalDate takenDate,
        Integer sortOrder,
        OffsetDateTime createdAt
) {
    public static PhotoResponse from(Photo photo) {
        return new PhotoResponse(
                photo.getId(),
                photo.getTravel().getId(),
                photo.getImageUrl(),
                photo.getStoragePath(),
                photo.getComment(),
                photo.getLocation(),
                photo.getTakenDate(),
                photo.getSortOrder(),
                photo.getCreatedAt()
        );
    }
}
