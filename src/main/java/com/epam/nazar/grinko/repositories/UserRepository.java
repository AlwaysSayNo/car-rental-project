package com.epam.nazar.grinko.repositories;

import com.epam.nazar.grinko.domians.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
