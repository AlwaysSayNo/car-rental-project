package com.epam.nazar.grinko.repositories;

import com.epam.nazar.grinko.domians.Order;
import com.epam.nazar.grinko.domians.helpers.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByUser_IdAndStatusIsIn(Long userId, Collection<OrderStatus> statuses);

    Optional<Order> getByUserIdAndCarIdAndStatus(Long userId, Long carId, OrderStatus status);

}
