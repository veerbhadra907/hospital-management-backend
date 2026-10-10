package com.hospital.management.security.controller;

import com.hospital.management.security.dto.PatientRegistrationRequest;
import com.hospital.management.security.dto.PatientRegistrationResponse;
import com.hospital.management.security.service.PatientRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final PatientRegistrationService registrationService ;

    @PostMapping("/register")
    public ResponseEntity<PatientRegistrationResponse> registerPatient(
            @Valid @RequestBody PatientRegistrationRequest request) {

        PatientRegistrationResponse response =
                registrationService.registerPatient(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

}
