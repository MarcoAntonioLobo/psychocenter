package com.psy.psychocenter.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.psy.psychocenter.model.enums.SupervisionStatus;

public record SupervisionResponseDTO(
        Long id,
        String supervisorName,
        String notes,
        LocalDateTime dateTime,
        SupervisionStatus status,
        List<PaymentResponseDTO> payments
) {}
