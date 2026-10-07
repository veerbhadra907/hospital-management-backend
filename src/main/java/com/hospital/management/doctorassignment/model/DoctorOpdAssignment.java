package com.hospital.management.doctorassignment.model;

import com.hospital.management.employee.model.Employee;
import com.hospital.management.opd.model.Opd;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "doctor_opd_assignments")
@Getter
@Setter
@NoArgsConstructor
public class DoctorOpdAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Employee doctor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "opd_id", nullable = false)
    private Opd opd;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    @Column(name = "unassigned_at")
    private LocalDateTime unassignedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DoctorOpdAssignmentStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (assignedAt == null) {
            assignedAt = now;
        }

        if (status == null) {
            status = DoctorOpdAssignmentStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();


    }
    }
