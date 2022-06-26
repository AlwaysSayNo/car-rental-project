package com.epam.nazar.grinko.repositories;

import com.epam.nazar.grinko.domians.Car;
import com.epam.nazar.grinko.domians.CarBrand;
import com.epam.nazar.grinko.domians.CarColor;
import com.epam.nazar.grinko.domians.helpers.CarSegment;
import com.epam.nazar.grinko.domians.helpers.CarStatus;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    String UPDATE_CAR_BY_ID = "UPDATE Car c SET c.number=:number, c.brand=:brand, c.name=:name, c.color=:color, c.pricePerDay=:pricePerDay, c.segment=:segment, c.status=:status WHERE c.id=:id";
    String UPDATE_CAR_STATUS_BY_ID = "UPDATE Car c SET c.status=:status WHERE c.id=:id";
    String UPDATE_CAR_STATUS_BY_ID_IN = "UPDATE Car c SET c.status=:status WHERE c.id IN (:id)";

    boolean existsByNumber(String number);
    Optional<Car> getByNumber(String number);

    @Transactional
    @Modifying
    @Query(UPDATE_CAR_BY_ID)
    void updateCarById(@Param("number") String number,
                       @Param("brand") CarBrand brand,
                       @Param("name") String name,
                       @Param("color") CarColor color,
                       @Param("pricePerDay") long pricePerDay,
                       @Param("segment") CarSegment segment,
                       @Param("status") CarStatus status,
                       @Param("id") long id);

    @Transactional
    @Modifying
    @Query(UPDATE_CAR_STATUS_BY_ID)
    void updateCarStatusById(@Param("status") CarStatus status,
                             @Param("id") long id);

    @Transactional
    @Modifying
    @Query(UPDATE_CAR_STATUS_BY_ID_IN)
    Integer updateCarStatusByIdIn(@Param("status") CarStatus status,
                             @Param("id") List<Long> id);
}
