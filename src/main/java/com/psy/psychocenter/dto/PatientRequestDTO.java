package com.psy.psychocenter.dto;

public record PatientRequestDTO(
    String name,
    String email,
    String phone,
    Integer packageCount
) {}
