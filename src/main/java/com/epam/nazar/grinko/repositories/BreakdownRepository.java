package com.epam.nazar.grinko.repositories;

import com.epam.nazar.grinko.domians.Breakdown;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BreakdownRepository extends JpaRepository<Breakdown, Long> {

    Optional<Breakdown> findDistinctByOrderId(Long orderId);

}
