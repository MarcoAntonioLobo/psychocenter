package com.psy.psychocenter.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.psy.psychocenter.dto.AppointmentRequestDTO;
import com.psy.psychocenter.dto.AppointmentResponseDTO;
import com.psy.psychocenter.mapper.AppointmentMapper;
import com.psy.psychocenter.model.Appointment;
import com.psy.psychocenter.model.Patient;
import com.psy.psychocenter.repository.AppointmentRepository;
import com.psy.psychocenter.repository.PatientRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final PatientRepository patientRepository;

    public AppointmentResponseDTO create(AppointmentRequestDTO dto) {
        Patient patient = patientRepository.findById(dto.patientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Appointment appointment = appointmentMapper.toEntity(dto, patient);
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
        Appointment existing = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        Patient patient = patientRepository.findById(dto.patientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        existing.setPatient(patient);
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
