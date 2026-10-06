package com.hospital.management.employee.dto;

import com.hospital.management.employee.model.EmployeeRole;
import com.hospital.management.employee.model.EmployeeStatus;

import java.time.LocalDate;

public record EmployeeResponse (
        String employeeId,

        String firstName,

        String lastName,

        EmployeeRole role,

        String department,

        String phone,

        String email,

        LocalDate joiningDate,

        EmployeeStatus status
) {

}
