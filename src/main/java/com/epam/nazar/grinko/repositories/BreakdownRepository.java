package com.epam.nazar.grinko.repositories;

import com.epam.nazar.grinko.domians.Breakdown;
import com.epam.nazar.grinko.domians.PaymentDetails;
import com.epam.nazar.grinko.domians.helpers.BreakdownStatus;
import com.epam.nazar.grinko.domians.helpers.CarStatus;
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
    String UPDATE_BREAKDOWN_STATUS_AND_PAYMENT_DETAILS_ID_BY_ID = "UPDATE Breakdown b SET b.status=:status, b.paymentDetails=:paymentDetails WHERE b.id=:id";

    Optional<Breakdown> findDistinctByOrderId(Long orderId);

    @Transactional
    @Modifying
    @Query(UPDATE_BREAKDOWN_STATUS_BY_ID)
    void updateBreakdownStatusById(@Param("status") BreakdownStatus status,
                                   @Param("id") long id);

    @Transactional
    @Modifying
    @Query(UPDATE_BREAKDOWN_STATUS_AND_PAYMENT_DETAILS_ID_BY_ID)
    void updateBreakdownStatusAndPaymentDetailsById(@Param("status") BreakdownStatus status,
                                   @Param("paymentDetails") PaymentDetails paymentDetails,
                                   @Param("id") long id);


}
