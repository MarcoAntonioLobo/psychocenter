package com.psy.psychocenter.dto;

import java.util.List;

import com.psy.psychocenter.model.enums.GroupType;

public record GroupRequestDTO(
    String name,
    GroupType type,
    List<Long> patientIds,
    List<Long> supervisionIds,
    Long paymentId,
    Long appointmentId
) {}
