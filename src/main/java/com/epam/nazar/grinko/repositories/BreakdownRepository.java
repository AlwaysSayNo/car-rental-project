package com.epam.nazar.grinko.repositories;

import com.epam.nazar.grinko.domians.Breakdown;
import com.epam.nazar.grinko.domians.helpers.BreakdownStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface BreakdownRepository extends JpaRepository<Breakdown, Long> {

    String UPDATE_BREAKDOWN_STATUS_BY_ID = "UPDATE Breakdown b SET b.status=:status WHERE b.id=:id";

    Optional<Breakdown> findDistinctByOrderId(Long orderId);

    @Transactional
    @Modifying
    @Query(UPDATE_BREAKDOWN_STATUS_BY_ID)
    void updateBreakdownStatusById(@Param("id") Long id,
                                   @Param("status") BreakdownStatus status);

}
