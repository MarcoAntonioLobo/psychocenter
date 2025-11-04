package com.psy.psychocenter.service;

import java.util.List;

import com.psy.psychocenter.dto.AppointmentRequestDTO;
import com.psy.psychocenter.dto.AppointmentResponseDTO;

public interface AppointmentService {

    AppointmentResponseDTO create(AppointmentRequestDTO dto);

    List<AppointmentResponseDTO> findAll();

    AppointmentResponseDTO findById(Long id);

    AppointmentResponseDTO update(Long id, AppointmentRequestDTO dto);

    void delete(Long id);
}
