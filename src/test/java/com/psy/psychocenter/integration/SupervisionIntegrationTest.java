package com.psy.psychocenter.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.psy.psychocenter.config.TestSecurityConfig;
import com.psy.psychocenter.dto.PatientRequestDTO;
import com.psy.psychocenter.dto.PaymentRequestDTO;
import com.psy.psychocenter.dto.SupervisionRequestDTO;
import com.psy.psychocenter.model.enums.PackageType;
import com.psy.psychocenter.model.enums.PaymentStatus;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Import(TestSecurityConfig.class)
class SupervisionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long createPatient() throws Exception {
        PatientRequestDTO p = new PatientRequestDTO("Sup Test", "sup@mail.com", "222222222", 4);
        String r = mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(p)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(r).get("id").asLong();
    }

    @Test
    void supervisionWithPaymentFlow() throws Exception {
        Long patientId = createPatient();

        // 1) Criar supervisão **sem** pagamento inicialmente
        SupervisionRequestDTO supReq = new SupervisionRequestDTO(
                "Dr. Supervisor",
                "Notas da supervisão",
                LocalDateTime.now().plusDays(3),
                null // sem paymentId aqui — cria supervision primeiro
        );

        String supCreateResp = mockMvc.perform(post("/supervisions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();

        Long supervisionId = objectMapper.readTree(supCreateResp).get("id").asLong();

        // 2) Criar pagamento **vinculado** à supervisão usando endpoint específico
        PaymentRequestDTO paymentReq = new PaymentRequestDTO(
                patientId,
                PackageType.SUPERVISION_4,
                BigDecimal.valueOf(150.00),
                LocalDate.now(),
                PaymentStatus.PAID
        );

        // POST /supervisions/{id}/payments -> deve retornar o Payment criado
        String payResp = mockMvc.perform(post("/supervisions/{id}/payments", supervisionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();

        Long paymentId = objectMapper.readTree(payResp).get("id").asLong();

        // 3) Verificar que a supervisão contém o pagamento no array payments
        mockMvc.perform(get("/supervisions/{id}", supervisionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.supervisorName").value("Dr. Supervisor"))
                .andExpect(jsonPath("$.payments[0].id").value(paymentId));

        // 4) Endpoint get payment da supervisão (retorna o PaymentResponseDTO)
        mockMvc.perform(get("/supervisions/{id}/payments", supervisionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(paymentId));

        // 5) Atualizar pagamento via PUT /supervisions/{id}/payments
        PaymentRequestDTO updatePayment = new PaymentRequestDTO(
                patientId,
                PackageType.SUPERVISION_4,
                BigDecimal.valueOf(175.00),
                LocalDate.now(),
                PaymentStatus.PAID
        );

        mockMvc.perform(put("/supervisions/{id}/payments", supervisionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(175.00));

        // 6) Atualizar supervisão (mantendo o pagamento vinculado)
        SupervisionRequestDTO updateSup = new SupervisionRequestDTO(
                "Dr. Nova",
                "Atualizado",
                LocalDateTime.now().plusDays(4),
                paymentId // opcionalmente pode enviar o id novamente
        );

        mockMvc.perform(put("/supervisions/{id}", supervisionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateSup)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.supervisorName").value("Dr. Nova"))
                .andExpect(jsonPath("$.payments[0].id").value(paymentId));

        // 7) Deletar supervisão
        mockMvc.perform(delete("/supervisions/{id}", supervisionId))
                .andExpect(status().isNoContent());
    }
}
