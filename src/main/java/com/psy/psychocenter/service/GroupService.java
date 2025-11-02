package com.psy.psychocenter.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.psy.psychocenter.dto.GroupRequestDTO;
import com.psy.psychocenter.dto.GroupResponseDTO;
import com.psy.psychocenter.mapper.GroupMapper;
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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupService {

	private final GroupRepository groupRepository;
	private final GroupMapper groupMapper;
	private final PatientRepository patientRepository;
	private final SupervisionRepository supervisionRepository;
	private final PaymentRepository paymentRepository;
	private final AppointmentRepository appointmentRepository;

	public GroupResponseDTO create(GroupRequestDTO dto) {
		List<Patient> patients = dto.patientIds() != null ? patientRepository.findAllById(dto.patientIds()) : List.of();

		List<Supervision> supervisions = dto.supervisionIds() != null
				? supervisionRepository.findAllById(dto.supervisionIds())
				: List.of();

		// Checa duplicados
		if (patients.size() != patients.stream().distinct().count()) {
			throw new RuntimeException("Duplicate patients are not allowed in a group");
		}

		Payment payment = dto.paymentId() != null ? paymentRepository.findById(dto.paymentId())
				.orElseThrow(() -> new RuntimeException("Payment not found")) : null;

		Appointment appointment = dto.appointmentId() != null ? appointmentRepository.findById(dto.appointmentId())
				.orElseThrow(() -> new RuntimeException("Appointment not found")) : null;

		Group group = groupMapper.toEntity(dto, patients, supervisions, payment, appointment);
		Group saved = groupRepository.save(group);
		return groupMapper.toResponse(saved);
	}

	@Transactional(readOnly = true)
	public List<GroupResponseDTO> findAll() {
		return groupRepository.findAll().stream().map(groupMapper::toResponse).toList();
	}

	@Transactional(readOnly = true)
	public GroupResponseDTO findById(Long id) {
		Group group = groupRepository.findById(id).orElseThrow(() -> new RuntimeException("Group not found"));
		return groupMapper.toResponse(group);
	}

	public GroupResponseDTO update(Long id, GroupRequestDTO dto) {
		Group existing = groupRepository.findById(id).orElseThrow(() -> new RuntimeException("Group not found"));

		List<Patient> patients = dto.patientIds() != null ? patientRepository.findAllById(dto.patientIds()) : List.of();

		List<Supervision> supervisions = dto.supervisionIds() != null
				? supervisionRepository.findAllById(dto.supervisionIds())
				: List.of();

		Payment payment = dto.paymentId() != null ? paymentRepository.findById(dto.paymentId())
				.orElseThrow(() -> new RuntimeException("Payment not found")) : null;

		Appointment appointment = dto.appointmentId() != null ? appointmentRepository.findById(dto.appointmentId())
				.orElseThrow(() -> new RuntimeException("Appointment not found")) : null;

		existing.setName(dto.name());
		existing.setType(dto.type());
		existing.setPatients(patients);
		existing.setSupervisions(supervisions);
		existing.setPayment(payment);
		existing.setAppointment(appointment);

		Group updated = groupRepository.save(existing);
		return groupMapper.toResponse(updated);
	}

	public void delete(Long id) {
		if (!groupRepository.existsById(id)) {
			throw new RuntimeException("Group not found");
		}
		groupRepository.deleteById(id);
	}
}
