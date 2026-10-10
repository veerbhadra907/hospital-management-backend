package com.hospital.management.security.dto;

import com.hospital.management.patient.dto.PatientCreateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;

public record PatientRegistrationRequest (
        @NotNull(message = "Patient details are required")
        @Valid
        PatientCreateRequest patient,

        @NotBlank(message = "Password is required")
        @Size(min = 12, max = 72,
                message = "Password must contain 12 to 72 characters")
        String password    
)
{}
