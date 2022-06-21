package com.epam.nazar.grinko.repositories;

import com.epam.nazar.grinko.domians.ManagerDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerDecisionRepository extends JpaRepository<ManagerDecision, Long> {
}