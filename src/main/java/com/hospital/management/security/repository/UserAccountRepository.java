package com.hospital.management.security.repository;

import com.hospital.management.security.model.UserAccount;
import com.hospital.management.security.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository  extends  JpaRepository<UserAccount , Long>{

    Optional<UserAccount> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmployeeId(Long employeeId);

    boolean existsByPatientId(Long patientId);

    Optional<UserAccount> findByEmailIgnoreCaseAndEnabledTrue(
            String email
    );
}
