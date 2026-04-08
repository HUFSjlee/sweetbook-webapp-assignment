package com.sweetbook.backend.order.dto;

import com.sweetbook.backend.order.entity.OrderStatus;
import com.sweetbook.backend.order.entity.TravelOrder;
import java.time.OffsetDateTime;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        UUID travelId,
        String orderUid,
        OrderStatus status,
        String recipientName,
        String address,
        String phone,
        Integer estimatedPrice,
        OffsetDateTime createdAt
) {
    public static OrderResponse from(TravelOrder order) {
        return new OrderResponse(
                order.getId(),
                order.getTravel().getId(),
                order.getOrderUid(),
                order.getStatus(),
                order.getRecipientName(),
                order.getAddress(),
                order.getPhone(),
                order.getEstimatedPrice(),
                order.getCreatedAt()
        );
    }
}
