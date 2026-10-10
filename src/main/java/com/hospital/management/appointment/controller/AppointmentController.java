package com.hospital.management.appointment.controller;

import com.hospital.management.appointment.dto.AppointmentCreateRequest;
import com.hospital.management.appointment.dto.AppointmentResponse;
import com.hospital.management.appointment.dto.AppointmentStatusUpdateRequest;
import com.hospital.management.appointment.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.hospital.management.appointment.dto.AppointmentSlotResponse;
import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/v1/appointments")
public class AppointmentController {


    private final AppointmentService appointmentService;

    public AppointmentController(
            AppointmentService appointmentService
    ) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<AppointmentResponse> createAppointment(
            @Valid @RequestBody AppointmentCreateRequest request
    ) {

        AppointmentResponse response =
                appointmentService.createAppointment(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<Page<AppointmentResponse>> getPatientAppointments(
            @PathVariable String patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                appointmentService.getPatientAppointments(
                        patientId,
                        pageable
                )
        );
    }

    @GetMapping("/doctor/{employeeId}")
    public ResponseEntity<Page<AppointmentResponse>> getDoctorAppointments(
            @PathVariable String employeeId,
            @RequestParam String date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                appointmentService.getDoctorAppointments(
                        employeeId,
                        java.time.LocalDate.parse(date),
                        pageable
                )
        );
    }

    @GetMapping("/opd/{opdCode}")
    public ResponseEntity<Page<AppointmentResponse>> getOpdAppointments(
            @PathVariable String opdCode,
            @RequestParam String date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                appointmentService.getOpdAppointments(
                        opdCode,
                        java.time.LocalDate.parse(date),
                        pageable
                )
        );
    }

    @DeleteMapping("/{appointmentId}")
    public ResponseEntity<AppointmentResponse> cancelAppointment(
            @PathVariable String appointmentId
    ) {

        AppointmentResponse response =
                appointmentService.cancelAppointment(appointmentId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/doctor/{employeeId}/slots")
    public ResponseEntity<List<AppointmentSlotResponse>> getAvailableSlots(
            @PathVariable String employeeId,
            @RequestParam LocalDate date
    ) {

        return ResponseEntity.ok(
                appointmentService.getAvailableSlots(
                        employeeId,
                        date
                )
        );
    }

    @PatchMapping("/{appointmentId}/status")
    public ResponseEntity<AppointmentResponse> updateAppointmentStatus(
            @PathVariable String appointmentId,
            @Valid @RequestBody AppointmentStatusUpdateRequest request) {

        AppointmentResponse response =
                appointmentService.updateAppointmentStatus(
                        appointmentId,
                        request.status()
                );

        return ResponseEntity.ok(response);
    }




}
