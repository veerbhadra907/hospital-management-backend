package com.hospital.management.doctorassignment.repository;

import com.hospital.management.doctorassignment.model.DoctorOpdAssignment;
import com.hospital.management.doctorassignment.model.DoctorOpdAssignmentStatus;
import com.hospital.management.employee.model.Employee;
import com.hospital.management.opd.model.Opd;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorOpdAssignmentRepository
        extends JpaRepository<DoctorOpdAssignment, Long> {

    boolean existsByDoctorAndOpdAndStatus(
            Employee doctor,
            Opd opd,
            DoctorOpdAssignmentStatus status
    );

    Page<DoctorOpdAssignment> findByStatus(
            DoctorOpdAssignmentStatus status,
            Pageable pageable
    );

    Page<DoctorOpdAssignment> findByDoctorAndStatus(
            Employee doctor,
            DoctorOpdAssignmentStatus status,
            Pageable pageable
    );

    Page<DoctorOpdAssignment> findByOpdAndStatus(
            Opd opd,
            DoctorOpdAssignmentStatus status,
            Pageable pageable
    );

    Optional<DoctorOpdAssignment> findByIdAndStatus(
            Long id,
            DoctorOpdAssignmentStatus status
    );

    
}