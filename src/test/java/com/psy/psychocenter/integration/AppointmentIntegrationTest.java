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
import com.psy.psychocenter.dto.AppointmentRequestDTO;
import com.psy.psychocenter.dto.PaymentRequestDTO;
import com.psy.psychocenter.model.enums.AppointmentStatus;
import com.psy.psychocenter.model.enums.AppointmentType;
import com.psy.psychocenter.model.enums.PackageType;
import com.psy.psychocenter.model.enums.PaymentStatus;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Import(TestSecurityConfig.class)
class AppointmentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long createPatientAndReturnId() throws Exception {
        String patientJson = """
            {
              "name":"Teste Paciente",
              "email":"teste@mail.com",
              "phone":"123456789",
              "packageCount":4
            }
            """;

        String resp = mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(resp).get("id").asLong();
    }

    private Long createPaymentForPatient(Long patientId) throws Exception {
        PaymentRequestDTO paymentReq = new PaymentRequestDTO(
                patientId,
                PackageType.INDIVIDUAL_4,
                new BigDecimal("200.00"),
                LocalDate.now(),
                PaymentStatus.PAID
        );

        String resp = mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(resp).get("id").asLong();
    }

    @Test
    void fullAppointmentCrudFlow() throws Exception {
        Long patientId = createPatientAndReturnId();
        createPaymentForPatient(patientId);

        AppointmentRequestDTO createReq = new AppointmentRequestDTO(
                patientId,
                null,
                LocalDateTime.now().plusDays(1),
                AppointmentType.INDIVIDUAL_PATIENT,
                AppointmentStatus.SCHEDULED,
                "Primeira sessão"
        );

        String createResp = mockMvc.perform(post("/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.patientId").value(patientId))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(createResp).get("id").asLong();

        mockMvc.perform(get("/appointments/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));

        mockMvc.perform(get("/appointments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        AppointmentRequestDTO updateReq = new AppointmentRequestDTO(
                patientId,
                null,
                LocalDateTime.now().plusDays(2),
                AppointmentType.INDIVIDUAL_PATIENT,
                AppointmentStatus.SCHEDULED,
                "Atualizado"
        );

        mockMvc.perform(put("/appointments/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notes").value("Atualizado"));

        mockMvc.perform(delete("/appointments/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/appointments/{id}", id))
                .andExpect(status().isNotFound());
    }
}
