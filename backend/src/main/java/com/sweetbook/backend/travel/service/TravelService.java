package com.sweetbook.backend.travel.service;

import com.sweetbook.backend.common.exception.NotFoundException;
import com.sweetbook.backend.travel.dto.TravelCreateRequest;
import com.sweetbook.backend.travel.dto.TravelResponse;
import com.sweetbook.backend.travel.dto.TravelUpdateRequest;
import com.sweetbook.backend.travel.entity.Travel;
import com.sweetbook.backend.travel.repository.TravelRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TravelService {

    private final TravelRepository travelRepository;

    @Transactional
    public TravelResponse create(TravelCreateRequest request) {
        Travel travel = new Travel();
        travel.setTitle(request.title());
        travel.setDescription(request.description());
        travel.setStartDate(request.startDate());
        travel.setEndDate(request.endDate());
        travel.setMood(request.mood());

        return TravelResponse.from(travelRepository.save(travel));
    }

    public List<TravelResponse> findAll() {
        return travelRepository.findAll().stream()
                .map(TravelResponse::from)
                .toList();
    }

    public TravelResponse findById(UUID travelId) {
        return TravelResponse.from(getEntity(travelId));
    }

    @Transactional
    public TravelResponse update(UUID travelId, TravelUpdateRequest request) {
        Travel travel = getEntity(travelId);
        travel.setTitle(request.title());
        travel.setDescription(request.description());
        travel.setStartDate(request.startDate());
        travel.setEndDate(request.endDate());
        travel.setMood(request.mood());
        return TravelResponse.from(travel);
    }

    @Transactional
    public void delete(UUID travelId) {
        travelRepository.delete(getEntity(travelId));
    }

    public Travel getEntity(UUID travelId) {
        return travelRepository.findById(travelId)
                .orElseThrow(() -> new NotFoundException("여행 프로젝트를 찾을 수 없습니다."));
    }
}
