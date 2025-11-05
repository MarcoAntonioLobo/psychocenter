package com.psy.psychocenter.mapper;

import java.util.List;

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
                .build();
    }

    public SupervisionResponseDTO toResponse(Supervision supervision) {
        List<PaymentResponseDTO> payments = supervision.getPayments().stream()
            .map(p -> new PaymentResponseDTO(
                    p.getId(),
                    p.getPatient() != null ? p.getPatient().getId() : null,
                    p.getPatient() != null ? p.getPatient().getName() : null,
                    p.getPackageType(),
                    p.getAmount(),
                    p.getPaymentDate(),
                    p.getStatus()
            ))
            .toList();

        return new SupervisionResponseDTO(
                supervision.getId(),
                supervision.getSupervisorName(),
                supervision.getNotes(),
                supervision.getDateTime(),
                supervision.getStatus(),
                payments
        );
    }
}