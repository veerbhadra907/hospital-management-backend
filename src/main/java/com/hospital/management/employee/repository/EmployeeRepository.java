package com.hospital.management.employee.repository;

import com.hospital.management.employee.model.Employee;
import com.hospital.management.employee.model.EmployeeRole;
import com.hospital.management.employee.model.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmployeeId(String employeeId);

    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);

    Page<Employee> findByStatus(
            EmployeeStatus status,
            Pageable pageable
    );

    Page<Employee> findByRole(
            EmployeeRole role,
            Pageable pageable
    );

    Page<Employee> findByStatusAndRole(
            EmployeeStatus status,
            EmployeeRole role,
            Pageable pageable
    );


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    SELECT e
    FROM Employee e
    WHERE e.employeeId = :employeeId
""")
    Optional<Employee> findByEmployeeIdForUpdate(
            @Param("employeeId") String employeeId
    );








}