package com.epam.nazar.grinko.services.order;

import com.epam.nazar.grinko.domians.Car;
import com.epam.nazar.grinko.domians.Order;
import com.epam.nazar.grinko.domians.User;
import com.epam.nazar.grinko.domians.helpers.CarStatus;
import com.epam.nazar.grinko.domians.helpers.OrderStatus;
import com.epam.nazar.grinko.dto.OrderDto;
import com.epam.nazar.grinko.repositories.OrderRepository;
import com.epam.nazar.grinko.services.car.CarService;
import com.epam.nazar.grinko.services.user.UserService;
import com.epam.nazar.grinko.utils.Utility;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderQueryManipulationService manipulationService;
    private final UserService userService;
    private final CarService carService;

    public void save(Order order){
        orderRepository.save(order);
    }

    public Order getById(Long id){
        return orderRepository.getById(id);
    }

    public Page<Order> getAllByUserIdAndStatus(PageRequest request, Long userId, List<OrderStatus> statuses,
                                               String filterBy, String filterValue){
        Map<String, List<String>> byIdAndStatuses = new HashMap<>();

        Utility.safetyAdd(byIdAndStatuses, "user_id", userId.toString());
        statuses.stream().map(Objects::toString).forEach(
                statusName -> Utility.safetyAdd(byIdAndStatuses, "status", statusName)
        );
        Utility.safetyAdd(byIdAndStatuses, filterBy, filterValue);

        return manipulationService.evaluateQuery(request, byIdAndStatuses);
    }

    public List<Order> getAllByUserIdAndStatus(Long userId, List<OrderStatus> statuses){
        return orderRepository.findAllByUser_IdAndStatusIsIn(userId, statuses);
    }

    public Page<Order> getOrdersWithStatus(PageRequest request, OrderStatus status, String filterBy, String filterValue) {
        return getOrdersWithStatusIn(request, Collections.singletonList(status), filterBy, filterValue);
    }

    public Page<Order> getOrdersWithStatusIn(PageRequest request, List<OrderStatus> statuses,
                                             String filterBy, String filterValue) {
        Map<String, List<String>> byStatuses = new HashMap<>();

        statuses.stream().map(OrderStatus::name).forEach(
                statusName -> Utility.safetyAdd(byStatuses, "status", statusName));
        Utility.safetyAdd(byStatuses, filterBy, filterValue);

        return manipulationService.evaluateQuery(request, byStatuses);
    }

    public Optional<Order> getOrderWithStatus(Long userId, Long carId, OrderStatus status){
        return orderRepository
                .getByUserIdAndCarIdAndStatusIn(userId, carId, Collections.singletonList(status));
    }

    public Optional<Order> getOrderWithStatusIn(Long userId, Long carId, OrderStatus... statuses){
        return orderRepository
                .getByUserIdAndCarIdAndStatusIn(userId, carId, Arrays.asList(statuses));
    }

    public void updateOrderStatus(OrderStatus status, Long id){
        orderRepository.updateOrderStatusById(status, id);
    }

    public void updateOrderStatusByIdIn(OrderStatus status, List<Long> id){
        orderRepository.updateOrderStatusByIdIn(status, id);
    }

    public List<Order> endExpiredSuccessfully(Calendar date){
        List<Order> orders = orderRepository.selectOrdersByStatusAndExpirationDateLess(OrderStatus.IN_USE, date);
        carService.updateCarStatusByIdIn(CarStatus.NOT_RENTED,
                orders.stream().map(Order::getCar).map(Car::getId).collect(Collectors.toList()));
        orderRepository.updateOrderStatusByIdIn(OrderStatus.ENDED_SUCCESSFULLY,
                orders.stream().map(Order::getId).collect(Collectors.toList()));

        return orders;
    }

    public List<Order> selectOrdersByStatusAndStartDateLess(OrderStatus status, Calendar date){
        return orderRepository.selectOrdersByStatusAndStartDateLess(status, date);
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

    public OrderQueryManipulationService getManipulationService() {
        return manipulationService;
    }

}
