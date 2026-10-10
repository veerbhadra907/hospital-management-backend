package com.hospital.management.appointment.dto;

import java.time.LocalTime;

public record AppointmentSlotResponse (

        LocalTime startTime,
        LocalTime endTime,
        boolean available
) {


}
