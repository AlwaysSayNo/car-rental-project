package com.epam.nazar.grinko.repositories;

import com.epam.nazar.grinko.domians.Cancellation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CancellationRepository  extends JpaRepository<Cancellation, Long> {
}
