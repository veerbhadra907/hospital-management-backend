package com.hospital.management.doctorassignment.service;

import com.hospital.management.doctorassignment.dto.DoctorOpdAssignmentCreateRequest;
import com.hospital.management.doctorassignment.dto.DoctorOpdAssignmentResponse;
import com.hospital.management.doctorassignment.mapper.DoctorOpdAssignmentMapper;
import com.hospital.management.doctorassignment.model.DoctorOpdAssignment;
import com.hospital.management.doctorassignment.model.DoctorOpdAssignmentStatus;
import com.hospital.management.doctorassignment.repository.DoctorOpdAssignmentRepository;
import com.hospital.management.employee.model.Employee;
import com.hospital.management.employee.model.EmployeeRole;
import com.hospital.management.employee.model.EmployeeStatus;
import com.hospital.management.employee.repository.EmployeeRepository;
import com.hospital.management.opd.model.Opd;
import com.hospital.management.opd.model.OpdStatus;
import com.hospital.management.opd.repository.OpdRepository;
import com.hospital.management.exception.DuplicateResourceException;
import com.hospital.management.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Service
public class DoctorOpdAssignmentService {

    private final DoctorOpdAssignmentRepository assignmentRepository;
    private final EmployeeRepository employeeRepository;
    private final OpdRepository opdRepository;
    private final DoctorOpdAssignmentMapper mapper;

    public DoctorOpdAssignmentService(
            DoctorOpdAssignmentRepository assignmentRepository,
            EmployeeRepository employeeRepository,
            OpdRepository opdRepository,
            DoctorOpdAssignmentMapper mapper
    ) {
        this.assignmentRepository = assignmentRepository;
        this.employeeRepository = employeeRepository;
        this.opdRepository = opdRepository;
        this.mapper = mapper;
    }

    @Transactional
    public DoctorOpdAssignmentResponse assignDoctorToOpd(
            DoctorOpdAssignmentCreateRequest request
    ) {

        // 1. Find employee
        Employee doctor = employeeRepository
                .findByEmployeeId(request.employeeId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor not found with employee ID: "
                                        + request.employeeId()
                        )
                );

        // 2. Verify employee is actually a doctor
        if (doctor.getRole() != EmployeeRole.DOCTOR) {
            throw new IllegalArgumentException(
                    "Employee is not a doctor"
            );
        }

        // 3. Verify doctor is active
        if (doctor.getStatus() != EmployeeStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Doctor is not active"
            );
        }

        // 4. Find OPD
        Opd opd = opdRepository
                .findByOpdCode(request.opdCode())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "OPD not found with code: "
                                        + request.opdCode()
                        )
                );

        // 5. Verify OPD is active
        if (opd.getStatus() != OpdStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "OPD is not active"
            );
        }

        // 6. Prevent duplicate active assignment
        boolean alreadyAssigned =
                assignmentRepository.existsByDoctorAndOpdAndStatus(
                        doctor,
                        opd,
                        DoctorOpdAssignmentStatus.ACTIVE
                );

        if (alreadyAssigned) {
            throw new DuplicateResourceException(
                    "Doctor is already assigned to this OPD"
            );
        }

        // 7. Create assignment
        DoctorOpdAssignment assignment =
                new DoctorOpdAssignment();

        assignment.setDoctor(doctor);
        assignment.setOpd(opd);
        assignment.setStatus(
                DoctorOpdAssignmentStatus.ACTIVE
        );

        // 8. Save
        DoctorOpdAssignment savedAssignment =
                assignmentRepository.save(assignment);

        // 9. Convert entity to response
        return mapper.toResponse(savedAssignment);
    }

    @Transactional(readOnly = true)
    public Page<DoctorOpdAssignmentResponse> getAllActiveAssignments(
            Pageable pageable
    ) {

        return assignmentRepository
                .findByStatus(
                        DoctorOpdAssignmentStatus.ACTIVE,
                        pageable
                )
                .map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<DoctorOpdAssignmentResponse> getAssignmentsByDoctor(
            String employeeId,
            Pageable pageable
    ) {

        Employee doctor = employeeRepository
                .findByEmployeeId(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor not found with employee ID: "
                                        + employeeId
                        )
                );

        if (doctor.getRole() != EmployeeRole.DOCTOR) {
            throw new IllegalArgumentException(
                    "Employee is not a doctor"
            );
        }

        return assignmentRepository
                .findByDoctorAndStatus(
                        doctor,
                        DoctorOpdAssignmentStatus.ACTIVE,
                        pageable
                )
                .map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<DoctorOpdAssignmentResponse> getAssignmentsByOpd(
            String opdCode,
            Pageable pageable
    ) {

        Opd opd = opdRepository
                .findByOpdCode(opdCode)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "OPD not found with code: "
                                        + opdCode
                        )
                );

        return assignmentRepository
                .findByOpdAndStatus(
                        opd,
                        DoctorOpdAssignmentStatus.ACTIVE,
                        pageable
                )
                .map(mapper::toResponse);
    }

    @Transactional
    public DoctorOpdAssignmentResponse deactivateAssignment(Long id) {

        DoctorOpdAssignment assignment =
                assignmentRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Doctor-OPD assignment not found with ID: "
                                                + id
                                )
                        );

        if (assignment.getStatus()
                == DoctorOpdAssignmentStatus.INACTIVE) {

            throw new IllegalArgumentException(
                    "Doctor-OPD assignment is already inactive"
            );
        }

        assignment.setStatus(
                DoctorOpdAssignmentStatus.INACTIVE
        );

        assignment.setUnassignedAt(
                java.time.LocalDateTime.now()
        );

        DoctorOpdAssignment updatedAssignment =
                assignmentRepository.save(assignment);

        return mapper.toResponse(updatedAssignment);
    }




}