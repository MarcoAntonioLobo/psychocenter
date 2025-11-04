package com.psy.psychocenter.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.psy.psychocenter.dto.PaymentRequestDTO;
import com.psy.psychocenter.dto.PaymentResponseDTO;
import com.psy.psychocenter.dto.SupervisionRequestDTO;
import com.psy.psychocenter.dto.SupervisionResponseDTO;
import com.psy.psychocenter.service.SupervisionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/supervisions")
@RequiredArgsConstructor
public class SupervisionController {

    private final SupervisionService supervisionService;

    @PostMapping
    public ResponseEntity<SupervisionResponseDTO> create(@RequestBody SupervisionRequestDTO dto) {
        SupervisionResponseDTO created = supervisionService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<SupervisionResponseDTO>> findAll() {
        return ResponseEntity.ok(supervisionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupervisionResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(supervisionService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupervisionResponseDTO> update(@PathVariable Long id,
                                                         @RequestBody SupervisionRequestDTO dto) {
        return ResponseEntity.ok(supervisionService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        supervisionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/payments")
    public ResponseEntity<PaymentResponseDTO> getPayment(@PathVariable Long id) {
        return ResponseEntity.ok(supervisionService.getPayment(id));
    }

    @PostMapping("/{id}/payments")
    public ResponseEntity<PaymentResponseDTO> createPayment(@PathVariable Long id,
                                                            @RequestBody PaymentRequestDTO dto) {
        PaymentResponseDTO created = supervisionService.createPayment(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}/payments")
    public ResponseEntity<PaymentResponseDTO> updatePayment(@PathVariable Long id,
                                                            @RequestBody PaymentRequestDTO dto) {
        return ResponseEntity.ok(supervisionService.updatePayment(id, dto));
    }
}
