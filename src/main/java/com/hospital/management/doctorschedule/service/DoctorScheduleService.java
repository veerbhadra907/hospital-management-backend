package com.hospital.management.doctorschedule.service;

import com.hospital.management.exception.DuplicateResourceException;
import com.hospital.management.exception.ResourceNotFoundException;
import com.hospital.management.doctorschedule.dto.DoctorScheduleCreateRequest;
import com.hospital.management.doctorschedule.dto.DoctorScheduleResponse;
import com.hospital.management.doctorschedule.mapper.DoctorScheduleMapper;
import com.hospital.management.doctorschedule.model.DoctorSchedule;
import com.hospital.management.doctorschedule.repository.DoctorScheduleRepository;
import com.hospital.management.employee.model.Employee;
import com.hospital.management.employee.model.EmployeeRole;
import com.hospital.management.employee.model.EmployeeStatus;
import com.hospital.management.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorScheduleService {

    private final DoctorScheduleRepository scheduleRepository;
    private final EmployeeRepository employeeRepository;
    private final DoctorScheduleMapper scheduleMapper;

    @Transactional
    public DoctorScheduleResponse createSchedule(
            DoctorScheduleCreateRequest request) {

        // 1. Find doctor
        Employee doctor = employeeRepository
                .findByEmployeeId(request.employeeId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor not found with employee ID: "
                                        + request.employeeId()
                        ));

        // 2. Employee must be a doctor
        if (doctor.getRole() != EmployeeRole.DOCTOR) {
            throw new IllegalArgumentException(
                    "Employee is not a doctor"
            );
        }

        // 3. Doctor must be active
        if (doctor.getStatus() != EmployeeStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Doctor is not active"
            );
        }

        // 4. Validate time range
        LocalTime startTime = request.startTime();
        LocalTime endTime = request.endTime();

        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException(
                    "Start time must be before end time"
            );
        }

        // 5. Validate slot duration
        int slotDuration = request.slotDurationMinutes();

        long totalMinutes =
                Duration.between(startTime, endTime).toMinutes();

        if (slotDuration > totalMinutes) {
            throw new IllegalArgumentException(
                    "Slot duration cannot be greater than the schedule duration"
            );
        }

        // 6. Ensure schedule divides into complete slots
        if (totalMinutes % slotDuration != 0) {
            throw new IllegalArgumentException(
                    "Schedule duration must be exactly divisible by slot duration"
            );
        }

        // 7. Prevent overlapping schedules
        boolean overlapping =
                scheduleRepository.existsOverlappingSchedule(
                        doctor,
                        request.dayOfWeek(),
                        startTime,
                        endTime
                );

        if (overlapping) {
            throw new DuplicateResourceException(
                    "Doctor already has an overlapping schedule on "
                            + request.dayOfWeek()
            );
        }

        // 8. Create entity
        DoctorSchedule schedule = new DoctorSchedule();

        schedule.setDoctor(doctor);
        schedule.setDayOfWeek(request.dayOfWeek());
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        schedule.setSlotDurationMinutes(slotDuration);

        // 9. Save
        DoctorSchedule savedSchedule =
                scheduleRepository.save(schedule);

        // 10. Convert to response
        return scheduleMapper.toResponse(savedSchedule);
    }

    @Transactional(readOnly = true)
    public Page<DoctorScheduleResponse> getDoctorSchedules(
            String employeeId,
            Pageable pageable) {

        Employee doctor = employeeRepository
                .findByEmployeeId(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor not found with employee ID: "
                                        + employeeId
                        ));

        if (doctor.getRole() != EmployeeRole.DOCTOR) {
            throw new IllegalArgumentException(
                    "Employee is not a doctor"
            );
        }

        return scheduleRepository
                .findByDoctor(doctor, pageable)
                .map(scheduleMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<DoctorScheduleResponse> getDoctorSchedulesForDay(
            String employeeId,
            java.time.DayOfWeek dayOfWeek) {

        Employee doctor = employeeRepository
                .findByEmployeeId(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor not found with employee ID: "
                                        + employeeId
                        ));

        if (doctor.getRole() != EmployeeRole.DOCTOR) {
            throw new IllegalArgumentException(
                    "Employee is not a doctor"
            );
        }

        return scheduleRepository
                .findByDoctorAndDayOfWeekOrderByStartTime(
                        doctor,
                        dayOfWeek
                )
                .stream()
                .map(scheduleMapper::toResponse)
                .toList();
    }
}