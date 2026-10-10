package com.hospital.management.doctorschedule.controller;

import com.hospital.management.doctorschedule.dto.DoctorScheduleCreateRequest;
import com.hospital.management.doctorschedule.dto.DoctorScheduleResponse;
import com.hospital.management.doctorschedule.service.DoctorScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.List;

@RestController
@RequestMapping("/api/v1/doctor-schedules")
@RequiredArgsConstructor
public class DoctorScheduleController {

    private final DoctorScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<DoctorScheduleResponse> createSchedule(
            @Valid @RequestBody DoctorScheduleCreateRequest request) {

        DoctorScheduleResponse response =
                scheduleService.createSchedule(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/doctor/{employeeId}")
    public ResponseEntity<Page<DoctorScheduleResponse>> getDoctorSchedules(
            @PathVariable String employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                scheduleService.getDoctorSchedules(
                        employeeId,
                        pageable
                )
        );
    }

    @GetMapping("/doctor/{employeeId}/day/{dayOfWeek}")
    public ResponseEntity<List<DoctorScheduleResponse>>
    getDoctorSchedulesForDay(
            @PathVariable String employeeId,
            @PathVariable DayOfWeek dayOfWeek) {

        return ResponseEntity.ok(
                scheduleService.getDoctorSchedulesForDay(
                        employeeId,
                        dayOfWeek
                )
        );
    }


}
