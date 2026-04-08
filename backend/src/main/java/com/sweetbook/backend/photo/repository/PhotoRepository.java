package com.sweetbook.backend.photo.repository;

import com.sweetbook.backend.photo.entity.Photo;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo, UUID> {

    List<Photo> findByTravelIdOrderBySortOrderAscCreatedAtAsc(UUID travelId);

    Optional<Photo> findTopByTravelIdOrderBySortOrderDesc(UUID travelId);
}
