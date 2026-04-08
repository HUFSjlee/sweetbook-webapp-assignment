package com.sweetbook.backend.order.entity;

import com.sweetbook.backend.travel.entity.Travel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "travel_orders")
public class TravelOrder {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "travel_id", nullable = false)
    private Travel travel;

    @Column(length = 100)
    private String orderUid;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status;

    @Column(nullable = false, length = 100)
    private String recipientName;

    @Column(nullable = false, length = 300)
    private String address;

    @Column(nullable = false, length = 50)
    private String phone;

    private Integer estimatedPrice;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (status == null) {
            status = OrderStatus.PENDING;
        }
        createdAt = OffsetDateTime.now();
    }
}
