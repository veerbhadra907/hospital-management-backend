package com.hospital.management.patient.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.parameters.P;

import java.time.LocalDate;


public record PatientUpdateRequest(

        @Size(max = 50 , message = "First name cannot exceed 50 characters")
        String firstName ,

        @Size(max = 50, message = "Last name cannot exceed 50 characters")
        String lastName,

        LocalDate dateOfBirth,

        String gender,

        @Pattern(
                regexp = "^[0-9]{10}$",
                message = "Phone number must contain exactly 10 digits"
        )
        String phone,

        @Email(message = "Invalid email address")
        @Size(max = 100, message = "Email cannot exceed 100 characters")
        String email,

        @Size(max = 500, message = "Address cannot exceed 500 characters")
        String address,

        @Size(max = 5, message = "Invalid blood group")
        String bloodGroup,

        @Size(max = 100, message = "Emergency contact name cannot exceed 100 characters")
        String emergencyContactName,

        @Pattern(
                regexp = "^[0-9]{10}$",
                message = "Emergency contact phone must contain exactly 10 digits"
        )
        String  emergencyContactPhone


) {}
