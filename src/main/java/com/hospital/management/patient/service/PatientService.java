package com.hospital.management.patient.service;

import com.hospital.management.exception.ResourceNotFoundException;
import com.hospital.management.patient.model.PatientStatus;
import com.hospital.management.patient.repository.PatientRepository;
import com.hospital.management.patient.dto.PatientResponse ;
import com.hospital.management.patient.dto.PatientCreateRequest ;
import com.hospital.management.patient.mapper.PatientMapper ;
import com.hospital.management.patient.model.Patient ;
import com.hospital.management.exception.DuplicateResourceException;
import com.hospital.management.patient.repository.PatientRepository ;
import com.hospital.management.patient.dto.PatientUpdateRequest ;
import com.hospital.management.patient.model.PatientStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional ;

import javax.smartcardio.ATR;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.UUID;


@Service
public class PatientService {

    private final PatientRepository patientRepository ;
    private final PatientMapper patientMapper ;

    public PatientService (PatientRepository patientRepository ,
                           PatientMapper patientMapper)
    {
        this.patientRepository = patientRepository ;
        this.patientMapper = patientMapper ;
    }



    @Transactional
    public PatientResponse createPatient (PatientCreateRequest request) {

        if (patientRepository.existsByPhone(request.phone())) {
            throw new DuplicateResourceException(
                    "A patient with this phone number already exists"
            );
        }

        // 2. Convert request DTO → Patient entity
        Patient patient = patientMapper.toEntity(request);

        // 3. Generate business patient ID
        patient.setPatientId(generatePatientId());


        // 4. Save patient to database
        Patient savedPatient = patientRepository.save(patient);

        // 5. Convert Patient entity → response DTO
        return patientMapper.toResponse(savedPatient);
    }

    private String generatePatientId() {
        return "PAT-" + UUID.randomUUID();
    }

    @Transactional(readOnly = true)
    public PatientResponse getPatientById(String patientId) {

        Patient patient = patientRepository
                .findByPatientId(patientId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient with ID " + patientId + " was not found"
                ));

        return patientMapper.toResponse(patient);
    }


    @Transactional(readOnly = true)
    public Page<PatientResponse> getAllPatients(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return patientRepository
                .findByStatus(PatientStatus.ACTIVE, pageable)
                .map(patientMapper::toResponse);
    }


    @Transactional(readOnly = true)
    public Page<PatientResponse> searchPatients(
            String query,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(page, size);


        return patientRepository
                .findByStatusAndFirstNameContainingIgnoreCaseOrStatusAndLastNameContainingIgnoreCaseOrStatusAndPhoneContainingOrStatusAndPatientIdContainingOrStatusAndEmailContainingIgnoreCase(
                        PatientStatus.ACTIVE,
                        query,
                        PatientStatus.ACTIVE,
                        query,
                        PatientStatus.ACTIVE,
                        query,
                        PatientStatus.ACTIVE,
                        query,
                        PatientStatus.ACTIVE,
                        query,
                        pageable
                )
                .map(patientMapper::toResponse);
    }

    @Transactional
    public PatientResponse updatePatient(
            String patientId,
            PatientUpdateRequest request
    ) {

        Patient patient = patientRepository
                .findByPatientId(patientId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient with ID " + patientId + " was not found"
                ));

        if (request.firstName() != null) {
            patient.setFirstName(request.firstName());
        }

        if (request.lastName() != null) {
            patient.setLastName(request.lastName());
        }

        if (request.dateOfBirth() != null) {
            patient.setDateOfBirth(request.dateOfBirth());
        }

        if (request.gender() != null) {
            patient.setGender(request.gender());
        }

        if (request.phone() != null &&
                !request.phone().equals(patient.getPhone())) {

            if (patientRepository.existsByPhone(request.phone())) {
                throw new DuplicateResourceException(
                        "A patient with this phone number already exists"
                );
            }

            patient.setPhone(request.phone());
        }

        if (request.email() != null) {
            patient.setEmail(request.email());
        }

        if (request.address() != null) {
            patient.setAddress(request.address());
        }

        if (request.bloodGroup() != null) {
            patient.setBloodGroup(request.bloodGroup());
        }

        if (request.emergencyContactName() != null) {
            patient.setEmergencyContactName(
                    request.emergencyContactName()
            );
        }

        if (request.emergencyContactPhone() != null) {
            patient.setEmergencyContactPhone(
                    request.emergencyContactPhone()
            );
        }

        Patient updatedPatient = patientRepository.save(patient);

        return patientMapper.toResponse(updatedPatient);
    }

    @Transactional
    public void deactivatePatient(String patientId) {

        Patient patient = patientRepository
                .findByPatientId(patientId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient with ID " + patientId + " was not found"
                ));

        patient.setStatus(PatientStatus.INACTIVE);

        patientRepository.save(patient);
    }










    }






