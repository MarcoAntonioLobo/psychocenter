package com.psy.psychocenter.dto;

import java.time.LocalDateTime;

import com.psy.psychocenter.model.enums.SupervisionStatus;

public record SupervisionResponseDTO(
        Long id,
        String supervisorName,
        String notes,
        LocalDateTime dateTime,
        SupervisionStatus status,
        PaymentResponseDTO payment
) {}
