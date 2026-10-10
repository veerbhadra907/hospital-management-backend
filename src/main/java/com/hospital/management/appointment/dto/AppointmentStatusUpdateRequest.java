package com.hospital.management.appointment.dto;

import com.hospital.management.appointment.model.AppointmentStatus;
import jakarta.validation.constraints.NotNull;

public record AppointmentStatusUpdateRequest(

        @NotNull(message = "Appointment status is required")
        AppointmentStatus status

) {
}