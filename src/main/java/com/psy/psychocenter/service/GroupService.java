package com.psy.psychocenter.service;

import java.util.List;

import com.psy.psychocenter.dto.GroupRequestDTO;
import com.psy.psychocenter.dto.GroupResponseDTO;
import com.psy.psychocenter.dto.PatientResponseDTO;
import com.psy.psychocenter.dto.SupervisionResponseDTO;

public interface GroupService {

    GroupResponseDTO create(GroupRequestDTO dto);

    List<GroupResponseDTO> findAll();

    GroupResponseDTO findById(Long id);

    GroupResponseDTO update(Long id, GroupRequestDTO dto);

    void delete(Long id);

    List<PatientResponseDTO> getPatients(Long groupId);

    List<SupervisionResponseDTO> getSupervisions(Long groupId);
}
