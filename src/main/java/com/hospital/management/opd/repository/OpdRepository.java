package com.hospital.management.opd.repository;

import com.hospital.management.opd.model.Opd;
import com.hospital.management.opd.model.OpdStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OpdRepository extends JpaRepository<Opd , Long>{

    Optional<Opd> findByOpdCode(String opdCode);

    boolean existsByOpdCode(String opdCode);

    boolean existsByNameIgnoreCase(String name);

    Page<Opd> findByStatus(
            OpdStatus status,
            Pageable pageable
    );


}
