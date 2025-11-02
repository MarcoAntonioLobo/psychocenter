package com.psy.psychocenter.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.psy.psychocenter.model.enums.PackageType;
import com.psy.psychocenter.model.enums.PaymentStatus;

public record PaymentRequestDTO(
    Long patientId,
    PackageType packageType,
    BigDecimal amount,
    LocalDate paymentDate,
    PaymentStatus status
) {}
