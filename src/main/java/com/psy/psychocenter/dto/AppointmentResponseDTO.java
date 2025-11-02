package com.psy.psychocenter.dto;

import java.time.LocalDateTime;

import com.psy.psychocenter.model.enums.AppointmentStatus;
import com.psy.psychocenter.model.enums.AppointmentType;

public record AppointmentResponseDTO(
        Long id,
        Long patientId,
        String patientName,
        Long patientPaymentId,
        Long supervisionId,
        String supervisorName,
        Long supervisionPaymentId,
        LocalDateTime dateTime,
        AppointmentType type,
        AppointmentStatus status,
        String notes
) {}
