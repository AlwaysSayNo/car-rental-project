package com.epam.nazar.grinko.repositories;

import com.epam.nazar.grinko.domians.User;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {
}
