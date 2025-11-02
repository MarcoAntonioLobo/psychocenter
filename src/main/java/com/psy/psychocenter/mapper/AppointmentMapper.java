package com.psy.psychocenter.mapper;

import org.springframework.stereotype.Component;

import com.psy.psychocenter.dto.AppointmentRequestDTO;
import com.psy.psychocenter.dto.AppointmentResponseDTO;
import com.psy.psychocenter.model.Appointment;
import com.psy.psychocenter.model.Patient;
import com.psy.psychocenter.model.enums.AppointmentStatus;

@Component
public class AppointmentMapper {

    public Appointment toEntity(AppointmentRequestDTO dto, Patient patient) {
        return Appointment.builder()
                .patient(patient)
                .dateTime(dto.dateTime())
                .type(dto.type())
                .notes(dto.notes())
                .status(dto.status() != null ? dto.status() : AppointmentStatus.SCHEDULED)
                .build();
    }

    public AppointmentResponseDTO toResponse(Appointment appointment) {
        return new AppointmentResponseDTO(
                appointment.getId(),
                appointment.getPatient().getId(),
                appointment.getPatient().getName(),
                appointment.getDateTime(),
                appointment.getType(),
                appointment.getStatus(),
                appointment.getNotes()
        );
    }
}
