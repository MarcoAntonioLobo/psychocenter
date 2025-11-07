package com.psy.psychocenter.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.psy.psychocenter.dto.PatientRequestDTO;
import com.psy.psychocenter.dto.PatientResponseDTO;
import com.psy.psychocenter.exception.ResourceNotFoundException;
import com.psy.psychocenter.mapper.PatientMapper;
import com.psy.psychocenter.model.Patient;
import com.psy.psychocenter.repository.PatientRepository;
import com.psy.psychocenter.service.impl.PatientServiceImpl;

class PatientServiceImplTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PatientMapper patientMapper;

    @InjectMocks
    private PatientServiceImpl patientService;

    private Patient patient;
    private PatientRequestDTO patientRequestDTO;
    private PatientResponseDTO patientResponseDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        patient = new Patient();
        patient.setId(1L);
        patient.setName("John Doe");
        patient.setEmail("john@example.com");
        patient.setPhone("123456789");
        patient.setPackageCount(5);

        patientRequestDTO = new PatientRequestDTO("John Doe", "john@example.com", "123456789", 5);

        patientResponseDTO = new PatientResponseDTO(
            1L, "John Doe", "john@example.com", "123456789", 5, 0
        );
    }

    // ====================== CREATE ======================
    @Test
    void create_success() {
        when(patientMapper.toEntity(patientRequestDTO)).thenReturn(patient);
        when(patientRepository.save(patient)).thenReturn(patient);
        when(patientMapper.toResponse(patient)).thenReturn(patientResponseDTO);

        PatientResponseDTO response = patientService.create(patientRequestDTO);
        assertThat(response).isEqualTo(patientResponseDTO);
    }

    // ====================== FIND ALL ======================
    @Test
    void findAll_returnsList() {
        when(patientRepository.findAll()).thenReturn(List.of(patient));
        when(patientMapper.toResponse(patient)).thenReturn(patientResponseDTO);

        List<PatientResponseDTO> list = patientService.findAll();
        assertThat(list).hasSize(1);
        assertThat(list.get(0)).isEqualTo(patientResponseDTO);
    }

    // ====================== FIND BY ID ======================
    @Test
    void findById_success() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientMapper.toResponse(patient)).thenReturn(patientResponseDTO);

        PatientResponseDTO response = patientService.findById(1L);
        assertThat(response).isEqualTo(patientResponseDTO);
    }

    @Test
    void findById_notFound_throwsResourceNotFoundException() {
        when(patientRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> patientService.findById(2L));
    }

    // ====================== UPDATE ======================
    @Test
    void update_success() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(patient)).thenReturn(patient);
        when(patientMapper.toResponse(patient)).thenReturn(patientResponseDTO);

        PatientResponseDTO response = patientService.update(1L, patientRequestDTO);
        assertThat(response).isEqualTo(patientResponseDTO);
    }

    @Test
    void update_notFound_throwsResourceNotFoundException() {
        when(patientRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> patientService.update(2L, patientRequestDTO));
    }

    // ====================== DELETE ======================
    @Test
    void delete_success() {
        when(patientRepository.existsById(1L)).thenReturn(true);
        patientService.delete(1L);
        verify(patientRepository).deleteById(1L);
    }

    @Test
    void delete_notFound_throwsResourceNotFoundException() {
        when(patientRepository.existsById(2L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> patientService.delete(2L));
    }
}
