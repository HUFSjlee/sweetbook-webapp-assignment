package com.sweetbook.backend.travel.repository;

import com.sweetbook.backend.travel.entity.Travel;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelRepository extends JpaRepository<Travel, UUID> {
}
