package com.psy.psychocenter.mapper;

import org.springframework.stereotype.Component;

import com.psy.psychocenter.dto.PatientRequestDTO;
import com.psy.psychocenter.dto.PatientResponseDTO;
import com.psy.psychocenter.model.Patient;

@Component
public class PatientMapper {

    public Patient toEntity(PatientRequestDTO dto) {
        return Patient.builder()
                .name(dto.name())
                .email(dto.email())
                .phone(dto.phone())
                .packageCount(dto.packageCount())
                .build();
    }

    public PatientResponseDTO toResponse(Patient patient) {
        return new PatientResponseDTO(
                patient.getId(),
                patient.getName(),
                patient.getEmail(),
                patient.getPhone(),
                patient.getPackageCount(),
                patient.getSessionsUsed()
        );
    }
}
