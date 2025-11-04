package com.psy.psychocenter.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.psy.psychocenter.dto.PaymentRequestDTO;
import com.psy.psychocenter.dto.PaymentResponseDTO;
import com.psy.psychocenter.dto.SupervisionRequestDTO;
import com.psy.psychocenter.dto.SupervisionResponseDTO;
import com.psy.psychocenter.exception.ResourceNotFoundException;
import com.psy.psychocenter.exception.ValidationException;
import com.psy.psychocenter.mapper.SupervisionMapper;
import com.psy.psychocenter.model.Payment;
import com.psy.psychocenter.model.Supervision;
import com.psy.psychocenter.model.enums.SupervisionStatus;
import com.psy.psychocenter.repository.PaymentRepository;
import com.psy.psychocenter.repository.SupervisionRepository;
import com.psy.psychocenter.service.SupervisionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SupervisionServiceImpl implements SupervisionService {

    private final SupervisionRepository supervisionRepository;
    private final PaymentRepository paymentRepository;
    private final SupervisionMapper supervisionMapper;

    @Override
    public SupervisionResponseDTO create(SupervisionRequestDTO dto) {
        if (dto.supervisorName() == null || dto.supervisorName().isBlank()) {
            throw new ValidationException("Supervisor name is required");
        }
        if (dto.dateTime() == null) {
            throw new ValidationException("dateTime is required");
        }
        if (dto.dateTime().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Supervision date cannot be in the past");
        }

        Payment payment = null;
        if (dto.paymentId() != null) {
            payment = paymentRepository.findById(dto.paymentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        }

        Supervision supervision = supervisionMapper.toEntity(dto, payment);
        supervision.setStatus(SupervisionStatus.SCHEDULED);

        Supervision saved = supervisionRepository.save(supervision);
        return supervisionMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupervisionResponseDTO> findAll() {
        return supervisionRepository.findAll()
                .stream()
                .map(supervisionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SupervisionResponseDTO findById(Long id) {
        Supervision supervision = supervisionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supervision not found"));
        return supervisionMapper.toResponse(supervision);
    }

    @Override
    public SupervisionResponseDTO update(Long id, SupervisionRequestDTO dto) {
        Supervision existing = supervisionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supervision not found"));

        if (dto.supervisorName() == null || dto.supervisorName().isBlank()) {
            throw new ValidationException("Supervisor name is required");
        }
        if (dto.dateTime() == null) {
            throw new ValidationException("dateTime is required");
        }
        if (dto.dateTime().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Supervision date cannot be in the past");
        }

        existing.setSupervisorName(dto.supervisorName());
        existing.setNotes(dto.notes());
        existing.setDateTime(dto.dateTime());

        if (dto.paymentId() != null) {
            Payment payment = paymentRepository.findById(dto.paymentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
            existing.setPayment(payment);
        } else {
            existing.setPayment(null);
        }

        Supervision updated = supervisionRepository.save(existing);
        return supervisionMapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        if (!supervisionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Supervision not found");
        }
        supervisionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDTO getPayment(Long supervisionId) {
        Supervision supervision = supervisionRepository.findById(supervisionId)
                .orElseThrow(() -> new ResourceNotFoundException("Supervision not found"));

        Payment payment = supervision.getPayment();
        if (payment == null) return null;

        return new PaymentResponseDTO(
                payment.getId(),
                payment.getPatient() != null ? payment.getPatient().getId() : null,
                payment.getPatient() != null ? payment.getPatient().getName() : null,
                payment.getPackageType(),
                payment.getAmount(),
                payment.getPaymentDate(),
                payment.getStatus()
        );
    }

    @Override
    public PaymentResponseDTO createPayment(Long supervisionId, PaymentRequestDTO dto) {
        Supervision supervision = supervisionRepository.findById(supervisionId)
                .orElseThrow(() -> new ResourceNotFoundException("Supervision not found"));

        Payment payment = new Payment();
        payment.setPatient(null);
        payment.setSupervision(supervision);
        payment.setPackageType(dto.packageType());
        payment.setAmount(dto.amount());
        payment.setPaymentDate(dto.paymentDate());
        payment.setStatus(dto.status());

        Payment saved = paymentRepository.save(payment);

        supervision.setPayment(saved);
        supervisionRepository.save(supervision);

        return new PaymentResponseDTO(
                saved.getId(),
                null,
                null,
                saved.getPackageType(),
                saved.getAmount(),
                saved.getPaymentDate(),
                saved.getStatus()
        );
    }

    @Override
    public PaymentResponseDTO updatePayment(Long supervisionId, PaymentRequestDTO dto) {
        Supervision supervision = supervisionRepository.findById(supervisionId)
                .orElseThrow(() -> new ResourceNotFoundException("Supervision not found"));

        Payment payment = supervision.getPayment();
        if (payment == null) throw new ResourceNotFoundException("Payment not found");

        payment.setPackageType(dto.packageType());
        payment.setAmount(dto.amount());
        payment.setPaymentDate(dto.paymentDate());
        payment.setStatus(dto.status());

        Payment updated = paymentRepository.save(payment);

        return new PaymentResponseDTO(
                updated.getId(),
                null,
                null,
                updated.getPackageType(),
                updated.getAmount(),
                updated.getPaymentDate(),
                updated.getStatus()
        );
    }
}
