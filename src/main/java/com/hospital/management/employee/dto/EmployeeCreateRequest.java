package com.hospital.management.employee.dto;


import com.hospital.management.employee.model.EmployeeRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record EmployeeCreateRequest(
        @NotBlank(message = "First name is required")
        @Size(max = 50, message = "First name cannot exceed 50 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 50, message = "Last name cannot exceed 50 characters")
        String lastName,

        @NotNull(message = "Employee role is required")
        EmployeeRole role,

        @Size(max = 100, message = "Department cannot exceed 100 characters")
        String department,

        @NotBlank(message = "Phone number is required")
        @Pattern(
                regexp = "^[0-9]{10}$",
                message = "Phone number must contain exactly 10 digits"
        )
        String phone,

        @Email(message = "Invalid email address")
        @Size(max = 100, message = "Email cannot exceed 100 characters")
        String email,

        @NotNull(message = "Joining date is required")
        @PastOrPresent(message = "Joining date cannot be in the future")
        LocalDate joiningDate
) {


}
