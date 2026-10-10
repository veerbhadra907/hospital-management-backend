package com.hospital.management.security.dto;


import com.hospital.management.security.model.UserRole;

public record PatientRegistrationResponse (
        String patientId,
        String email,
        UserRole role,
        String message
)
{}
