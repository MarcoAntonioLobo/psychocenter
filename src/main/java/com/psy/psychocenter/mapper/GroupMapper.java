package com.psy.psychocenter.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.psy.psychocenter.dto.GroupRequestDTO;
import com.psy.psychocenter.dto.GroupResponseDTO;
import com.psy.psychocenter.model.Appointment;
import com.psy.psychocenter.model.Group;
import com.psy.psychocenter.model.Patient;
import com.psy.psychocenter.model.Payment;
import com.psy.psychocenter.model.Supervision;

@Component
public class GroupMapper {

    public Group toEntity(GroupRequestDTO dto,
                          List<Patient> patients,
                          List<Supervision> supervisions,
                          Payment payment,
                          Appointment appointment) {
        return Group.builder()
                .name(dto.name())
                .type(dto.type())
                .patients(patients)
                .supervisions(supervisions)
                .payment(payment)
                .appointment(appointment)
                .build();
    }

    public GroupResponseDTO toResponse(Group group) {
        List<Long> patientIds = group.getPatients() != null
                ? group.getPatients().stream().map(Patient::getId).toList()
                : List.of();

        List<Long> supervisionIds = group.getSupervisions() != null
                ? group.getSupervisions().stream().map(Supervision::getId).toList()
                : List.of();

        return new GroupResponseDTO(
                group.getId(),
                group.getName(),
                group.getType(),
                patientIds,
                supervisionIds,
                group.getPayment() != null ? group.getPayment().getId() : null,
                group.getAppointment() != null ? group.getAppointment().getId() : null
        );
    }
}
