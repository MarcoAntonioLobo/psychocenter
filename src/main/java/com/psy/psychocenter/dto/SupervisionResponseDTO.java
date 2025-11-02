package com.psy.psychocenter.dto;

import java.time.LocalDateTime;

import com.psy.psychocenter.model.enums.AppointmentStatus;

public record SupervisionResponseDTO(
    Long id,
    String supervisorName,
    String notes,
    LocalDateTime dateTime,
    AppointmentStatus status
) {}
