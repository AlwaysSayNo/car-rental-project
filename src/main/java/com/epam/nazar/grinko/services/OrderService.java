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

    public List<Order> getAllByUserIdAndStatus(long id, OrderStatus... status){
        return orderRepository.findAllByUser_IdAndStatusIsIn(id, Arrays.asList(status));
    }


    public Optional<Order> getOrderWithStatus(Long userId, Long carId, OrderStatus status){
        return orderRepository.getByUserIdAndCarIdAndStatus(userId, carId, status);
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
                .setUser(userService.convertUserToUserDto(order.getUser()))
                .setStatus(order.getStatus());
    }

    public void updateOrderStatus(OrderStatus status, Long id){
        orderRepository.updateBreakdownStatusById(status, id);
    }

}
