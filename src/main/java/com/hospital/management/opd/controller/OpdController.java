package com.hospital.management.opd.controller;

import com.hospital.management.opd.dto.OpdCreateRequest;
import com.hospital.management.opd.dto.OpdResponse;
import com.hospital.management.opd.service.OpdService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/opds")
public class OpdController {

    private final OpdService opdService;

    public OpdController(OpdService opdService) {
        this.opdService = opdService;
    }

    @PostMapping
    public ResponseEntity<OpdResponse> createOpd(
            @Valid @RequestBody OpdCreateRequest request
    ) {

        OpdResponse response =
                opdService.createOpd(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}