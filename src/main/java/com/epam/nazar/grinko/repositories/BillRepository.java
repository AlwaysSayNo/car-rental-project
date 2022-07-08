package com.epam.nazar.grinko.repositories;

import com.epam.nazar.grinko.domians.Bill;
import com.epam.nazar.grinko.domians.helpers.BillStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

    String UPDATE_BILL_STATUS_BY_ID = "UPDATE Bill b SET b.status=:status WHERE b.id=:id";

    Optional<Bill> findDistinctByOrderId(Long orderId);
    Bill getBillsById(Long id);

    @Transactional
    @Modifying
    @Query(UPDATE_BILL_STATUS_BY_ID)
    void updateStatusById(@Param("id") Long id,
                          @Param("status") BillStatus status);

}
