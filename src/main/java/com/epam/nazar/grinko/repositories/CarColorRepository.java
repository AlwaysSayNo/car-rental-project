package com.epam.nazar.grinko.repositories;

import com.epam.nazar.grinko.domians.CarColor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarColorRepository extends JpaRepository<CarColor, Long> {

    boolean existsCarColorByValue(String value);

    CarColor getCarColorByValue(String value);

}
