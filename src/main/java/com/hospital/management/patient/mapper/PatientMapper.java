package com.hospital.management.patient.mapper;

import com.hospital.management.patient.dto.PatientCreateRequest;
import com.hospital.management.patient.dto.PatientResponse;
import com.hospital.management.patient.model.Patient;
import org.springframework.stereotype.Component;


@Component
public class PatientMapper {

    public Patient toEntity(PatientCreateRequest request) {

        Patient patient = new Patient();

        patient.setFirstName(request.firstName());
        patient.setLastName(request.lastName());
        patient.setDateOfBirth(request.dateOfBirth());
        patient.setGender(request.gender());
        patient.setPhone(request.phone());
        patient.setEmail(request.email());
        patient.setAddress(request.address());
        patient.setBloodGroup(request.bloodGroup());
        patient.setEmergencyContactName(request.emergencyContactName());
        patient.setEmergencyContactPhone(request.emergencyContactPhone());

        return patient;
    }

    public PatientResponse toResponse(Patient patient) {

        return new PatientResponse(
                patient.getPatientId(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getDateOfBirth(),
                patient.getGender(),
                patient.getPhone(),
                patient.getEmail(),
                patient.getAddress(),
                patient.getBloodGroup(),
                patient.getEmergencyContactName(),
                patient.getEmergencyContactPhone(),
                patient.getStatus()
        );
    }
}
