package com.hospital.management.opd.service;

import com.hospital.management.exception.DuplicateResourceException;
import com.hospital.management.opd.dto.OpdCreateRequest;
import com.hospital.management.opd.dto.OpdResponse;
import com.hospital.management.opd.mapper.OpdMapper;
import com.hospital.management.opd.model.Opd;
import com.hospital.management.opd.repository.OpdRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OpdService {

    private final OpdRepository opdRepository;
    private final OpdMapper opdMapper;

    public OpdService(
            OpdRepository opdRepository,
            OpdMapper opdMapper
    ) {
        this.opdRepository = opdRepository;
        this.opdMapper = opdMapper;
    }

    @Transactional
    public OpdResponse createOpd(OpdCreateRequest request) {

        if (opdRepository.existsByOpdCode(request.opdCode())) {
            throw new DuplicateResourceException(
                    "An OPD with code " + request.opdCode() + " already exists"
            );
        }

        if (opdRepository.existsByNameIgnoreCase(request.name())) {
            throw new DuplicateResourceException(
                    "An OPD with name " + request.name() + " already exists"
            );
        }

        Opd opd = opdMapper.toEntity(request);

        Opd savedOpd = opdRepository.save(opd);

        return opdMapper.toResponse(savedOpd);
    }
}