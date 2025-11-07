package com.psy.psychocenter.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.psy.psychocenter.service.GroupService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupServiceImpl implements GroupService {

	private final GroupRepository groupRepository;
	private final GroupMapper groupMapper;
	private final PatientRepository patientRepository;
	private final SupervisionRepository supervisionRepository;
	private final PaymentRepository paymentRepository;
	private final AppointmentRepository appointmentRepository;
	private final PatientMapper patientMapper;
	private final SupervisionMapper supervisionMapper;

	@Override
	public GroupResponseDTO create(GroupRequestDTO dto) {

		if (dto.patientIds() != null) {
			Set<Long> unique = dto.patientIds().stream().collect(Collectors.toSet());
			if (unique.size() != dto.patientIds().size()) {
				throw new BusinessRuleException("Duplicate patients are not allowed in a group");
			}
		}

		List<Patient> patients = List.of();
		if (dto.patientIds() != null) {
			patients = patientRepository.findAllById(dto.patientIds());
			if (patients.size() != dto.patientIds().size()) {
				throw new ResourceNotFoundException("One or more patients not found");
			}
		}

		List<Supervision> supervisions = List.of();
		if (dto.supervisionIds() != null) {
			supervisions = supervisionRepository.findAllById(dto.supervisionIds());
			if (supervisions.size() != dto.supervisionIds().size()) {
				throw new ResourceNotFoundException("One or more supervisions not found");
			}
		}

		Payment payment = null;
		if (dto.paymentId() != null) {
			payment = paymentRepository.findById(dto.paymentId())
					.orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
		}

		Appointment appointment = null;
		if (dto.appointmentId() != null) {
			appointment = appointmentRepository.findById(dto.appointmentId())
					.orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
		}

		Group group = groupMapper.toEntity(dto, patients, supervisions, payment, appointment);
		Group saved = groupRepository.save(group);
		return groupMapper.toResponse(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public List<GroupResponseDTO> findAll() {
		return groupRepository.findAll().stream().map(groupMapper::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public GroupResponseDTO findById(Long id) {
		Group group = groupRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Group not found"));
		return groupMapper.toResponse(group);
	}

	@Override
	public GroupResponseDTO update(Long id, GroupRequestDTO dto) {

		Group existing = groupRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Group not found"));

		if (dto.patientIds() != null) {
			Set<Long> unique = dto.patientIds().stream().collect(Collectors.toSet());
			if (unique.size() != dto.patientIds().size()) {
				throw new BusinessRuleException("Duplicate patients are not allowed in a group");
			}
		}

		List<Patient> patients = List.of();
		if (dto.patientIds() != null) {
			patients = patientRepository.findAllById(dto.patientIds());
			if (patients.size() != dto.patientIds().size()) {
				throw new ResourceNotFoundException("One or more patients not found");
			}
		}

		List<Supervision> supervisions = List.of();
		if (dto.supervisionIds() != null) {
			supervisions = supervisionRepository.findAllById(dto.supervisionIds());
			if (supervisions.size() != dto.supervisionIds().size()) {
				throw new ResourceNotFoundException("One or more supervisions not found");
			}
		}

		Payment payment = null;
		if (dto.paymentId() != null) {
			payment = paymentRepository.findById(dto.paymentId())
					.orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
		}

		Appointment appointment = null;
		if (dto.appointmentId() != null) {
			appointment = appointmentRepository.findById(dto.appointmentId())
					.orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
		}

		existing.setName(dto.name());
		existing.setType(dto.type());
		existing.setPatients(patients);
		existing.setSupervisions(supervisions);
		existing.setPayment(payment);
		existing.setAppointment(appointment);

		Group updated = groupRepository.save(existing);
		return groupMapper.toResponse(updated);
	}

	@Override
	public void delete(Long id) {
		if (!groupRepository.existsById(id)) {
			throw new ResourceNotFoundException("Group not found");
		}
		groupRepository.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PatientResponseDTO> getPatients(Long groupId) {
		Group group = groupRepository.findById(groupId)
				.orElseThrow(() -> new ResourceNotFoundException("Group not found"));
		return group.getPatients().stream().map(patientMapper::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<SupervisionResponseDTO> getSupervisions(Long groupId) {
		Group group = groupRepository.findById(groupId)
				.orElseThrow(() -> new ResourceNotFoundException("Group not found"));
		return group.getSupervisions().stream().map(supervisionMapper::toResponse).toList();
	}
}
