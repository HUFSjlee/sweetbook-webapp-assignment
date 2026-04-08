package com.sweetbook.backend.travel.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "travels")
public class Travel {

    @Id
    private UUID id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 300)
    private String description;

    private LocalDate startDate;
    private LocalDate endDate;

    @Column(length = 50)
    private String mood;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TravelStatus status;

    @Column(length = 100)
    private String bookUid;

    @Column(length = 500)
    private String coverImageUrl;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (status == null) {
            status = TravelStatus.DRAFT;
        }
        createdAt = OffsetDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
