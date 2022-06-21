package com.epam.nazar.grinko.services;

import com.epam.nazar.grinko.domians.Car;
import com.epam.nazar.grinko.domians.Order;
import com.epam.nazar.grinko.domians.User;
import com.epam.nazar.grinko.domians.helpers.OrderStatus;
import com.epam.nazar.grinko.dto.OrderDto;
import com.epam.nazar.grinko.repositories.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final CarService carService;

    public void save(Order order){
        carService.updateCarStatusById(order.getCar().getId(), order.getCar().getStatus());
        orderRepository.save(order);
    }

    public Optional<Order> getById(Long id){
        return orderRepository.findById(id);
    }

    public List<Order> getAllByUserIdAndStatus(long id, OrderStatus... status){
        return orderRepository.findAllByUser_IdAndStatusIsIn(id, Arrays.asList(status));
    }

    public List<Order> getOrdersWithStatus(OrderStatus status) {
        return orderRepository.getAllByStatusIn(Collections.singletonList(status));
    }

    public List<Order> getOrdersWithStatusIn(OrderStatus... statuses) {
        return orderRepository.getAllByStatusIn(Arrays.asList(statuses));
    }

    public Optional<Order> getOrderWithStatus(Long userId, Long carId, OrderStatus status){
        return orderRepository
                .getByUserIdAndCarIdAndStatusIn(userId, carId, Collections.singletonList(status));
    }

    public Optional<Order> getOrderWithStatusIn(Long userId, Long carId, OrderStatus... statuses){
        return orderRepository
                .getByUserIdAndCarIdAndStatusIn(userId, carId, Arrays.asList(statuses));
    }

    public Order mapToObject(OrderDto orderDto){
        Long carId = carService.getByNumber(orderDto.getCar().getNumber())
                .orElseThrow(NullPointerException::new).getId();
        Long userId = userService.getByEmail(orderDto.getUser().getEmail())
                .orElseThrow(NullPointerException::new).getId();

        Car car = carService.mapToObject(orderDto.getCar()).setId(carId);
        User user = userService.mapToObject(orderDto.getUser()).setId(userId);

        return new Order().setCar(car)
                .setUser(user)
                .setStatus(orderDto.getStatus());
    }

    public OrderDto mapToDto(Order order){
        return new OrderDto().setCar(carService.mapToDto(order.getCar()))
                .setUser(userService.mapToDto(order.getUser()))
                .setStatus(order.getStatus());
    }

    public void updateOrderStatus(OrderStatus status, Long id){
        orderRepository.updateBreakdownStatusById(status, id);
    }

}
