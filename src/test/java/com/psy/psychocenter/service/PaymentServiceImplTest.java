package com.psy.psychocenter.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.psy.psychocenter.dto.PaymentRequestDTO;
import com.psy.psychocenter.dto.PaymentResponseDTO;
import com.psy.psychocenter.exception.BusinessRuleException;
import com.psy.psychocenter.exception.ResourceNotFoundException;
import com.psy.psychocenter.exception.ValidationException;
import com.psy.psychocenter.mapper.PaymentMapper;
import com.psy.psychocenter.model.Patient;
import com.psy.psychocenter.model.Payment;
import com.psy.psychocenter.model.enums.PackageType;
import com.psy.psychocenter.model.enums.PaymentStatus;
import com.psy.psychocenter.repository.PatientRepository;
import com.psy.psychocenter.repository.PaymentRepository;
import com.psy.psychocenter.service.impl.PaymentServiceImpl;

class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Payment payment;
    private PaymentRequestDTO paymentRequestDTO;
    private PaymentResponseDTO paymentResponseDTO;
    private Patient patient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        patient = new Patient();
        patient.setId(1L);
        patient.setName("John Doe");

        payment = new Payment();
        payment.setId(1L);
        payment.setPatient(patient);
        payment.setAmount(new BigDecimal("100.00"));
        payment.setPaymentDate(LocalDate.now());
        payment.setPackageType(PackageType.INDIVIDUAL_4);
        payment.setStatus(PaymentStatus.PAID);

        paymentRequestDTO = new PaymentRequestDTO(
            1L,
            PackageType.INDIVIDUAL_4,
            new BigDecimal("100.00"),
            LocalDate.now(),
            PaymentStatus.PAID
        );

        paymentResponseDTO = new PaymentResponseDTO(
            1L,
            1L,
            "John Doe",
            PackageType.INDIVIDUAL_4,
            new BigDecimal("100.00"),
            LocalDate.now(),
            PaymentStatus.PAID
        );
    }

    // ====================== CREATE ======================
    @Test
    void create_success() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        when(paymentMapper.toEntity(paymentRequestDTO, patient)).thenReturn(payment);
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(paymentMapper.toResponse(payment)).thenReturn(paymentResponseDTO);

        PaymentResponseDTO response = paymentService.create(paymentRequestDTO);

        assertThat(response).isEqualTo(paymentResponseDTO);
    }

    @Test
    void create_negativeAmount_throwsValidationException() {
        PaymentRequestDTO invalid = new PaymentRequestDTO(
            1L,
            PackageType.INDIVIDUAL_4,
            new BigDecimal("-1"),
            LocalDate.now(),
            PaymentStatus.PAID
        );

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        assertThrows(ValidationException.class, () -> paymentService.create(invalid));
    }

    @Test
    void create_patientNotFound_throwsResourceNotFoundException() {
        when(patientRepository.findById(2L)).thenReturn(Optional.empty());

        PaymentRequestDTO dto = new PaymentRequestDTO(
            2L, PackageType.INDIVIDUAL_4,
            new BigDecimal("100"),
            LocalDate.now(),
            PaymentStatus.PAID
        );

        assertThrows(ResourceNotFoundException.class, () -> paymentService.create(dto));
    }

    @Test
    void create_packageTypeNull_throwsBusinessRuleException() {
        PaymentRequestDTO dto = new PaymentRequestDTO(
            1L, null,
            new BigDecimal("100"),
            LocalDate.now(),
            PaymentStatus.PAID
        );

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        assertThrows(BusinessRuleException.class, () -> paymentService.create(dto));
    }

    // ====================== GET ALL ======================
    @Test
    void getAll_returnsList() {
        when(paymentRepository.findAll()).thenReturn(List.of(payment));
        when(paymentMapper.toResponse(payment)).thenReturn(paymentResponseDTO);

        List<PaymentResponseDTO> list = paymentService.getAll();
        assertThat(list).hasSize(1);
    }

    // ====================== GET BY ID ======================
    @Test
    void getById_success() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentMapper.toResponse(payment)).thenReturn(paymentResponseDTO);

        PaymentResponseDTO response = paymentService.getById(1L);
        assertThat(response).isEqualTo(paymentResponseDTO);
    }

    @Test
    void getById_notFound_throwsResourceNotFoundException() {
        when(paymentRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> paymentService.getById(2L));
    }

    // ====================== GET BY PATIENT ======================
    @Test
    void getByPatient_success() {
        when(patientRepository.existsById(1L)).thenReturn(true);
        when(paymentRepository.findByPatientId(1L)).thenReturn(List.of(payment));
        when(paymentMapper.toResponse(payment)).thenReturn(paymentResponseDTO);

        List<PaymentResponseDTO> list = paymentService.getByPatient(1L);

        assertThat(list).hasSize(1);
    }

    @Test
    void getByPatient_patientNotFound_throwsResourceNotFoundException() {
        when(patientRepository.existsById(2L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> paymentService.getByPatient(2L));
    }

    // ====================== UPDATE ======================
    @Test
    void update_success() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(paymentMapper.toEntity(paymentRequestDTO, patient)).thenReturn(payment);
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(paymentMapper.toResponse(payment)).thenReturn(paymentResponseDTO);

        PaymentResponseDTO response = paymentService.update(1L, paymentRequestDTO);
        assertThat(response).isEqualTo(paymentResponseDTO);
    }

    @Test
    void update_paymentNotFound_throwsResourceNotFoundException() {
        when(paymentRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> paymentService.update(2L, paymentRequestDTO));
    }

    @Test
    void update_patientNotFound_throwsResourceNotFoundException() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(patientRepository.findById(2L)).thenReturn(Optional.empty());

        PaymentRequestDTO dto = new PaymentRequestDTO(
            2L, PackageType.INDIVIDUAL_4,
            new BigDecimal("100"),
            LocalDate.now(),
            PaymentStatus.PAID
        );

        assertThrows(ResourceNotFoundException.class, () -> paymentService.update(1L, dto));
    }

    @Test
    void update_negativeAmount_throwsValidationException() {
        PaymentRequestDTO dto = new PaymentRequestDTO(
            1L, PackageType.INDIVIDUAL_4,
            new BigDecimal("-1"),
            LocalDate.now(),
            PaymentStatus.PAID
        );

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        assertThrows(ValidationException.class, () -> paymentService.update(1L, dto));
    }

    // ====================== DELETE ======================
    @Test
    void delete_success() {
        when(paymentRepository.existsById(1L)).thenReturn(true);
        paymentService.delete(1L);
        verify(paymentRepository).deleteById(1L);
    }

    @Test
    void delete_notFound_throwsResourceNotFoundException() {
        when(paymentRepository.existsById(2L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> paymentService.delete(2L));
    }
}
