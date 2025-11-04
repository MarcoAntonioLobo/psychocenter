package com.psy.psychocenter.service;

import java.util.List;

import com.psy.psychocenter.dto.AppointmentResponseDTO;
import com.psy.psychocenter.dto.PatientRequestDTO;
import com.psy.psychocenter.dto.PatientResponseDTO;
import com.psy.psychocenter.dto.PaymentResponseDTO;

public interface PatientService {

    PatientResponseDTO create(PatientRequestDTO dto);

    List<PatientResponseDTO> findAll();

    PatientResponseDTO findById(Long id);

    PatientResponseDTO update(Long id, PatientRequestDTO dto);

    void delete(Long id);

    List<AppointmentResponseDTO> getAppointments(Long patientId);

    List<PaymentResponseDTO> getPayments(Long patientId);
}
