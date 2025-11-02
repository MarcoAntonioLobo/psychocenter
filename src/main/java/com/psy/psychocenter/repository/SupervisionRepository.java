package com.psy.psychocenter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.psy.psychocenter.model.Supervision;

@Repository
public interface SupervisionRepository extends JpaRepository<Supervision, Long> {
}
