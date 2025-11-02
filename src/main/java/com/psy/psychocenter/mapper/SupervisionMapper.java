package com.psy.psychocenter.mapper;

import org.springframework.stereotype.Component;

import com.psy.psychocenter.dto.PaymentResponseDTO;
import com.psy.psychocenter.dto.SupervisionRequestDTO;
import com.psy.psychocenter.dto.SupervisionResponseDTO;
import com.psy.psychocenter.model.Payment;
import com.psy.psychocenter.model.Supervision;
import com.psy.psychocenter.model.enums.SupervisionStatus;

@Component
public class SupervisionMapper {

    public Supervision toEntity(SupervisionRequestDTO dto, Payment payment) {
        return Supervision.builder()
                .supervisorName(dto.supervisorName())
                .notes(dto.notes())
                .dateTime(dto.dateTime())
                .status(SupervisionStatus.SCHEDULED)
                .payment(payment)
                .build();
    }

    public SupervisionResponseDTO toResponse(Supervision supervision) {
        PaymentResponseDTO paymentDTO = null;
        Payment payment = supervision.getPayment();
        if (payment != null) {
            paymentDTO = new PaymentResponseDTO(
                    payment.getId(),
                    payment.getPatient() != null ? payment.getPatient().getId() : null,
                    payment.getPatient() != null ? payment.getPatient().getName() : null,
                    payment.getPackageType(),
                    payment.getAmount(),
                    payment.getPaymentDate(),
                    payment.getStatus()
            );
        } 

        return new SupervisionResponseDTO(
                supervision.getId(),
                supervision.getSupervisorName(),
                supervision.getNotes(),
                supervision.getDateTime(),
                supervision.getStatus(),
                paymentDTO
        );
    }
}
