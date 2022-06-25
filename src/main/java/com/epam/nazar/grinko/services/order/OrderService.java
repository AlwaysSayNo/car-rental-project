package com.epam.nazar.grinko.services.order;

import com.epam.nazar.grinko.domians.Car;
import com.epam.nazar.grinko.domians.Order;
import com.epam.nazar.grinko.domians.User;
import com.epam.nazar.grinko.domians.helpers.OrderStatus;
import com.epam.nazar.grinko.dto.OrderDto;
import com.epam.nazar.grinko.repositories.OrderRepository;
import com.epam.nazar.grinko.services.car.CarService;
import com.epam.nazar.grinko.services.user.UserService;
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

    //?
    public void save(Order order){
        carService.updateCarStatusById(order.getCar().getId(), order.getCar().getStatus());
        orderRepository.save(order);
    }

    public Order getById(Long id){
        return orderRepository.getById(id);
    }

    public List<Order> getAllByUserIdAndStatus(Long userId, OrderStatus... status){
        return orderRepository.findAllByUser_IdAndStatusIsIn(userId, Arrays.asList(status));
    }

    public Page<Order> getOrdersWithStatus(PageRequest request, OrderStatus status, String filterBy, String filterValue) {
        return getOrdersWithStatusIn(request, Collections.singletonList(status), filterBy, filterValue);
    }

    public Page<Order> getOrdersWithStatusIn(PageRequest request, List<OrderStatus> statuses, String filterBy, String filterValue) {
        Map<String, List<String>> byStatuses = new HashMap<>();
        byStatuses.put("status", statuses.stream().map(OrderStatus::name).collect(Collectors.toList()));

        if(filterBy != null) {
            List<String> values;
            if (byStatuses.containsKey(filterBy)) values = new ArrayList<>(byStatuses.get(filterBy));
            else values = new ArrayList<>();

            values.add(filterValue);
            byStatuses.put(filterBy, values);
        }

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
        orderRepository.updateBreakdownStatusById(status, id);
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
