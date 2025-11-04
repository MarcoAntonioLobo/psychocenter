package com.psy.psychocenter.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.psy.psychocenter.dto.AppointmentRequestDTO;
import com.psy.psychocenter.dto.AppointmentResponseDTO;
import com.psy.psychocenter.exception.BusinessRuleException;
import com.psy.psychocenter.exception.ResourceNotFoundException;
import com.psy.psychocenter.exception.ValidationException;
import com.psy.psychocenter.mapper.AppointmentMapper;
import com.psy.psychocenter.model.Appointment;
import com.psy.psychocenter.model.Patient;
import com.psy.psychocenter.model.Supervision;
import com.psy.psychocenter.repository.AppointmentRepository;
import com.psy.psychocenter.repository.PatientRepository;
import com.psy.psychocenter.repository.SupervisionRepository;
import com.psy.psychocenter.service.AppointmentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final PatientRepository patientRepository;
    private final SupervisionRepository supervisionRepository;

    @Override
    public AppointmentResponseDTO create(AppointmentRequestDTO dto) {
        if (dto.dateTime() == null) {
            throw new ValidationException("dateTime is required");
        }
        if (dto.dateTime().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Appointment cannot be scheduled in the past");
        }

        Patient patient = null;
        Supervision supervision = null;

        if (dto.patientId() != null) {
            patient = patientRepository.findById(dto.patientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

            if (patient.getPayments() == null || patient.getPayments().isEmpty()) {
                throw new BusinessRuleException("Patient must have at least one payment before scheduling an appointment");
            }
        }

        if (dto.supervisionId() != null) {
            supervision = supervisionRepository.findById(dto.supervisionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supervision not found"));

            if (supervision.getPayment() == null) {
                throw new BusinessRuleException("Supervision must have a payment before scheduling an appointment");
            }
        }

        Appointment appointment = appointmentMapper.toEntity(dto, patient, supervision);
        Appointment saved = appointmentRepository.save(appointment);

        return appointmentMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> findAll() {
        return appointmentRepository.findAll()
                .stream()
                .map(appointmentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentResponseDTO findById(Long id) {
        return appointmentRepository.findById(id)
                .map(appointmentMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
    }

    @Override
    public AppointmentResponseDTO update(Long id, AppointmentRequestDTO dto) {
        if (dto.dateTime() == null) {
            throw new ValidationException("dateTime is required");
        }
        if (dto.dateTime().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Appointment cannot be scheduled in the past");
        }

        Appointment existing = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        Patient patient = null;
        Supervision supervision = null;

        if (dto.patientId() != null) {
            patient = patientRepository.findById(dto.patientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
            if (patient.getPayments() == null || patient.getPayments().isEmpty()) {
                throw new BusinessRuleException("Patient must have at least one payment before updating an appointment");
            }
        }

        if (dto.supervisionId() != null) {
            supervision = supervisionRepository.findById(dto.supervisionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supervision not found"));
            if (supervision.getPayment() == null) {
                throw new BusinessRuleException("Supervision must have a payment before updating an appointment");
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

    @Override
    public void delete(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Appointment not found");
        }
        appointmentRepository.deleteById(id);
    }
}
