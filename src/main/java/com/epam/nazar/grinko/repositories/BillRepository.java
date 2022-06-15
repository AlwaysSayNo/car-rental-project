package com.epam.nazar.grinko.repositories;

import com.epam.nazar.grinko.domians.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
}
