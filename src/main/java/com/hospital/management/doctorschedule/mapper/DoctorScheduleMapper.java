package com.hospital.management.doctorschedule.mapper;

import com.hospital.management.doctorschedule.dto.DoctorScheduleResponse;
import com.hospital.management.doctorschedule.model.DoctorSchedule;
import org.springframework.stereotype.Component;

@Component
public class DoctorScheduleMapper {
    public DoctorScheduleResponse toResponse(DoctorSchedule schedule) {

        String doctorName =
                schedule.getDoctor().getFirstName()
                        + " "
                        + schedule.getDoctor().getLastName();

        return new DoctorScheduleResponse(
                schedule.getId(),
                schedule.getDoctor().getEmployeeId(),
                doctorName,
                schedule.getDayOfWeek(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getSlotDurationMinutes(),
                schedule.getCreatedAt(),
                schedule.getUpdatedAt()
        );
    }
}
