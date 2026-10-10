package com.hospital.management.appointment.dto;

import com.hospital.management.appointment.model.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record AppointmentResponse(

        String appointmentId,

        String patientId,

        String patientName,

        String employeeId,

        String doctorName,

        String opdCode,

        String opdName,

        LocalDate appointmentDate,

        LocalTime startTime,

        LocalTime endTime,

        String reason,

        AppointmentStatus status,

        LocalDateTime createdAt,

        LocalDateTime updatedAt
) {
}