package com.hospital.management.opd.dto;

import com.hospital.management.opd.model.OpdStatus;

public record OpdResponse (
        String opdCode,

        String name,

        String description,

        OpdStatus status
) {

}
