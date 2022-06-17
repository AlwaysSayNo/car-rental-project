package com.epam.nazar.grinko.repositories;

import com.epam.nazar.grinko.domians.PaymentDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentDetailsRepository extends JpaRepository<PaymentDetails, Long> {

    boolean existsByPassportNumber(String passportNumber);

}
