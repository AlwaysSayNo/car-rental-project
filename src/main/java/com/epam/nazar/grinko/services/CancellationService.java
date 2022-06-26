package com.epam.nazar.grinko.services;

import com.epam.nazar.grinko.constants.ToLocaleConstants;
import com.epam.nazar.grinko.domians.Cancellation;
import com.epam.nazar.grinko.domians.Car;
import com.epam.nazar.grinko.domians.Order;
import com.epam.nazar.grinko.domians.helpers.CarStatus;
import com.epam.nazar.grinko.domians.helpers.OrderStatus;
import com.epam.nazar.grinko.dto.CancellationDto;
import com.epam.nazar.grinko.repositories.CancellationRepository;
import com.epam.nazar.grinko.services.car.CarService;
import com.epam.nazar.grinko.services.order.OrderService;
import com.epam.nazar.grinko.services.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CancellationService {

    private final CancellationRepository cancellationRepository;
    private final OrderService orderService;
    private final CarService carService;
    private final TranslationService translationService;

    public void save(Cancellation cancellation){
        cancellationRepository.save(cancellation);
    }

    public List<Order> cancelUnverifiedOrders(Calendar date){
        List<Order> orders = orderService.selectOrdersByStatusAndStartDateLess(OrderStatus.UNDER_CONSIDERATION, date);
        List<Car> affectedCars = orders.stream().map(Order::getCar).collect(Collectors.toList());

        carService.updateCarStatusByIdIn(CarStatus.NOT_RENTED,
                affectedCars.stream().map(Car::getId).collect(Collectors.toList()));

        orderService.updateOrderStatusByIdIn(OrderStatus.CANCELED,
                orders.stream().map(Order::getId).collect(Collectors.toList()));

        orders.forEach(
                order -> {
                    Cancellation cancellation = new Cancellation()
                            .setOrder(order)
                            .setMessage(getDefaultCancellationMessage());

                    save(cancellation);
                }
        );

        return orders;
    }

    public CancellationDto mapToDto(Cancellation cancellation){
        return new CancellationDto()
                .setMessage(cancellation.getMessage())
                .setOrder(orderService.mapToDto(cancellation.getOrder()));
    }

    public Cancellation mapToObject(CancellationDto cancellationDto){
        return new Cancellation()
                .setMessage(cancellationDto.getMessage())
                .setOrder(orderService.mapToObject(cancellationDto.getOrder()));
    }

    public String getDefaultCancellationMessage(){
        return translationService.toLocale(ToLocaleConstants.DEFAULT_CANCELLATION_MESSAGE);
    }

}
