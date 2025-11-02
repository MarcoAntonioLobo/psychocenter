package com.psy.psychocenter.dto;

import java.time.LocalDateTime;

public record SupervisionRequestDTO(
    String supervisorName,
    String notes,
    LocalDateTime dateTime
) {}
