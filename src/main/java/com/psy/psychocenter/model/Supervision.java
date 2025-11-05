package com.psy.psychocenter.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.psy.psychocenter.model.enums.SupervisionStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tb_supervisions")
public class Supervision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String supervisorName;
    private String notes;
    private LocalDateTime dateTime;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SupervisionStatus status = SupervisionStatus.SCHEDULED;

    @Builder.Default
    @OneToMany(mappedBy = "supervision", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

}
