package com.hospital.management.opd.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OpdCreateRequest(

        @NotBlank(message = "OPD code is required")
        @Size(max = 20, message = "OPD code cannot exceed 20 characters")
        String opdCode,

        @NotBlank(message = "OPD name is required")
        @Size(max = 100, message = "OPD name cannot exceed 100 characters")
        String name,

        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description
) {


}
