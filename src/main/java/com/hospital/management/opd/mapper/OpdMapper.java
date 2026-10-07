package com.hospital.management.opd.mapper;

import com.hospital.management.opd.dto.OpdCreateRequest;
import com.hospital.management.opd.dto.OpdResponse;
import com.hospital.management.opd.model.Opd;
import org.springframework.stereotype.Component;

@Component
public class OpdMapper {

    public Opd toEntity(OpdCreateRequest request) {

        Opd opd = new Opd();

        opd.setOpdCode(request.opdCode());
        opd.setName(request.name());
        opd.setDescription(request.description());

        return opd;
    }

    public OpdResponse toResponse(Opd opd) {

        return new OpdResponse(
                opd.getOpdCode(),
                opd.getName(),
                opd.getDescription(),
                opd.getStatus()
        );
    }


}
