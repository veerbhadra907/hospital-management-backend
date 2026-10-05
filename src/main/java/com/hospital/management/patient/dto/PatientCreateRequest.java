package com.hospital.management.patient.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;


public record PatientCreateRequest(

        @NotBlank(message = "First name is required")
        @Size(max = 50, message = "First name cannot exceed 50 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 50, message = "Last name cannot exceed 50 characters")
        String lastName,

        @NotNull(message = "Date of birth is required")
        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,

        @NotBlank(message = "Gender is required")
        String gender,

        @NotBlank(message = "Phone number is required")
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
        String emergencyContactPhone
) {}
