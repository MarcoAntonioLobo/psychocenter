package com.psy.psychocenter.service;

import java.util.List;

import com.psy.psychocenter.dto.PaymentRequestDTO;
import com.psy.psychocenter.dto.PaymentResponseDTO;

public interface PaymentService {

    PaymentResponseDTO create(PaymentRequestDTO dto);

    List<PaymentResponseDTO> getAll();

    PaymentResponseDTO getById(Long id);

    List<PaymentResponseDTO> getByPatient(Long patientId);

    PaymentResponseDTO update(Long id, PaymentRequestDTO dto);

    void delete(Long id);
}
