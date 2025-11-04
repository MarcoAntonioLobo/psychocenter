package com.psy.psychocenter.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.psy.psychocenter.dto.AppointmentResponseDTO;
import com.psy.psychocenter.dto.PatientRequestDTO;
import com.psy.psychocenter.dto.PatientResponseDTO;
import com.psy.psychocenter.dto.PaymentResponseDTO;
import com.psy.psychocenter.exception.BusinessRuleException;
import com.psy.psychocenter.exception.ResourceNotFoundException;
import com.psy.psychocenter.mapper.AppointmentMapper;
import com.psy.psychocenter.mapper.PatientMapper;
import com.psy.psychocenter.mapper.PaymentMapper;
import com.psy.psychocenter.model.Patient;
import com.psy.psychocenter.repository.AppointmentRepository;
import com.psy.psychocenter.repository.PatientRepository;
import com.psy.psychocenter.repository.PaymentRepository;
import com.psy.psychocenter.service.PatientService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final PaymentRepository paymentRepository;

    private final PatientMapper patientMapper;
    private final AppointmentMapper appointmentMapper;
    private final PaymentMapper paymentMapper;

    @Override
    public PatientResponseDTO create(PatientRequestDTO dto) {
        patientRepository.findByEmail(dto.email()).ifPresent(p -> {
            throw new BusinessRuleException("Email already exists");
        });

        if (dto.packageCount() != null && dto.packageCount() < 0) {
            throw new BusinessRuleException("Package count cannot be negative");
        }

        Patient patient = patientMapper.toEntity(dto);
        Patient saved = patientRepository.save(patient);
        return patientMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientResponseDTO> findAll() {
        return patientRepository.findAll()
                .stream()
                .map(patientMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponseDTO findById(Long id) {
        return patientRepository.findById(id)
                .map(patientMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
    }

    @Override
    @Transactional
    public PatientResponseDTO update(Long id, PatientRequestDTO dto) {
        Patient existing = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        patientRepository.findByEmail(dto.email())
                .filter(p -> !p.getId().equals(id))
                .ifPresent(p -> { throw new BusinessRuleException("Email already exists"); });

        if (dto.packageCount() != null && dto.packageCount() < 0) {
            throw new BusinessRuleException("Package count cannot be negative");
        }

        existing.setName(dto.name());
        existing.setEmail(dto.email());
        existing.setPhone(dto.phone());
        existing.setPackageCount(dto.packageCount());

        Patient updated = patientRepository.save(existing);
        return patientMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Patient not found");
        }
        patientRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> getAppointments(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        return appointmentRepository.findByPatient(patient)
                .stream()
                .map(appointmentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getPayments(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found");
        }

        return paymentRepository.findByPatientId(patientId)
                .stream()
                .map(paymentMapper::toResponse)
                .toList();
    }
}
