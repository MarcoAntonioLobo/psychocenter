package com.psy.psychocenter.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.psy.psychocenter.dto.GroupRequestDTO;
import com.psy.psychocenter.dto.GroupResponseDTO;
import com.psy.psychocenter.dto.PatientResponseDTO;
import com.psy.psychocenter.dto.SupervisionResponseDTO;
import com.psy.psychocenter.exception.BusinessRuleException;
import com.psy.psychocenter.exception.ResourceNotFoundException;
import com.psy.psychocenter.mapper.GroupMapper;
import com.psy.psychocenter.mapper.PatientMapper;
import com.psy.psychocenter.mapper.SupervisionMapper;
import com.psy.psychocenter.model.Appointment;
import com.psy.psychocenter.model.Group;
import com.psy.psychocenter.model.Patient;
import com.psy.psychocenter.model.Payment;
import com.psy.psychocenter.model.Supervision;
import com.psy.psychocenter.repository.AppointmentRepository;
import com.psy.psychocenter.repository.GroupRepository;
import com.psy.psychocenter.repository.PatientRepository;
import com.psy.psychocenter.repository.PaymentRepository;
import com.psy.psychocenter.repository.SupervisionRepository;
import com.psy.psychocenter.service.impl.GroupServiceImpl;

class GroupServiceImplTest {

    @Mock private GroupRepository groupRepository;
    @Mock private GroupMapper groupMapper;
    @Mock private PatientRepository patientRepository;
    @Mock private SupervisionRepository supervisionRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private AppointmentRepository appointmentRepository;
    @Mock private PatientMapper patientMapper;
    @Mock private SupervisionMapper supervisionMapper;

    @InjectMocks private GroupServiceImpl groupService;

    private Group group;
    private GroupRequestDTO groupRequestDTO;
    private GroupResponseDTO groupResponseDTO;
    private Patient patient;
    private Supervision supervision;
    private Payment payment;
    private Appointment appointment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        patient = new Patient();
        patient.setId(1L);

        supervision = new Supervision();
        supervision.setId(2L);

        payment = new Payment();
        payment.setId(1L);

        appointment = new Appointment();
        appointment.setId(1L);

        group = new Group();
        group.setId(1L);
        group.setName("Group 1");
        group.setPatients(new ArrayList<>(List.of(patient)));
        group.setSupervisions(new ArrayList<>(List.of(supervision)));

        groupRequestDTO = new GroupRequestDTO(
                "Group 1",
                null,
                List.of(1L),
                List.of(2L),
                1L,
                1L
        );

