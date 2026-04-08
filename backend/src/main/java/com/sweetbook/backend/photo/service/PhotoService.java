package com.sweetbook.backend.photo.service;

import com.sweetbook.backend.common.exception.NotFoundException;
import com.sweetbook.backend.photo.dto.PhotoReorderItemRequest;
import com.sweetbook.backend.photo.dto.PhotoResponse;
import com.sweetbook.backend.photo.dto.PhotoUpdateRequest;
import com.sweetbook.backend.photo.entity.Photo;
import com.sweetbook.backend.photo.repository.PhotoRepository;
import com.sweetbook.backend.storage.StorageService;
import com.sweetbook.backend.travel.entity.Travel;
import com.sweetbook.backend.travel.service.TravelService;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final TravelService travelService;
    private final StorageService storageService;

    @Transactional
    public PhotoResponse upload(UUID travelId, MultipartFile file, String comment, String location) {
        Travel travel = travelService.getEntity(travelId);
        StorageService.StoredFile storedFile;
        try {
            storedFile = storageService.store("travels/" + travelId, file);
        } catch (IOException exception) {
            throw new RuntimeException("사진 저장에 실패했습니다.", exception);
        }

        int nextSortOrder = photoRepository.findTopByTravelIdOrderBySortOrderDesc(travelId)
                .map(photo -> photo.getSortOrder() + 1)
                .orElse(1);

        Photo photo = new Photo();
        photo.setTravel(travel);
        photo.setImageUrl(storedFile.publicUrl());
        photo.setStoragePath(storedFile.storagePath());
        photo.setComment(comment);
        photo.setLocation(location);
        photo.setSortOrder(nextSortOrder);

        return PhotoResponse.from(photoRepository.save(photo));
    }

    public List<PhotoResponse> findByTravel(UUID travelId) {
        travelService.getEntity(travelId);
        return photoRepository.findByTravelIdOrderBySortOrderAscCreatedAtAsc(travelId).stream()
                .map(PhotoResponse::from)
                .toList();
    }

    @Transactional
    public PhotoResponse update(UUID photoId, PhotoUpdateRequest request) {
        Photo photo = getEntity(photoId);
        photo.setComment(request.comment());
        photo.setLocation(request.location());
        photo.setTakenDate(request.takenDate());
        return PhotoResponse.from(photo);
    }

    @Transactional
    public void delete(UUID photoId) {
        Photo photo = getEntity(photoId);
        try {
            storageService.delete(photo.getStoragePath());
        } catch (IOException exception) {
            throw new RuntimeException("사진 삭제에 실패했습니다.", exception);
        }
        photoRepository.delete(photo);
    }

    @Transactional
    public List<PhotoResponse> reorder(UUID travelId, List<PhotoReorderItemRequest> request) {
        Map<UUID, Photo> photoMap = photoRepository.findByTravelIdOrderBySortOrderAscCreatedAtAsc(travelId).stream()
                .collect(java.util.stream.Collectors.toMap(Photo::getId, photo -> photo));

        for (PhotoReorderItemRequest item : request) {
            Photo photo = photoMap.get(item.photoId());
            if (photo == null) {
                throw new NotFoundException("정렬 대상 사진을 찾을 수 없습니다.");
            }
            photo.setSortOrder(item.sortOrder());
        }

        return photoRepository.findByTravelIdOrderBySortOrderAscCreatedAtAsc(travelId).stream()
                .map(PhotoResponse::from)
                .toList();
    }

    public List<Photo> getEntitiesByTravel(UUID travelId) {
        return photoRepository.findByTravelIdOrderBySortOrderAscCreatedAtAsc(travelId);
    }

    public Photo getEntity(UUID photoId) {
        return photoRepository.findById(photoId)
                .orElseThrow(() -> new NotFoundException("사진을 찾을 수 없습니다."));
    }
}
