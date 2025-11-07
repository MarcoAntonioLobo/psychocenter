package com.psy.psychocenter.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.psy.psychocenter.dto.AppointmentRequestDTO;
import com.psy.psychocenter.dto.AppointmentResponseDTO;
import com.psy.psychocenter.exception.BusinessRuleException;
import com.psy.psychocenter.exception.ResourceNotFoundException;
import com.psy.psychocenter.exception.ValidationException;
import com.psy.psychocenter.mapper.AppointmentMapper;
import com.psy.psychocenter.model.Appointment;
import com.psy.psychocenter.model.Patient;
import com.psy.psychocenter.model.Payment;
import com.psy.psychocenter.model.Supervision;
import com.psy.psychocenter.model.enums.AppointmentStatus;
import com.psy.psychocenter.model.enums.AppointmentType;
import com.psy.psychocenter.repository.AppointmentRepository;
import com.psy.psychocenter.repository.PatientRepository;
import com.psy.psychocenter.repository.SupervisionRepository;
import com.psy.psychocenter.service.impl.AppointmentServiceImpl;

class AppointmentServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private SupervisionRepository supervisionRepository;
    @Mock
    private AppointmentMapper appointmentMapper;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private Patient patient;
    private Supervision supervision;
    private Appointment appointment;
    private AppointmentRequestDTO appointmentRequestDTO;
    private AppointmentResponseDTO appointmentResponseDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Payment patientPayment = new Payment();
        patientPayment.setId(10L);

        patient = new Patient();
        patient.setId(1L);
        patient.setName("John Doe");
        patient.setPayments(List.of(patientPayment));

        Payment supervisionPayment = new Payment();
        supervisionPayment.setId(20L);

        supervision = new Supervision();
        supervision.setId(1L);
        supervision.setSupervisorName("Dr. Smith");
        supervision.setPayments(List.of(supervisionPayment));

        appointment = new Appointment();
        appointment.setId(1L);
        appointment.setPatient(patient);
        appointment.setSupervision(supervision);
        appointment.setDateTime(LocalDateTime.now().plusDays(1));
        appointment.setType(AppointmentType.INDIVIDUAL_PATIENT);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setNotes("Test notes");

        appointmentRequestDTO = new AppointmentRequestDTO(
                1L,
                1L,
                LocalDateTime.now().plusDays(1),
                AppointmentType.INDIVIDUAL_PATIENT,
                AppointmentStatus.SCHEDULED,
                "Test notes"
        );

        appointmentResponseDTO = new AppointmentResponseDTO(
                1L,
                1L,
                "John Doe",
                10L,
                1L,
                "Dr. Smith",
                20L,
                LocalDateTime.now().plusDays(1),
                AppointmentType.INDIVIDUAL_PATIENT,
                AppointmentStatus.SCHEDULED,
                "Test notes"
        );
    }

    // ====================== CREATE ======================
    @Test
    void create_success() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(supervisionRepository.findById(1L)).thenReturn(Optional.of(supervision));
        when(appointmentMapper.toEntity(appointmentRequestDTO, patient, supervision)).thenReturn(appointment);
        when(appointmentRepository.save(appointment)).thenReturn(appointment);
        when(appointmentMapper.toResponse(appointment)).thenReturn(appointmentResponseDTO);

        AppointmentResponseDTO response = appointmentService.create(appointmentRequestDTO);

        assertThat(response).isEqualTo(appointmentResponseDTO);
    }

    @Test
    void create_dateTimeInPast_throwsValidationException() {
        AppointmentRequestDTO dto = new AppointmentRequestDTO(
                1L, 1L, LocalDateTime.now().minusDays(1),
                AppointmentType.INDIVIDUAL_PATIENT,
                AppointmentStatus.SCHEDULED,
                "Notes"
        );
        assertThrows(ValidationException.class, () -> appointmentService.create(dto));
    }

    @Test
    void create_patientOrSupervisionNotFound_throwsResourceNotFoundException() {
        when(patientRepository.findById(2L)).thenReturn(Optional.empty());
        AppointmentRequestDTO dto = new AppointmentRequestDTO(
                2L, 1L, LocalDateTime.now().plusDays(1),
                AppointmentType.INDIVIDUAL_PATIENT,
                AppointmentStatus.SCHEDULED,
                "Notes"
        );
        assertThrows(ResourceNotFoundException.class, () -> appointmentService.create(dto));
    }

    @Test
    void create_patientOrSupervisionNoPayment_throwsBusinessRuleException() {
        Patient patientNoPayment = new Patient();
        patientNoPayment.setId(1L);
        patientNoPayment.setPayments(List.of());

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patientNoPayment));
        when(supervisionRepository.findById(1L)).thenReturn(Optional.of(supervision));

        assertThrows(BusinessRuleException.class, () -> appointmentService.create(appointmentRequestDTO));
    }

    // ====================== FIND ALL ======================
    @Test
    void findAll_returnsList() {
        when(appointmentRepository.findAll()).thenReturn(List.of(appointment));
        when(appointmentMapper.toResponse(appointment)).thenReturn(appointmentResponseDTO);

        List<AppointmentResponseDTO> list = appointmentService.findAll();

        assertThat(list).hasSize(1);
    }

    // ====================== FIND BY ID ======================
    @Test
    void findById_success() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentMapper.toResponse(appointment)).thenReturn(appointmentResponseDTO);

        AppointmentResponseDTO response = appointmentService.findById(1L);

        assertThat(response).isEqualTo(appointmentResponseDTO);
    }

    @Test
    void findById_notFound_throwsResourceNotFoundException() {
        when(appointmentRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> appointmentService.findById(2L));
    }

    // ====================== UPDATE ======================
    @Test
    void update_success() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(supervisionRepository.findById(1L)).thenReturn(Optional.of(supervision));
        when(appointmentMapper.toEntity(appointmentRequestDTO, patient, supervision)).thenReturn(appointment);
        when(appointmentRepository.save(appointment)).thenReturn(appointment);
        when(appointmentMapper.toResponse(appointment)).thenReturn(appointmentResponseDTO);

        AppointmentResponseDTO response = appointmentService.update(1L, appointmentRequestDTO);

        assertThat(response).isEqualTo(appointmentResponseDTO);
    }

    @Test
    void update_notFound_throwsResourceNotFoundException() {
        when(appointmentRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> appointmentService.update(2L, appointmentRequestDTO));
    }

    @Test
    void update_dateTimeInPast_throwsValidationException() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        AppointmentRequestDTO dto = new AppointmentRequestDTO(
                1L, 1L, LocalDateTime.now().minusDays(1),
                AppointmentType.INDIVIDUAL_PATIENT,
                AppointmentStatus.SCHEDULED,
                "Notes"
        );
        assertThrows(ValidationException.class, () -> appointmentService.update(1L, dto));
    }

    @Test
    void update_patientOrSupervisionNoPayment_throwsBusinessRuleException() {
        AppointmentRequestDTO dto = appointmentRequestDTO;
        Patient patientNoPayment = new Patient();
        patientNoPayment.setId(1L);
        patientNoPayment.setPayments(List.of());

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patientNoPayment));
        when(supervisionRepository.findById(1L)).thenReturn(Optional.of(supervision));

        assertThrows(BusinessRuleException.class, () -> appointmentService.update(1L, dto));
    }

    // ====================== DELETE ======================
    @Test
    void delete_success() {
        when(appointmentRepository.existsById(1L)).thenReturn(true);
        appointmentService.delete(1L);
        verify(appointmentRepository).deleteById(1L);
    }

    @Test
    void delete_notFound_throwsResourceNotFoundException() {
        when(appointmentRepository.existsById(2L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> appointmentService.delete(2L));
    }
}
