package com.hospital.management.appointment.repository;

import com.hospital.management.appointment.model.Appointment;
import com.hospital.management.appointment.model.AppointmentStatus;
import com.hospital.management.employee.model.Employee;
import com.hospital.management.patient.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.hospital.management.opd.model.Opd;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import java.time.LocalDate;
import java.time.LocalTime;

public interface AppointmentRepository
        extends JpaRepository<Appointment, Long> {

    boolean existsByAppointmentId(String appointmentId);

    Optional<Appointment> findByAppointmentId(String appointmentId);

    Page<Appointment> findByStatus(
            AppointmentStatus status,
            Pageable pageable
    );

    Page<Appointment> findByDoctorAndStatus(
            Employee doctor,
            AppointmentStatus status,
            Pageable pageable
    );

    Page<Appointment> findByPatientAndStatus(
            Patient patient,
            AppointmentStatus status,
            Pageable pageable
    );

    Page<Appointment> findByDoctorAndAppointmentDateAndStatus(
            Employee doctor,
            LocalDate appointmentDate,
            AppointmentStatus status,
            Pageable pageable
    );

    Page<Appointment> findByPatientAndStatusIn(
            Patient patient,
            java.util.Collection<AppointmentStatus> statuses,
            Pageable pageable
    );

    Page<Appointment> findByDoctorAndAppointmentDateAndStatusIn(
            Employee doctor,
            LocalDate appointmentDate,
            java.util.Collection<AppointmentStatus> statuses,
            Pageable pageable
    );

    Page<Appointment> findByOpdAndAppointmentDateAndStatusIn(
            Opd opd,
            LocalDate appointmentDate,
            java.util.Collection<AppointmentStatus> statuses,
            Pageable pageable
    );


    @Query("""
            SELECT COUNT(a) > 0
            FROM Appointment a
            WHERE a.doctor = :doctor
              AND a.appointmentDate = :appointmentDate
              AND a.status IN :activeStatuses
              AND a.startTime < :endTime
              AND a.endTime > :startTime
            """)
    boolean existsOverlappingDoctorAppointment(
            @Param("doctor") Employee doctor,
            @Param("appointmentDate") LocalDate appointmentDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("activeStatuses")
            java.util.Collection<AppointmentStatus> activeStatuses
    );

    @Query("""
            SELECT COUNT(a) > 0
            FROM Appointment a
            WHERE a.patient = :patient
              AND a.appointmentDate = :appointmentDate
              AND a.status IN :activeStatuses
              AND a.startTime < :endTime
              AND a.endTime > :startTime
            """)
    boolean existsOverlappingPatientAppointment(
            @Param("patient") Patient patient,
            @Param("appointmentDate") LocalDate appointmentDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("activeStatuses")
            java.util.Collection<AppointmentStatus> activeStatuses
    );
}
