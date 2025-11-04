package com.psy.psychocenter.service;

import java.util.List;

import com.psy.psychocenter.dto.PaymentRequestDTO;
import com.psy.psychocenter.dto.PaymentResponseDTO;
import com.psy.psychocenter.dto.SupervisionRequestDTO;
import com.psy.psychocenter.dto.SupervisionResponseDTO;

public interface SupervisionService {

    SupervisionResponseDTO create(SupervisionRequestDTO dto);

    List<SupervisionResponseDTO> findAll();

    SupervisionResponseDTO findById(Long id);

    SupervisionResponseDTO update(Long id, SupervisionRequestDTO dto);

    void delete(Long id);

    PaymentResponseDTO getPayment(Long supervisionId);

    PaymentResponseDTO createPayment(Long supervisionId, PaymentRequestDTO dto);

    PaymentResponseDTO updatePayment(Long supervisionId, PaymentRequestDTO dto);
}
