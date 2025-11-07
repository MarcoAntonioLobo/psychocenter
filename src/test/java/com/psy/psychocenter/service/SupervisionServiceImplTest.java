package com.psy.psychocenter.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.psy.psychocenter.dto.SupervisionRequestDTO;
import com.psy.psychocenter.dto.SupervisionResponseDTO;
import com.psy.psychocenter.exception.ValidationException;
import com.psy.psychocenter.mapper.SupervisionMapper;
import com.psy.psychocenter.model.Supervision;
import com.psy.psychocenter.repository.SupervisionRepository;
import com.psy.psychocenter.service.impl.SupervisionServiceImpl;

class SupervisionServiceImplTest {

    @InjectMocks
    private SupervisionServiceImpl service;

    @Mock private SupervisionRepository supervisionRepository;
    @Mock private SupervisionMapper supervisionMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createSupervisionSuccess() {
        SupervisionRequestDTO dto = new SupervisionRequestDTO("Dr. Smith", "Notes", LocalDateTime.now().plusDays(1), null);
        Supervision supervision = new Supervision();
        SupervisionResponseDTO response = new SupervisionResponseDTO(1L, "Dr. Smith", "Notes", dto.dateTime(), null, List.of());

        when(supervisionMapper.toEntity(dto, null)).thenReturn(supervision);
        when(supervisionRepository.save(supervision)).thenReturn(supervision);
        when(supervisionMapper.toResponse(supervision)).thenReturn(response);

        SupervisionResponseDTO result = service.create(dto);
        assertNotNull(result);
        assertEquals("Dr. Smith", result.supervisorName());
    }

    @Test
    void createSupervisionPastDateThrows() {
        SupervisionRequestDTO dto = new SupervisionRequestDTO("Dr. Smith", "Notes", LocalDateTime.now().minusDays(1), null);
        assertThrows(ValidationException.class, () -> service.create(dto));
    }
}
