package com.hospital.management.patient.dto;

import com.hospital.management.patient.model.PatientStatus;

import java.time.LocalDate;

public record PatientResponse(

        String patientId,

        String firstName,

        String lastName,

        LocalDate dateOfBirth,

        String gender,

        String phone,

        String email,

        String address,

        String bloodGroup,

        String emergencyContactName,

        String emergencyContactPhone,

        PatientStatus status
) {}