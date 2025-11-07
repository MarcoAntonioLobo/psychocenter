package com.psy.psychocenter.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.psy.psychocenter.dto.PaymentRequestDTO;
import com.psy.psychocenter.dto.PaymentResponseDTO;
import com.psy.psychocenter.exception.BusinessRuleException;
import com.psy.psychocenter.exception.ResourceNotFoundException;
import com.psy.psychocenter.exception.ValidationException;
import com.psy.psychocenter.mapper.PaymentMapper;
import com.psy.psychocenter.model.Patient;
import com.psy.psychocenter.model.Payment;
import com.psy.psychocenter.repository.PatientRepository;
import com.psy.psychocenter.repository.PaymentRepository;
import com.psy.psychocenter.service.PaymentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PatientRepository patientRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public PaymentResponseDTO create(PaymentRequestDTO dto) {
        if (dto.amount() == null || dto.amount().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Amount cannot be negative");
        }

        Patient patient = patientRepository.findById(dto.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        if (dto.packageType() == null) {
            throw new BusinessRuleException("Package type is required");
        }

        Payment payment = paymentMapper.toEntity(dto, patient);
        Payment saved = paymentRepository.save(payment);
        return paymentMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getAll() {
        return paymentRepository.findAll()
                .stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDTO getById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getByPatient(Long patientId) {

        // ✅ CORREÇÃO: validar existência do paciente (o teste exige isso)
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found");
        }

        return paymentRepository.findByPatientId(patientId)
                .stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    @Override
    public PaymentResponseDTO update(Long id, PaymentRequestDTO dto) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        Patient patient = patientRepository.findById(dto.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        if (dto.amount() == null || dto.amount().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Amount cannot be negative");
        }

        payment.setPatient(patient);
        payment.setPackageType(dto.packageType());
        payment.setAmount(dto.amount());
        payment.setPaymentDate(dto.paymentDate());
        payment.setStatus(dto.status());

        Payment updated = paymentRepository.save(payment);
        return paymentMapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Payment not found");
        }
        paymentRepository.deleteById(id);
    }
}
