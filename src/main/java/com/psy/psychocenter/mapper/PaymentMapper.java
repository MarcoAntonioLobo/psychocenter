package com.psy.psychocenter.mapper;

import org.springframework.stereotype.Component;

import com.psy.psychocenter.dto.PaymentRequestDTO;
import com.psy.psychocenter.dto.PaymentResponseDTO;
import com.psy.psychocenter.model.Patient;
import com.psy.psychocenter.model.Payment;

@Component
public class PaymentMapper {

    public Payment toEntity(PaymentRequestDTO dto, Patient patient) {
        return Payment.builder()
                .patient(patient)
                .packageType(dto.packageType())
                .amount(dto.amount())
                .paymentDate(dto.paymentDate())
                .status(dto.status())
                .build();
    }

    public PaymentResponseDTO toResponse(Payment payment) {
        return new PaymentResponseDTO(
                payment.getId(),
                payment.getPatient() != null ? payment.getPatient().getId() : null,
                payment.getPatient() != null ? payment.getPatient().getName() : null,
                payment.getPackageType(),
                payment.getAmount(),
                payment.getPaymentDate(),
                payment.getStatus()
        );
    }
}
