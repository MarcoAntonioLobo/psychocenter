package com.psy.psychocenter.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.psy.psychocenter.dto.SupervisionRequestDTO;
import com.psy.psychocenter.dto.SupervisionResponseDTO;
import com.psy.psychocenter.mapper.SupervisionMapper;
import com.psy.psychocenter.model.Supervision;
import com.psy.psychocenter.repository.SupervisionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SupervisionService {

    private final SupervisionRepository supervisionRepository;
    private final SupervisionMapper supervisionMapper;

    @Transactional
    public SupervisionResponseDTO create(SupervisionRequestDTO dto) {
        Supervision supervision = supervisionMapper.toEntity(dto);
        Supervision saved = supervisionRepository.save(supervision);
        return supervisionMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<SupervisionResponseDTO> findAll() {
        return supervisionRepository.findAll()
                .stream()
                .map(supervisionMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SupervisionResponseDTO findById(Long id) {
        Supervision supervision = supervisionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supervisão não encontrada"));
        return supervisionMapper.toResponse(supervision);
    }

    @Transactional
    public SupervisionResponseDTO update(Long id, SupervisionRequestDTO dto) {
        Supervision existing = supervisionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supervisão não encontrada"));

        existing.setSupervisorName(dto.supervisorName());
        existing.setNotes(dto.notes());
        existing.setDateTime(dto.dateTime());

        Supervision updated = supervisionRepository.save(existing);
        return supervisionMapper.toResponse(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!supervisionRepository.existsById(id)) {
            throw new RuntimeException("Supervisão não encontrada");
        }
        supervisionRepository.deleteById(id);
    }
}
