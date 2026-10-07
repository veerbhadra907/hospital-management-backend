package com.hospital.management.doctorassignment.mapper;

import com.hospital.management.doctorassignment.dto.DoctorOpdAssignmentResponse;
import com.hospital.management.doctorassignment.model.DoctorOpdAssignment;
import org.springframework.stereotype.Component;

@Component
public class DoctorOpdAssignmentMapper {

    public DoctorOpdAssignmentResponse toResponse(
            DoctorOpdAssignment assignment
    ) {

        return new DoctorOpdAssignmentResponse(
                assignment.getId(),
                assignment.getDoctor().getEmployeeId(),
                assignment.getDoctor().getFirstName()
                        + " "
                        + assignment.getDoctor().getLastName(),
                assignment.getOpd().getOpdCode(),
                assignment.getOpd().getName(),
                assignment.getAssignedAt(),
                assignment.getUnassignedAt(),
                assignment.getStatus()
        );
    }
}