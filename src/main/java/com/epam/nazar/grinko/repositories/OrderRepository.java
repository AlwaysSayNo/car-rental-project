package com.epam.nazar.grinko.repositories;

import com.epam.nazar.grinko.domians.Order;
import com.epam.nazar.grinko.domians.helpers.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    String UPDATE_ORDER_STATUS_BY_ID = "UPDATE Order o SET o.status=:status WHERE o.id=:id";

    List<Order> findAllByUser_IdAndStatusIsIn(Long userId, Collection<OrderStatus> statuses);

    Optional<Order> getByUserIdAndCarIdAndStatusIn(Long userId, Long carId, Collection<OrderStatus> statuses);

    List<Order> getAllByStatusIn(Collection<OrderStatus> statuses);

    @Transactional
    @Modifying
    @Query(UPDATE_ORDER_STATUS_BY_ID)
    void updateBreakdownStatusById(@Param("status") OrderStatus status,
                                   @Param("id") long id);
}
