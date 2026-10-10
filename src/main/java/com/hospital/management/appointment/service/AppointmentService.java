package com.hospital.management.appointment.service;

import com.hospital.management.appointment.dto.AppointmentCreateRequest;
import com.hospital.management.appointment.dto.AppointmentResponse;
import com.hospital.management.appointment.mapper.AppointmentMapper;
import com.hospital.management.appointment.model.Appointment;
import com.hospital.management.appointment.model.AppointmentStatus;
import com.hospital.management.appointment.repository.AppointmentRepository;
import com.hospital.management.exception.DuplicateResourceException;
import com.hospital.management.exception.ResourceNotFoundException;
import com.hospital.management.doctorassignment.model.DoctorOpdAssignmentStatus;
import com.hospital.management.doctorassignment.repository.DoctorOpdAssignmentRepository;
import com.hospital.management.employee.model.Employee;
import com.hospital.management.employee.model.EmployeeRole;
import com.hospital.management.employee.model.EmployeeStatus;
import com.hospital.management.employee.repository.EmployeeRepository;
import com.hospital.management.opd.model.Opd;
import com.hospital.management.opd.model.OpdStatus;
import com.hospital.management.opd.repository.OpdRepository;
import com.hospital.management.patient.model.Patient;
import com.hospital.management.patient.model.PatientStatus;
import com.hospital.management.patient.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hospital.management.opd.model.Opd;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.hospital.management.appointment.dto.AppointmentSlotResponse;
import com.hospital.management.doctorschedule.model.DoctorSchedule;
import com.hospital.management.doctorschedule.repository.DoctorScheduleRepository;
import com.hospital.management.appointment.model.AppointmentStatus;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import java.time.LocalTime;
import java.util.EnumSet;
import java.util.UUID;


