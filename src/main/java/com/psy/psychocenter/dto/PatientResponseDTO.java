package com.psy.psychocenter.dto;

public record PatientResponseDTO(
    Long id,
    String name,
    String email,
    String phone,
    Integer packageCount,
    Integer sessionsUsed
) {}