        groupResponseDTO = new GroupResponseDTO(
                1L,
                "Group 1",
                null,
                List.of(1L),
                List.of(2L),
                1L,
                1L
        );
    }

    // ====================== CREATE ======================
    @Test
    void create_success() {
        when(patientRepository.findAllById(groupRequestDTO.patientIds()))
                .thenReturn(List.of(patient));
        when(supervisionRepository.findAllById(groupRequestDTO.supervisionIds()))
                .thenReturn(List.of(supervision));
        when(paymentRepository.findById(groupRequestDTO.paymentId()))
                .thenReturn(Optional.of(payment));
        when(appointmentRepository.findById(groupRequestDTO.appointmentId()))
                .thenReturn(Optional.of(appointment));

        when(groupMapper.toEntity(groupRequestDTO,
                List.of(patient),
                List.of(supervision),
                payment,
                appointment)).thenReturn(group);

        when(groupRepository.save(group)).thenReturn(group);
        when(groupMapper.toResponse(group)).thenReturn(groupResponseDTO);

        GroupResponseDTO response = groupService.create(groupRequestDTO);

        assertThat(response).isEqualTo(groupResponseDTO);
    }

    @Test
    void create_duplicatePatients_throwsBusinessRuleException() {
        GroupRequestDTO dto = new GroupRequestDTO(
                "Group 1",
                null,
                List.of(1L, 1L),
                List.of(2L),
                1L,
                1L
        );

        assertThrows(BusinessRuleException.class, () -> groupService.create(dto));
    }

    @Test
    void create_nonexistentPaymentOrAppointment_throwsResourceNotFoundException() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());
        when(appointmentRepository.findById(99L)).thenReturn(Optional.empty());

        GroupRequestDTO dto = new GroupRequestDTO(
                "Group 1",
                null,
                List.of(1L),
                List.of(2L),
                99L,
                99L
        );

        assertThrows(ResourceNotFoundException.class, () -> groupService.create(dto));
    }

    // ====================== FIND ALL ======================
    @Test
    void findAll_returnsList() {
        when(groupRepository.findAll()).thenReturn(List.of(group));
        when(groupMapper.toResponse(group)).thenReturn(groupResponseDTO);

        List<GroupResponseDTO> list = groupService.findAll();

        assertThat(list).hasSize(1);
        assertThat(list.get(0)).isEqualTo(groupResponseDTO);
    }

    // ====================== FIND BY ID ======================
    @Test
    void findById_success() {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(groupMapper.toResponse(group)).thenReturn(groupResponseDTO);

        GroupResponseDTO response = groupService.findById(1L);
        assertThat(response).isEqualTo(groupResponseDTO);
    }

    @Test
    void findById_notFound_throwsResourceNotFoundException() {
        when(groupRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> groupService.findById(2L));
    }

    // ====================== UPDATE ======================
    @Test
    void update_success() {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(patientRepository.findAllById(groupRequestDTO.patientIds()))
                .thenReturn(List.of(patient));
        when(supervisionRepository.findAllById(groupRequestDTO.supervisionIds()))
                .thenReturn(List.of(supervision));
        when(paymentRepository.findById(groupRequestDTO.paymentId()))
                .thenReturn(Optional.of(payment));
        when(appointmentRepository.findById(groupRequestDTO.appointmentId()))
                .thenReturn(Optional.of(appointment));

        when(groupRepository.save(group)).thenReturn(group);
        when(groupMapper.toResponse(group)).thenReturn(groupResponseDTO);

        GroupResponseDTO response = groupService.update(1L, groupRequestDTO);

        assertThat(response).isEqualTo(groupResponseDTO);
    }

    @Test
    void update_notFound_throwsResourceNotFoundException() {
        when(groupRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> groupService.update(2L, groupRequestDTO));
    }

    @Test
    void update_duplicatePatients_throwsBusinessRuleException() {
        GroupRequestDTO dto = new GroupRequestDTO(
                "Group 1",
                null,
                List.of(1L, 1L),
                List.of(2L),
                1L,
                1L
        );

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));

        assertThrows(BusinessRuleException.class, () -> groupService.update(1L, dto));
    }

    @Test
    void update_nonexistentPaymentOrAppointment_throwsResourceNotFoundException() {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());
        when(appointmentRepository.findById(99L)).thenReturn(Optional.empty());

        GroupRequestDTO dto = new GroupRequestDTO(
                "Group 1",
                null,
                List.of(1L),
                List.of(2L),
                99L,
                99L
        );

        assertThrows(ResourceNotFoundException.class, () -> groupService.update(1L, dto));
    }

    // ====================== DELETE ======================
    @Test
    void delete_success() {
        when(groupRepository.existsById(1L)).thenReturn(true);
        groupService.delete(1L);
        verify(groupRepository).deleteById(1L);
    }

    @Test
    void delete_notFound_throwsResourceNotFoundException() {
        when(groupRepository.existsById(2L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> groupService.delete(2L));
    }

    // ====================== GET PATIENTS ======================
    @Test
    void getPatients_success() {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(patientMapper.toResponse(any()))
                .thenReturn(new PatientResponseDTO(1L, "John", null, null, null, null));

        List<PatientResponseDTO> patients = groupService.getPatients(1L);

        assertThat(patients).hasSize(1);
    }

    @Test
    void getPatients_notFound_throwsResourceNotFoundException() {
        when(groupRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> groupService.getPatients(2L));
    }

    // ====================== GET SUPERVISIONS ======================
    @Test
    void getSupervisions_success() {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(supervisionMapper.toResponse(any()))
                .thenReturn(new SupervisionResponseDTO(2L, "Supervisor", null, null, null, List.of()));

        List<SupervisionResponseDTO> list = groupService.getSupervisions(1L);

        assertThat(list).hasSize(1);
    }

    @Test
    void getSupervisions_notFound_throwsResourceNotFoundException() {
        when(groupRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> groupService.getSupervisions(2L));
    }
}
