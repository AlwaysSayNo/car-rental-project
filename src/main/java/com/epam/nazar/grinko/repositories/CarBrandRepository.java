package com.epam.nazar.grinko.repositories;

import com.epam.nazar.grinko.domians.Car;
import com.epam.nazar.grinko.domians.CarBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarBrandRepository extends JpaRepository<CarBrand, Long> {

    boolean existsCarBrandByValue(String value);

    CarBrand getCarBrandByValue(String value);
}
