package com.hospital.management.appointment.mapper;

import com.hospital.management.appointment.dto.AppointmentResponse;
import com.hospital.management.appointment.model.Appointment;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    public AppointmentResponse toResponse(Appointment appointment) {

        return new AppointmentResponse(
                appointment.getAppointmentId(),

                appointment.getPatient().getPatientId(),

                appointment.getPatient().getFirstName()
                        + " "
                        + appointment.getPatient().getLastName(),

                appointment.getDoctor().getEmployeeId(),

                appointment.getDoctor().getFirstName()
                        + " "
                        + appointment.getDoctor().getLastName(),

                appointment.getOpd().getOpdCode(),

                appointment.getOpd().getName(),

                appointment.getAppointmentDate(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.getReason(),
                appointment.getStatus(),
                appointment.getCreatedAt(),
                appointment.getUpdatedAt()
        );
    }
}