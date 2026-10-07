package com.hospital.management.opd.model;

import jakarta.persistence.*;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table (name = "opds")
@Getter
@Setter
@NoArgsConstructor
public class Opd {

    @Id
    @GeneratedValue
    private Long id ;

    @Column(
            name = "opd_code",
            nullable = false,
            unique = true,
            length = 20
    )
    private String opdCode;

    @Column(
            nullable = false,
            unique = true,
            length = 100
    )
    private String name;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OpdStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = OpdStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }






}
