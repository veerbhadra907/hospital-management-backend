package com.hospital.management.employee.mapper;

import com.hospital.management.employee.dto.EmployeeCreateRequest;
import com.hospital.management.employee.dto.EmployeeResponse;
import com.hospital.management.employee.model.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public Employee toEntity(EmployeeCreateRequest request) {

        Employee employee = new Employee();

        employee.setFirstName(request.firstName());
        employee.setLastName(request.lastName());
        employee.setRole(request.role());
        employee.setDepartment(request.department());
        employee.setPhone(request.phone());
        employee.setEmail(request.email());
        employee.setJoiningDate(request.joiningDate());

        return employee;
    }

    public EmployeeResponse toResponse(Employee employee) {

        return new EmployeeResponse(
                employee.getEmployeeId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getRole(),
                employee.getDepartment(),
                employee.getPhone(),
                employee.getEmail(),
                employee.getJoiningDate(),
                employee.getStatus()
        );
    }

}
