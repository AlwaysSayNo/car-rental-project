package com.epam.nazar.grinko.repositories;

import com.epam.nazar.grinko.domians.Order;
import com.epam.nazar.grinko.domians.helpers.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    String UPDATE_ORDER_STATUS_BY_ID = "UPDATE Order o SET o.status=:status WHERE o.id=:id";
    String UPDATE_ORDER_STATUS_BY_ID_IN = "UPDATE Order o SET o.status=:status WHERE o.id IN (:ids)";
    String SELECT_ALL_ORDERS_BY_STATUS_AND_EXPIRE_DATE_LESS_THAN = "SELECT o FROM Order o INNER JOIN o.bill b WHERE o.status=:status AND b.expirationDate<=:date";
    String SELECT_ALL_ORDERS_BY_STATUS_AND_START_DATE_LESS_THAN = "SELECT o FROM Order o INNER JOIN o.bill b WHERE o.status=:status AND b.startDate<=:date";

    List<Order> findAllByUser_IdAndStatusIsIn(Long userId, Collection<OrderStatus> statuses);
    Optional<Order> getByUserIdAndCarIdAndStatusIn(Long userId, Long carId, Collection<OrderStatus> statuses);

    @Transactional
    @Modifying
    @Query(UPDATE_ORDER_STATUS_BY_ID)
    void updateOrderStatusById(@Param("status") OrderStatus status,
                               @Param("id") Long id);
    @Transactional
    @Modifying
    @Query(UPDATE_ORDER_STATUS_BY_ID_IN)
    Integer updateOrderStatusByIdIn(@Param("status") OrderStatus status,
                               @Param("ids") List<Long> ids);

    @Transactional
    @Modifying
    @Query(value = SELECT_ALL_ORDERS_BY_STATUS_AND_EXPIRE_DATE_LESS_THAN)
    List<Order> selectOrdersByStatusAndExpirationDateLess(@Param("status") OrderStatus status,
                                                          @Param("date") Calendar date);
    @Transactional
    @Modifying
    @Query(value = SELECT_ALL_ORDERS_BY_STATUS_AND_START_DATE_LESS_THAN)
    List<Order> selectOrdersByStatusAndStartDateLess(@Param("status") OrderStatus status,
                                                     @Param("date") Calendar date);

}
