package com.hospital.management.employee.service;

import com.hospital.management.employee.dto.EmployeeCreateRequest;
import com.hospital.management.employee.dto.EmployeeResponse;
import com.hospital.management.employee.dto.EmployeeUpdateRequest;
import com.hospital.management.employee.mapper.EmployeeMapper;
import com.hospital.management.employee.model.Employee;
import com.hospital.management.employee.repository.EmployeeRepository ;
import com.hospital.management.exception.DuplicateResourceException;
import com.hospital.management.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import com.hospital.management.employee.model.EmployeeRole;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.management.employee.model.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    EmployeeService(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public EmployeeResponse createEmployee(EmployeeCreateRequest request) {

        if (employeeRepository.existsByPhone(request.phone())) {
            throw new DuplicateResourceException(
                    "An employee with this phone number already exists"
            );
        }

        if (request.email() != null &&
                employeeRepository.existsByEmail(request.email())) {

            throw new DuplicateResourceException(
                    "An employee with this email already exists"
            );
        }

        Employee employee = employeeMapper.toEntity(request);

        employee.setEmployeeId(generateEmployeeId());

        Employee savedEmployee =
                employeeRepository.save(employee);

        return employeeMapper.toResponse(savedEmployee);

    }

    private String generateEmployeeId() {
        return "EMP-" + UUID.randomUUID();
    }

    @Transactional(readOnly = true)
    public Page<EmployeeResponse> getAllEmployees(
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        return employeeRepository
                .findByStatus(EmployeeStatus.ACTIVE, pageable)
                .map(employeeMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<EmployeeResponse> getEmployeesByRole(
            EmployeeRole role,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        return employeeRepository
                .findByStatusAndRole(
                        EmployeeStatus.ACTIVE,
                        role,
                        pageable
                )
                .map(employeeMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(String employeeId) {

        Employee employee = employeeRepository
                .findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee with ID " + employeeId + " was not found"
                ));

        return employeeMapper.toResponse(employee);
    }

    @Transactional
    public EmployeeResponse updateEmployee(
            String employeeId,
            EmployeeUpdateRequest request
    ) {

        Employee employee = employeeRepository
                .findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee with ID " + employeeId + " was not found"
                ));

        if (request.firstName() != null) {
            employee.setFirstName(request.firstName());
        }

        if (request.lastName() != null) {
            employee.setLastName(request.lastName());
        }

        if (request.role() != null) {
            employee.setRole(request.role());
        }

        if (request.department() != null) {
            employee.setDepartment(request.department());
        }

        if (request.phone() != null &&
                !request.phone().equals(employee.getPhone())) {

            if (employeeRepository.existsByPhone(request.phone())) {
                throw new DuplicateResourceException(
                        "An employee with this phone number already exists"
                );
            }

            employee.setPhone(request.phone());
        }

        if (request.email() != null &&
                !request.email().equals(employee.getEmail())) {

            if (employeeRepository.existsByEmail(request.email())) {
                throw new DuplicateResourceException(
                        "An employee with this email already exists"
                );
            }

            employee.setEmail(request.email());
        }

        Employee updatedEmployee =
                employeeRepository.save(employee);

        return employeeMapper.toResponse(updatedEmployee);
    }

    @Transactional
    public void deactivateEmployee(String employeeId) {

        Employee employee = employeeRepository
                .findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee with ID " + employeeId + " was not found"
                ));

        employee.setStatus(EmployeeStatus.INACTIVE);

        employeeRepository.save(employee);
    }
}
