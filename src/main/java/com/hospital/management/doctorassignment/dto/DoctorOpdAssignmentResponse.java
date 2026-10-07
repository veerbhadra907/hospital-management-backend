package com.hospital.management.doctorassignment.dto;

import com.hospital.management.doctorassignment.model.DoctorOpdAssignmentStatus;

import java.time.LocalDateTime;

public record DoctorOpdAssignmentResponse(

        Long id,

        String employeeId,

        String doctorName,

        String opdCode,

        String opdName,

        LocalDateTime assignedAt,

        LocalDateTime unassignedAt,

        DoctorOpdAssignmentStatus status
) {
}