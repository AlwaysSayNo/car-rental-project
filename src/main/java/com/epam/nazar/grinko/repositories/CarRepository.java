package com.epam.nazar.grinko.repositories;

import com.epam.nazar.grinko.domians.Car;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends PagingAndSortingRepository<Car, Long> {
}
