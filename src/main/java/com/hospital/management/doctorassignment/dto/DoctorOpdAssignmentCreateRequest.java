package com.hospital.management.doctorassignment.dto;

import jakarta.validation.constraints.NotBlank;

public record DoctorOpdAssignmentCreateRequest(

        @NotBlank(message = "Doctor employee ID is required")
        String employeeId,

        @NotBlank(message = "OPD code is required")
        String opdCode
) {
}