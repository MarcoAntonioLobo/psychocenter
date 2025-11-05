package com.psy.psychocenter.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;

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
import com.psy.psychocenter.model.enums.PackageType;
import com.psy.psychocenter.model.enums.PaymentStatus;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Import(TestSecurityConfig.class)
class PaymentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long createPatient() throws Exception {
        PatientRequestDTO p = new PatientRequestDTO("Paga Teste", "paga@mail.com", "111111111", 4);
        String r = mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(p)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(r).get("id").asLong();
    }

    @Test
    void paymentCrudFlow() throws Exception {
        Long patientId = createPatient();

        PaymentRequestDTO payReq = new PaymentRequestDTO(
                patientId,
                PackageType.INDIVIDUAL_4,
                BigDecimal.valueOf(200.00),
                LocalDate.now(),
                PaymentStatus.PAID
        );

        String createResp = mockMvc.perform(post("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.patientId").value(patientId))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(createResp).get("id").asLong();

        mockMvc.perform(get("/payments/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(200.0));

        mockMvc.perform(get("/payments/patient/{patientId}", patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        PaymentRequestDTO updateReq = new PaymentRequestDTO(
                patientId,
                PackageType.INDIVIDUAL_4,
                BigDecimal.valueOf(250.00),
                LocalDate.now(),
                PaymentStatus.PAID
        );

        mockMvc.perform(put("/payments/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(250.0));

        mockMvc.perform(delete("/payments/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/payments/{id}", id))
                .andExpect(status().isNotFound());
    }
}
