package com.hospital.management.patient.controller;

import com.hospital.management.patient.dto.PatientCreateRequest;
import com.hospital.management.patient.dto.PatientResponse;
import com.hospital.management.patient.dto.PatientUpdateRequest;
import com.hospital.management.patient.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.ParameterizedType;


@RestController
    @RequestMapping("/api/v1/patients")
    public class PatientController {

        private final PatientService patientService;

        public PatientController(PatientService patientService) {
            this.patientService = patientService;
        }

        @PostMapping
        public ResponseEntity<PatientResponse> createPatient(
                @Valid @RequestBody PatientCreateRequest request
        ) {

            PatientResponse response = patientService.createPatient(request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);
        }

    @GetMapping("/search")
    public ResponseEntity<Page<PatientResponse>> searchPatients(
            @RequestParam String query,
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

        Page<PatientResponse> patients =
                patientService.searchPatients(query, page, size);

        return ResponseEntity.ok(patients);
    }

    @GetMapping("/{patientId}")
    public ResponseEntity<PatientResponse> getPatientById(
            @PathVariable String patientId
    ) {

        PatientResponse response =
                patientService.getPatientById(patientId);

        return ResponseEntity.ok(response);
    }

        @GetMapping
        public ResponseEntity<Page<PatientResponse>> getAllPatients(
                @RequestParam(defaultValue = "0") int page ,
                @RequestParam(defaultValue = "10")int size
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

            Page<PatientResponse> patients = patientService.getAllPatients(page ,size);
            return ResponseEntity.ok(patients);
        }

    @PutMapping("/{patientId}")
    public ResponseEntity<PatientResponse> updatePatient(
            @PathVariable String patientId,
            @Valid @RequestBody PatientUpdateRequest request
    ) {

        PatientResponse response =
                patientService.updatePatient(patientId, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{patientId}")
    public ResponseEntity<Void> deletePatient(
            @PathVariable String patientId
    ) {

        patientService.deactivatePatient(patientId);

        return ResponseEntity.noContent().build();
    }







    }





