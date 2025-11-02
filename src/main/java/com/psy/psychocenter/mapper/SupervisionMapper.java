package com.psy.psychocenter.mapper;

import org.springframework.stereotype.Component;

import com.psy.psychocenter.dto.SupervisionRequestDTO;
import com.psy.psychocenter.dto.SupervisionResponseDTO;
import com.psy.psychocenter.model.Supervision;
import com.psy.psychocenter.model.enums.AppointmentStatus;

@Component
public class SupervisionMapper {

    public Supervision toEntity(SupervisionRequestDTO dto) {
        return Supervision.builder()
                .supervisorName(dto.supervisorName())
                .notes(dto.notes())
                .dateTime(dto.dateTime())
                .status(AppointmentStatus.SCHEDULED)
                .build();
    }

    public SupervisionResponseDTO toResponse(Supervision supervision) {
        return new SupervisionResponseDTO(
                supervision.getId(),
                supervision.getSupervisorName(),
                supervision.getNotes(),
                supervision.getDateTime(),
                supervision.getStatus()
        );
    }
}
