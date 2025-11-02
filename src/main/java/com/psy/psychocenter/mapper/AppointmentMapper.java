package com.psy.psychocenter.mapper;

import org.springframework.stereotype.Component;

import com.psy.psychocenter.dto.AppointmentRequestDTO;
import com.psy.psychocenter.dto.AppointmentResponseDTO;
import com.psy.psychocenter.model.Appointment;
import com.psy.psychocenter.model.Patient;
import com.psy.psychocenter.model.Supervision;
import com.psy.psychocenter.model.Payment;
import com.psy.psychocenter.model.enums.AppointmentStatus;

@Component
public class AppointmentMapper {

    public Appointment toEntity(AppointmentRequestDTO dto, Patient patient, Supervision supervision) {
        return Appointment.builder()
                .patient(patient)
                .supervision(supervision)
                .dateTime(dto.dateTime())
                .type(dto.type())
                .notes(dto.notes())
                .status(dto.status() != null ? dto.status() : AppointmentStatus.SCHEDULED)
                .build();
    }

    public AppointmentResponseDTO toResponse(Appointment appointment) {
        Long patientId = appointment.getPatient() != null ? appointment.getPatient().getId() : null;
        String patientName = appointment.getPatient() != null ? appointment.getPatient().getName() : null;

        Long patientPaymentId = null;
        if (appointment.getPatient() != null && !appointment.getPatient().getPayments().isEmpty()) {
            Payment lastPayment = appointment.getPatient().getPayments()
                                             .get(appointment.getPatient().getPayments().size() - 1);
            patientPaymentId = lastPayment.getId();
        }

        Long supervisionId = appointment.getSupervision() != null ? appointment.getSupervision().getId() : null;
        String supervisorName = appointment.getSupervision() != null ? appointment.getSupervision().getSupervisorName() : null;
        Long supervisionPaymentId = appointment.getSupervision() != null && appointment.getSupervision().getPayment() != null
                                   ? appointment.getSupervision().getPayment().getId() : null;

        return new AppointmentResponseDTO(
                appointment.getId(),
                patientId,
                patientName,
                patientPaymentId,
                supervisionId,
                supervisorName,
                supervisionPaymentId,
                appointment.getDateTime(),
                appointment.getType(),
                appointment.getStatus(),
                appointment.getNotes()
        );
    }
}
