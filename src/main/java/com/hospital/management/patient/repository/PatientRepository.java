package com.hospital.management.patient.repository;

import com.hospital.management.patient.model.Patient;
import com.hospital.management.patient.model.PatientStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface PatientRepository  extends  JpaRepository<Patient, Long>{

    boolean existsByPhone(String phone) ;

    Optional<Patient> findByPatientId(String patientId);





    Page<Patient> findByStatus(
            PatientStatus status,
            Pageable pageable
    );

    Page<Patient> findByStatusAndFirstNameContainingIgnoreCaseOrStatusAndLastNameContainingIgnoreCaseOrStatusAndPhoneContainingOrStatusAndPatientIdContainingOrStatusAndEmailContainingIgnoreCase(
            PatientStatus status1,
            String firstName,
            PatientStatus status2,
            String lastName,
            PatientStatus status3,
            String phone,
            PatientStatus status4,
            String patientId,
            PatientStatus status5,
            String email,
            Pageable pageable
    );
}
