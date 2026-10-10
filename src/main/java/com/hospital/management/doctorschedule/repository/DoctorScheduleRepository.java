package com.hospital.management.doctorschedule.repository;

import com.hospital.management.doctorschedule.model.DoctorSchedule;
import com.hospital.management.employee.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;


public interface DoctorScheduleRepository extends  JpaRepository<DoctorSchedule , Long> {

    Page<DoctorSchedule> findByDoctor(Employee doctor, Pageable pageable);

    List<DoctorSchedule> findByDoctorAndDayOfWeekOrderByStartTime(
            Employee doctor,
            DayOfWeek dayOfWeek
    );

    @Query("""
        SELECT COUNT(s) > 0
        FROM DoctorSchedule s
        WHERE s.doctor = :doctor
          AND s.dayOfWeek = :dayOfWeek
          AND s.startTime < :endTime
          AND s.endTime > :startTime
    """)
    boolean existsOverlappingSchedule(
            @Param("doctor") Employee doctor,
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

}
