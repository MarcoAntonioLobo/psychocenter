package com.psy.psychocenter.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.psy.psychocenter.dto.AppointmentRequestDTO;
import com.psy.psychocenter.dto.AppointmentResponseDTO;
import com.psy.psychocenter.mapper.AppointmentMapper;
import com.psy.psychocenter.model.Appointment;
import com.psy.psychocenter.model.Patient;
import com.psy.psychocenter.model.Supervision;
import com.psy.psychocenter.repository.AppointmentRepository;
import com.psy.psychocenter.repository.PatientRepository;
import com.psy.psychocenter.repository.SupervisionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final PatientRepository patientRepository;
    private final SupervisionRepository supervisionRepository;

    public AppointmentResponseDTO create(AppointmentRequestDTO dto) {
        if (dto.dateTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Appointment cannot be scheduled in the past");
        }

        Patient patient = null;
        Supervision supervision = null;

        if (dto.patientId() != null) {
            patient = patientRepository.findById(dto.patientId())
                    .orElseThrow(() -> new RuntimeException("Patient not found"));

            if (patient.getPayments().isEmpty()) {
                throw new RuntimeException("Patient must have at least one payment before scheduling an appointment");
            }
        }

        if (dto.supervisionId() != null) {
            supervision = supervisionRepository.findById(dto.supervisionId())
                    .orElseThrow(() -> new RuntimeException("Supervision not found"));

            if (supervision.getPayment() == null) {
                throw new RuntimeException("Supervision must have a payment before scheduling an appointment");
            }
        }

        Appointment appointment = appointmentMapper.toEntity(dto, patient, supervision);
        Appointment saved = appointmentRepository.save(appointment);

        return appointmentMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> findAll() {
        return appointmentRepository.findAll()
                .stream()
                .map(appointmentMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AppointmentResponseDTO findById(Long id) {
        return appointmentRepository.findById(id)
                .map(appointmentMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }

    public AppointmentResponseDTO update(Long id, AppointmentRequestDTO dto) {
        if (dto.dateTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Appointment cannot be scheduled in the past");
        }

        Appointment existing = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        Patient patient = null;
        Supervision supervision = null;

        if (dto.patientId() != null) {
            patient = patientRepository.findById(dto.patientId())
                    .orElseThrow(() -> new RuntimeException("Patient not found"));
            if (patient.getPayments().isEmpty()) {
                throw new RuntimeException("Patient must have at least one payment before updating an appointment");
            }
        }

        if (dto.supervisionId() != null) {
            supervision = supervisionRepository.findById(dto.supervisionId())
                    .orElseThrow(() -> new RuntimeException("Supervision not found"));
            if (supervision.getPayment() == null) {
                throw new RuntimeException("Supervision must have a payment before updating an appointment");
            }
        }

        existing.setPatient(patient);
        existing.setSupervision(supervision);
        existing.setDateTime(dto.dateTime());
        existing.setType(dto.type());
        existing.setStatus(dto.status());
        existing.setNotes(dto.notes());

        Appointment updated = appointmentRepository.save(existing);
        return appointmentMapper.toResponse(updated);
    }

    public void delete(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new RuntimeException("Appointment not found");
        }
        appointmentRepository.deleteById(id);
    }
}
