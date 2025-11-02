package com.psy.psychocenter.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.psy.psychocenter.model.Payment;
import com.psy.psychocenter.model.enums.PaymentStatus;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    boolean existsByPatientIdAndStatus(Long patientId, PaymentStatus status);

    List<Payment> findByPatientId(Long patientId);

}
