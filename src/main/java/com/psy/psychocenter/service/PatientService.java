package com.psy.psychocenter.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.psy.psychocenter.dto.AppointmentResponseDTO;
import com.psy.psychocenter.dto.PatientRequestDTO;
import com.psy.psychocenter.dto.PatientResponseDTO;
import com.psy.psychocenter.dto.PaymentResponseDTO;
import com.psy.psychocenter.mapper.AppointmentMapper;
import com.psy.psychocenter.mapper.PatientMapper;
import com.psy.psychocenter.mapper.PaymentMapper;
import com.psy.psychocenter.model.Patient;
import com.psy.psychocenter.repository.AppointmentRepository;
import com.psy.psychocenter.repository.PatientRepository;
import com.psy.psychocenter.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final PaymentRepository paymentRepository;

    private final PatientMapper patientMapper;
    private final AppointmentMapper appointmentMapper;
    private final PaymentMapper paymentMapper;

    // Criar paciente
    @Transactional
    public PatientResponseDTO create(PatientRequestDTO dto) {
        Patient patient = patientMapper.toEntity(dto);
        Patient saved = patientRepository.save(patient);
        return patientMapper.toResponse(saved);
    }

    // Buscar todos
    @Transactional(readOnly = true)
    public List<PatientResponseDTO> findAll() {
        return patientRepository.findAll()
                .stream()
                .map(patientMapper::toResponse)
                .toList();
    }

    // Buscar por ID
    @Transactional(readOnly = true)
    public PatientResponseDTO findById(Long id) {
        return patientRepository.findById(id)
                .map(patientMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
    }

    // Atualizar paciente
    @Transactional
    public PatientResponseDTO update(Long id, PatientRequestDTO dto) {
        Patient existing = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        existing.setName(dto.name());
        existing.setEmail(dto.email());
        existing.setPhone(dto.phone());
        existing.setPackageCount(dto.packageCount());

        Patient updated = patientRepository.save(existing);
        return patientMapper.toResponse(updated);
    }

    // Deletar paciente
    @Transactional
    public void delete(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new RuntimeException("Paciente não encontrado");
        }
        patientRepository.deleteById(id);
    }

    // Listar agendamentos do paciente
    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> getAppointments(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        return appointmentRepository.findByPatient(patient)
                .stream()
                .map(appointmentMapper::toResponse)
                .toList();
    }

    // Listar pagamentos do paciente
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getPayments(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new RuntimeException("Paciente não encontrado");
        }

        return paymentRepository.findByPatientId(patientId)
                .stream()
                .map(paymentMapper::toResponse)
                .toList();
    }
}
