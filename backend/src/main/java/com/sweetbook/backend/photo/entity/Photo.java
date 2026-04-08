package com.sweetbook.backend.photo.entity;

import com.sweetbook.backend.travel.entity.Travel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
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
@Table(name = "photos")
public class Photo {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "travel_id", nullable = false)
    private Travel travel;

    @Column(nullable = false, length = 500)
    private String imageUrl;

    @Column(nullable = false, length = 300)
    private String storagePath;

    @Column(length = 500)
    private String comment;

    @Column(length = 150)
    private String location;

    private LocalDate takenDate;

    @Column(nullable = false)
    private Integer sortOrder;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        createdAt = OffsetDateTime.now();
    }
}