@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final EmployeeRepository employeeRepository;
    private final OpdRepository opdRepository;
    private final DoctorOpdAssignmentRepository assignmentRepository;
    private final AppointmentMapper mapper;
    private final DoctorScheduleRepository doctorScheduleRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            EmployeeRepository employeeRepository,
            OpdRepository opdRepository,
            DoctorOpdAssignmentRepository assignmentRepository,
            AppointmentMapper mapper, DoctorScheduleRepository doctorScheduleRepository
    ){
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.employeeRepository = employeeRepository;
        this.opdRepository = opdRepository;
        this.assignmentRepository = assignmentRepository;
        this.mapper = mapper;
        this.doctorScheduleRepository = doctorScheduleRepository;
    }

    @Transactional
    public AppointmentResponse createAppointment(AppointmentCreateRequest request)
    {

        // 1. Validate time range
        if(!request.startTime().isBefore(request.endTime())){
            throw  new IllegalArgumentException("Start time must be before the endtime");
        }

        // 2. Find patient
        Patient patient = patientRepository
                .findByPatientIdForUpdate(request.patientId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Patient not found with ID: "
                                        + request.patientId()
                        )
                );

        // 3. Verify patient is active
        if (patient.getStatus() != PatientStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Patient is not active"
            );
        }

        // 4. find employee
        Employee doctor = employeeRepository
                .findByEmployeeIdForUpdate(request.employeeId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor not found with employee ID: "
                                        + request.employeeId()
                        )
                );

        // 5. Verify employee is a doctor
        if (doctor.getRole() != EmployeeRole.DOCTOR) {
            throw new IllegalArgumentException(
                    "Employee is not a doctor"
            );
        }


        // 6. Verify doctor is active
        if (doctor.getStatus() != EmployeeStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Doctor is not active"
            );
        }

        // 7. Find OPD
        Opd opd = opdRepository
                .findByOpdCode(request.opdCode())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "OPD not found with code: "
                                        + request.opdCode()
                        )
                );

        // 8. Verify OPD is active
        if (opd.getStatus() != OpdStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "OPD is not active"
            );
        }

        // 9. Verify doctor is assigned to this OPD
        boolean assignedToOpd =
                assignmentRepository
                        .existsByDoctorAndOpdAndStatus(
                                doctor,
                                opd,
                                DoctorOpdAssignmentStatus.ACTIVE
                        );

        if (!assignedToOpd) {
            throw new IllegalArgumentException(
                    "Doctor is not assigned to this OPD"
            );
        }

        // 10. Appointment statuses that block a time slot
        EnumSet<AppointmentStatus> blockingStatuses =
                EnumSet.of(
                        AppointmentStatus.SCHEDULED,
                        AppointmentStatus.CONFIRMED,
                        AppointmentStatus.IN_PROGRESS
                );

        // 11. Check doctor availability
        boolean doctorBusy =
                appointmentRepository
                        .existsOverlappingDoctorAppointment(
                                doctor,
                                request.appointmentDate(),
                                request.startTime(),
                                request.endTime(),
                                blockingStatuses
                        );

        if (doctorBusy) {
            throw new DuplicateResourceException(
                    "Doctor already has an appointment during this time"
            );
        }



        List<DoctorSchedule> schedules =
                doctorScheduleRepository
                        .findByDoctorAndDayOfWeekOrderByStartTime(
                                doctor,
                                request.appointmentDate().getDayOfWeek()
                        );

        boolean validSlot = schedules.stream().anyMatch(schedule -> {

            LocalTime scheduleStart = schedule.getStartTime();
            LocalTime scheduleEnd = schedule.getEndTime();

            LocalTime appointmentStart = request.startTime();
            LocalTime appointmentEnd = request.endTime();

            int slotDuration = schedule.getSlotDurationMinutes();

            long requestedDuration =
                    Duration.between(
                            appointmentStart,
                            appointmentEnd
                    ).toMinutes();

            long minutesFromScheduleStart =
                    Duration.between(
                            scheduleStart,
                            appointmentStart
                    ).toMinutes();

            return !appointmentStart.isBefore(scheduleStart)
                    && !appointmentEnd.isAfter(scheduleEnd)
                    && requestedDuration == slotDuration
                    && minutesFromScheduleStart >= 0
                    && minutesFromScheduleStart % slotDuration == 0;
        });

        if (!validSlot) {
            throw new IllegalArgumentException(
                    "Appointment must match a valid slot within the doctor's schedule"
            );
        }



        // 12. Check patient availability
        boolean patientBusy =
                appointmentRepository
                        .existsOverlappingPatientAppointment(
                                patient,
                                request.appointmentDate(),
                                request.startTime(),
                                request.endTime(),
                                blockingStatuses
                        );

        if (patientBusy) {
            throw new DuplicateResourceException(
                    "Patient already has an appointment during this time"
            );
        }

        // 13. Create appointment
        Appointment appointment = new Appointment();

        appointment.setAppointmentId(generateAppointmentId());
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setOpd(opd);
        appointment.setAppointmentDate(
                request.appointmentDate()
        );
        appointment.setStartTime(request.startTime());
        appointment.setEndTime(request.endTime());
        appointment.setReason(request.reason());
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        // 14. Save
        Appointment savedAppointment =
                appointmentRepository.save(appointment);


        // 15. Convert to response
        return mapper.toResponse(savedAppointment);








    }



    @Transactional(readOnly = true)
    public Page<AppointmentResponse> getPatientAppointments(
            String patientId,
            Pageable pageable
    ) {

        Patient patient = patientRepository
                .findByPatientId(patientId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Patient not found with ID: " + patientId
                        )
                );

        return appointmentRepository
                .findByPatientAndStatusIn(
                        patient,
                        EnumSet.allOf(AppointmentStatus.class),
                        pageable
                )
                .map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponse> getDoctorAppointments(
            String employeeId,
            java.time.LocalDate date,
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

        return appointmentRepository
                .findByDoctorAndAppointmentDateAndStatusIn(
                        doctor,
                        date,
                        EnumSet.allOf(AppointmentStatus.class),
                        pageable
                )
                .map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponse> getOpdAppointments(
            String opdCode,
            java.time.LocalDate date,
            Pageable pageable
    ) {

        Opd opd = opdRepository
                .findByOpdCode(opdCode)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "OPD not found with code: " + opdCode
                        )
                );

        return appointmentRepository
                .findByOpdAndAppointmentDateAndStatusIn(
                        opd,
                        date,
                        EnumSet.allOf(AppointmentStatus.class),
                        pageable
                )
                .map(mapper::toResponse);
    }

    @Transactional
    public AppointmentResponse cancelAppointment(String appointmentId) {

        Appointment appointment = appointmentRepository
                .findByAppointmentId(appointmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Appointment not found with ID: "
                                        + appointmentId
                        )
                );

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalArgumentException(
                    "Appointment is already cancelled"
            );
        }

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalArgumentException(
                    "Completed appointment cannot be cancelled"
            );
        }

        if (appointment.getStatus() == AppointmentStatus.NO_SHOW) {
            throw new IllegalArgumentException(
                    "No-show appointment cannot be cancelled"
            );
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);

        Appointment updatedAppointment =
                appointmentRepository.save(appointment);

        return mapper.toResponse(updatedAppointment);
    }



    private String generateAppointmentId(){
        return "APT-" + UUID.randomUUID();
    }

    @Transactional(readOnly = true)
    public List<AppointmentSlotResponse> getAvailableSlots(
            String employeeId,
            LocalDate date) {

        // 1. Find the doctor
        Employee doctor = employeeRepository
                .findByEmployeeId(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor not found with employee ID: "
                                        + employeeId
                        ));

        // 2. Validate the employee's role
        if (doctor.getRole() != EmployeeRole.DOCTOR) {
            throw new IllegalArgumentException(
                    "Employee is not a doctor"
            );
        }

        // 3. Only active doctors can offer appointment slots
        if (doctor.getStatus() != EmployeeStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Doctor is not active"
            );
        }

        // 4. Load schedules for the requested day
        List<DoctorSchedule> schedules =
                doctorScheduleRepository
                        .findByDoctorAndDayOfWeekOrderByStartTime(
                                doctor,
                                date.getDayOfWeek()
                        );

        // 5. Define appointment statuses that block a slot
        List<AppointmentStatus> blockingStatuses = List.of(
                AppointmentStatus.SCHEDULED,
                AppointmentStatus.CONFIRMED,
                AppointmentStatus.IN_PROGRESS
        );

        List<AppointmentSlotResponse> availableSlots =
                new ArrayList<>();

        // 6. Generate slots from each configured working period
        for (DoctorSchedule schedule : schedules) {

            LocalTime slotStart = schedule.getStartTime();

            while (true) {

                LocalTime slotEnd = slotStart.plusMinutes(
                        schedule.getSlotDurationMinutes()
                );

                // Do not generate a slot extending past working hours
                if (slotEnd.isAfter(schedule.getEndTime())) {
                    break;
                }

                // 7. Check whether an appointment overlaps this slot
                boolean occupied =
                        appointmentRepository
                                .existsOverlappingDoctorAppointment(
                                        doctor,
                                        date,
                                        slotStart,
                                        slotEnd,
                                        blockingStatuses
                                );

                availableSlots.add(
                        new AppointmentSlotResponse(
                                slotStart,
                                slotEnd,
                                !occupied
                        )
                );

                slotStart = slotEnd;
            }
        }

        return availableSlots;
    }
    private void generateSlots(
            Employee doctor,
            LocalDate date,
            LocalTime start,
            LocalTime end,
            List<AppointmentSlotResponse> slots
    ) {

        LocalTime current = start;

        EnumSet<AppointmentStatus> blockingStatuses =
                EnumSet.of(
                        AppointmentStatus.SCHEDULED,
                        AppointmentStatus.CONFIRMED,
                        AppointmentStatus.IN_PROGRESS
                );

        while (current.isBefore(end)) {

            LocalTime slotEnd = current.plusMinutes(30);

            if (slotEnd.isAfter(end)) {
                break;
            }

            boolean occupied =
                    appointmentRepository
                            .existsOverlappingDoctorAppointment(
                                    doctor,
                                    date,
                                    current,
                                    slotEnd,
                                    blockingStatuses
                            );

            slots.add(
                    new AppointmentSlotResponse(
                            current,
                            slotEnd,
                            !occupied
                    )
            );

            current = slotEnd;
        }
    }

    @Transactional
    public AppointmentResponse updateAppointmentStatus(
            String appointmentId,
            AppointmentStatus newStatus) {

        Appointment appointment = appointmentRepository
                .findByAppointmentId(appointmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Appointment not found: " + appointmentId
                        ));

        AppointmentStatus currentStatus = appointment.getStatus();

        if (currentStatus == newStatus) {
            throw new IllegalArgumentException(
                    "Appointment already has status: " + currentStatus
            );
        }

        boolean allowed = switch (currentStatus) {
            case SCHEDULED ->
                    newStatus == AppointmentStatus.CONFIRMED
                            || newStatus == AppointmentStatus.CANCELLED
                            || newStatus == AppointmentStatus.NO_SHOW;

            case CONFIRMED ->
                    newStatus == AppointmentStatus.IN_PROGRESS
                            || newStatus == AppointmentStatus.CANCELLED
                            || newStatus == AppointmentStatus.NO_SHOW;

            case IN_PROGRESS ->
                    newStatus == AppointmentStatus.COMPLETED;

            case COMPLETED, CANCELLED, NO_SHOW -> false;
        };

        if (!allowed) {
            throw new IllegalArgumentException(
                    "Cannot change appointment status from "
                            + currentStatus + " to " + newStatus
            );
        }

        appointment.setStatus(newStatus);

        Appointment savedAppointment =
                appointmentRepository.save(appointment);

        return mapper.toResponse(savedAppointment);
    }



}
