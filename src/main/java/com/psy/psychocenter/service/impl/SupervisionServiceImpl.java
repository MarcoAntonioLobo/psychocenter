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
import com.psy.psychocenter.model.Patient;
import com.psy.psychocenter.model.Payment;
import com.psy.psychocenter.model.Supervision;
import com.psy.psychocenter.model.enums.SupervisionStatus;
import com.psy.psychocenter.repository.PatientRepository;
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
    private final PatientRepository patientRepository;
    private final SupervisionMapper supervisionMapper;

    // -------------------- SUPERVISION --------------------

    @Override
    public SupervisionResponseDTO create(SupervisionRequestDTO dto) {
        validateSupervisionDTO(dto);

        Supervision supervision = supervisionMapper.toEntity(dto, null);
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
        validateSupervisionDTO(dto);

        Supervision existing = supervisionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supervision not found"));

        existing.setSupervisorName(dto.supervisorName());
        existing.setNotes(dto.notes());
        existing.setDateTime(dto.dateTime());

        Supervision updated = supervisionRepository.save(existing);
        return supervisionMapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        Supervision supervision = supervisionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supervision not found"));

        if (supervision.getPayments() != null && !supervision.getPayments().isEmpty()) {
            supervision.getPayments().forEach(paymentRepository::delete);
            supervision.getPayments().clear();
        }

        supervisionRepository.delete(supervision);
    }

    // -------------------- PAYMENT --------------------

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDTO getPayment(Long supervisionId) {
        Supervision supervision = supervisionRepository.findById(supervisionId)
                .orElseThrow(() -> new ResourceNotFoundException("Supervision not found"));

        if (supervision.getPayments() == null || supervision.getPayments().isEmpty()) return null;

        Payment lastPayment = supervision.getPayments().get(supervision.getPayments().size() - 1);
        return mapPaymentToResponse(lastPayment);
    }

    @Override
    public PaymentResponseDTO createPayment(Long supervisionId, PaymentRequestDTO dto) {
        Supervision supervision = supervisionRepository.findById(supervisionId)
                .orElseThrow(() -> new ResourceNotFoundException("Supervision not found"));

        Payment payment = new Payment();
        payment.setSupervision(supervision);

        if (dto.patientId() != null) {
            Patient patient = patientRepository.findById(dto.patientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

            payment.setPatient(patient);
            patient.getPayments().add(payment);
        }

        payment.setPackageType(dto.packageType());
        payment.setAmount(dto.amount());
        payment.setPaymentDate(dto.paymentDate());
        payment.setStatus(dto.status());

        Payment saved = paymentRepository.save(payment);

        supervision.getPayments().add(saved);
        supervisionRepository.save(supervision);

        return mapPaymentToResponse(saved);
    }

    @Override
    public PaymentResponseDTO updatePayment(Long supervisionId, PaymentRequestDTO dto) {
        Supervision supervision = supervisionRepository.findById(supervisionId)
                .orElseThrow(() -> new ResourceNotFoundException("Supervision not found"));

        if (supervision.getPayments() == null || supervision.getPayments().isEmpty()) {
            throw new ResourceNotFoundException("No payments found for this supervision");
        }

        Payment payment = supervision.getPayments().get(supervision.getPayments().size() - 1);

        if (dto.patientId() != null) {
            Patient patient = patientRepository.findById(dto.patientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
            payment.setPatient(patient);
        }

        payment.setPackageType(dto.packageType());
        payment.setAmount(dto.amount());
        payment.setPaymentDate(dto.paymentDate());
        payment.setStatus(dto.status());

        Payment updated = paymentRepository.save(payment);
        return mapPaymentToResponse(updated);
    }

    // -------------------- HELPERS --------------------

    private void validateSupervisionDTO(SupervisionRequestDTO dto) {
        if (dto.supervisorName() == null || dto.supervisorName().isBlank()) {
            throw new ValidationException("Supervisor name is required");
        }
        if (dto.dateTime() == null) {
            throw new ValidationException("dateTime is required");
        }
        if (dto.dateTime().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Supervision date cannot be in the past");
        }
    }

    private PaymentResponseDTO mapPaymentToResponse(Payment payment) {
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
}
