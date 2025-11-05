package com.psy.psychocenter.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Import(TestSecurityConfig.class)
class GroupIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long createPatient(String name) throws Exception {
        String patientJson = """
            {
              "name":"%s",
              "email":"%s@mail.com",
              "phone":"000000000",
              "packageCount":4
            }
            """.formatted(name, name.toLowerCase());

        String resp = mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(resp).get("id").asLong();
    }

    @Test
    void groupCrudAndLists() throws Exception {
        Long p1 = createPatient("G1");
        Long p2 = createPatient("G2");

        String groupReq = """
            {
              "name":"Grupo Teste",
              "type":"PATIENTS",
              "patientIds":[%d,%d],
              "supervisionIds":[],
              "paymentId":null,
              "appointmentId":null
            }
            """.formatted(p1, p2);

        String createResp = mockMvc.perform(post("/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(groupReq))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(createResp).get("id").asLong();

        mockMvc.perform(get("/groups/{id}/patients", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").exists());

        mockMvc.perform(get("/groups/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Grupo Teste"));

        String updateReq = """
            {
              "name":"Grupo Teste Atualizado",
              "type":"PATIENTS",
              "patientIds":[%d],
              "supervisionIds":[],
              "paymentId":null,
              "appointmentId":null
            }
            """.formatted(p1);

        mockMvc.perform(put("/groups/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateReq))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Grupo Teste Atualizado"));

        mockMvc.perform(delete("/groups/{id}", id))
                .andExpect(status().isNoContent());
    }
}
