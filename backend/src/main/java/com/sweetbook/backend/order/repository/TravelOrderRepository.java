package com.sweetbook.backend.order.repository;

import com.sweetbook.backend.order.entity.TravelOrder;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelOrderRepository extends JpaRepository<TravelOrder, UUID> {

    List<TravelOrder> findAllByOrderByCreatedAtDesc();
}
