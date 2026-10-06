package com.hospital.management.employee.controller;

import com.hospital.management.employee.dto.EmployeeCreateRequest;
import com.hospital.management.employee.dto.EmployeeResponse;
import com.hospital.management.employee.dto.EmployeeUpdateRequest;
import com.hospital.management.employee.service.EmployeeService;
import com.hospital.management.employee.model.EmployeeRole ;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }


    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(
            @Valid @RequestBody EmployeeCreateRequest request
    ) {

        EmployeeResponse response =
                employeeService.createEmployee(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<Page<EmployeeResponse>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        if (page < 0) {
            page = 0;
        }

        if (size < 1) {
            size = 10;
        }

        if (size > 100) {
            size = 100;
        }

        Page<EmployeeResponse> employees =
                employeeService.getAllEmployees(page, size);

        return ResponseEntity.ok(employees);
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<Page<EmployeeResponse>> getEmployeesByRole(
            @PathVariable EmployeeRole role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        if (page < 0) {
            page = 0;
        }

        if (size < 1) {
            size = 10;
        }

        if (size > 100) {
            size = 100;
        }

        Page<EmployeeResponse> employees =
                employeeService.getEmployeesByRole(
                        role,
                        page,
                        size
                );

        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(
            @PathVariable String employeeId
    ) {

        EmployeeResponse response =
                employeeService.getEmployeeById(employeeId);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{employeeId}")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable String employeeId,
            @Valid @RequestBody EmployeeUpdateRequest request
    ) {

        EmployeeResponse response =
                employeeService.updateEmployee(
                        employeeId,
                        request
                );

        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/{employeeId}")
    public ResponseEntity<Void> deactivateEmployee(
            @PathVariable String employeeId
    ) {

        employeeService.deactivateEmployee(employeeId);

        return ResponseEntity.noContent().build();
    }



}
