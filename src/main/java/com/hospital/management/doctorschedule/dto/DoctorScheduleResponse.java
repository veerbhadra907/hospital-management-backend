package com.hospital.management.doctorschedule.dto;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record DoctorScheduleResponse (
        Long id,
        String employeeId,
        String doctorName,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        Integer slotDurationMinutes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
