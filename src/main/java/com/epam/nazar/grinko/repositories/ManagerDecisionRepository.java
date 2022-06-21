package com.epam.nazar.grinko.repositories;

import com.epam.nazar.grinko.domians.ManagerDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManagerDecisionRepository extends JpaRepository<ManagerDecision, Long> {

    List<ManagerDecision> findDistinctByManagerId(Long managerId);
    List<ManagerDecision> findDistinctByOrderId(Long orderId);

}
