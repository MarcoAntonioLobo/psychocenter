package com.psy.psychocenter.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.psy.psychocenter.dto.PaymentRequestDTO;
import com.psy.psychocenter.dto.PaymentResponseDTO;
import com.psy.psychocenter.model.Patient;
import com.psy.psychocenter.model.Payment;
import com.psy.psychocenter.repository.PatientRepository;
import com.psy.psychocenter.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PatientRepository patientRepository;

    // ✅ Criar pagamento
    @Transactional
    public PaymentResponseDTO create(PaymentRequestDTO dto) {
        Patient patient = patientRepository.findById(dto.patientId())
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        Payment payment = Payment.builder()
                .patient(patient)
                .packageType(dto.packageType())
                .amount(dto.amount())
                .paymentDate(dto.paymentDate())
                .status(dto.status())
                .build();

        paymentRepository.save(payment);
        return toResponse(payment);
    }

    // ✅ Buscar todos os pagamentos
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getAll() {
        return paymentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ✅ Buscar pagamento por ID
    @Transactional(readOnly = true)
    public PaymentResponseDTO getById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));
        return toResponse(payment);
    }

    // ✅ Buscar pagamentos de um paciente
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getByPatient(Long patientId) {
        return paymentRepository.findByPatientId(patientId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ✅ Atualizar pagamento
    @Transactional
    public PaymentResponseDTO update(Long id, PaymentRequestDTO dto) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));

        Patient patient = patientRepository.findById(dto.patientId())
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        payment.setPatient(patient);
        payment.setPackageType(dto.packageType());
        payment.setAmount(dto.amount());
        payment.setPaymentDate(dto.paymentDate());
        payment.setStatus(dto.status());

        paymentRepository.save(payment);
        return toResponse(payment);
    }

    // ✅ Deletar pagamento
    @Transactional
    public void delete(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new RuntimeException("Pagamento não encontrado");
        }
        paymentRepository.deleteById(id);
    }

    // ✅ Conversão para DTO
    private PaymentResponseDTO toResponse(Payment payment) {
        return new PaymentResponseDTO(
                payment.getId(),
                payment.getPatient().getId(),
                payment.getPatient().getName(),
                payment.getPackageType(),
                payment.getAmount(),
                payment.getPaymentDate(),
                payment.getStatus()
        );
    }
}
