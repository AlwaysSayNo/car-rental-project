package com.epam.nazar.grinko.repositories;

import com.epam.nazar.grinko.domians.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

    Optional<Bill> findDistinctByOrderId(Long orderId);
    Bill getBillsById(Long id);

}
