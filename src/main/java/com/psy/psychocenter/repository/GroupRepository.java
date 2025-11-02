package com.psy.psychocenter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.psy.psychocenter.model.Group;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
}
