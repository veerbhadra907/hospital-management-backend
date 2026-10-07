package com.hospital.management.doctorassignment.controller;

import com.hospital.management.doctorassignment.dto.DoctorOpdAssignmentCreateRequest;
import com.hospital.management.doctorassignment.dto.DoctorOpdAssignmentResponse;
import com.hospital.management.doctorassignment.service.DoctorOpdAssignmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


@RestController
@RequestMapping("/api/v1/doctor-assignments")
public class DoctorOpdAssignmentController {

    private final DoctorOpdAssignmentService assignmentService;

    public DoctorOpdAssignmentController(
            DoctorOpdAssignmentService assignmentService
    ) {
        this.assignmentService = assignmentService;
    }

    @PostMapping
    public ResponseEntity<DoctorOpdAssignmentResponse> assignDoctorToOpd(
            @Valid @RequestBody DoctorOpdAssignmentCreateRequest request
    ) {

        DoctorOpdAssignmentResponse response =
                assignmentService.assignDoctorToOpd(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<Page<DoctorOpdAssignmentResponse>> getAllActiveAssignments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                assignmentService.getAllActiveAssignments(pageable)
        );
    }

    @GetMapping("/doctor/{employeeId}")
    public ResponseEntity<Page<DoctorOpdAssignmentResponse>> getAssignmentsByDoctor(
            @PathVariable String employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                assignmentService.getAssignmentsByDoctor(
                        employeeId,
                        pageable
                )
        );
    }

    @GetMapping("/opd/{opdCode}")
    public ResponseEntity<Page<DoctorOpdAssignmentResponse>> getAssignmentsByOpd(
            @PathVariable String opdCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                assignmentService.getAssignmentsByOpd(
                        opdCode,
                        pageable
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DoctorOpdAssignmentResponse> deactivateAssignment(
            @PathVariable Long id
    ) {

        DoctorOpdAssignmentResponse response =
                assignmentService.deactivateAssignment(id);

        return ResponseEntity.ok(response);
    }

}