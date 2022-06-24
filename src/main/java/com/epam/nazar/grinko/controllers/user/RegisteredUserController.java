package com.epam.nazar.grinko.controllers.user;

import com.epam.nazar.grinko.domians.Bill;
import com.epam.nazar.grinko.domians.Car;
import com.epam.nazar.grinko.domians.Order;
import com.epam.nazar.grinko.domians.helpers.CarStatus;
import com.epam.nazar.grinko.domians.helpers.OrderStatus;
import com.epam.nazar.grinko.dto.*;
import com.epam.nazar.grinko.exceptions.IllegalPathVariableException;
import com.epam.nazar.grinko.exceptions.IllegalJwtContentException;
import com.epam.nazar.grinko.securities.jwt.JwtTokenProvider;
import com.epam.nazar.grinko.services.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("car-rental-service/registered-user")
@AllArgsConstructor
public class RegisteredUserController {

    private final CarService carService;
    private final UserService userService;
    private final OrderService orderService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/cars")
    public String showAllCarsPage(@RequestParam(value = "sortBy", required = false) String sortBy,
                                  @RequestParam(value = "direction", required = false) String direction,
                                  @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                  @RequestParam(value = "size", required = false, defaultValue = "8") Integer size,
                                  @RequestParam(value = "filterBy", required = false) String filterBy,
                                  @RequestParam(value = "filterValue", required = false) String filterValue, Model model){
        PageRequest pageRequest = carService.getManipulationService().createRequest(page - 1, size, sortBy, direction);
        Page<Car> cars = carService.getByStatus(pageRequest, CarStatus.NOT_RENTED, filterBy, filterValue);

        List<CarDto> allCarDto = cars.stream().map(carService::mapToDto).collect(Collectors.toList());
        List<Long> allId = cars.stream().map(Car::getId).collect(Collectors.toList());

        model.addAttribute("cars", allCarDto);
        model.addAttribute("ids", allId);

        return "user/show-cars";
    }

    @GetMapping("/active-orders")
    public String showActiveOrdersPage(Model model, HttpServletRequest request){
        Long id = userService.getUserIdByEmail(jwtTokenProvider.getUsername(
                jwtTokenProvider.resolveToken(request))
        ).orElseThrow(IllegalJwtContentException::new);

        List<Order> orders = orderService.getAllByUserIdAndStatus(
                id, OrderStatus.IN_USE, OrderStatus.REPAIR_PAYMENT
        );

        List<OrderDto> ordersDto = orders.stream().map(orderService::mapToDto).collect(Collectors.toList());
        List<Long> ids = orders.stream().map(Order::getId).collect(Collectors.toList());

        model.addAttribute("orders", ordersDto);
        model.addAttribute("ids", ids);

        return "user/show-active-orders";
    }

    @GetMapping("/orders-history")
    public String showHistoryPage(Model model, HttpServletRequest request){
        Long id = userService.getUserIdByEmail(jwtTokenProvider.getUsername(
                jwtTokenProvider.resolveToken(request))
        ).orElseThrow(IllegalJwtContentException::new);

        List<Order> orders = orderService.getAllByUserIdAndStatus(
                id, OrderStatus.CANCELED, OrderStatus.ENDED_SUCCESSFULLY, OrderStatus.ENDED_WITH_BREAKDOWN
        );

        List<OrderDto> ordersDto = orders.stream().map(orderService::mapToDto).collect(Collectors.toList());
        List<Long> ids = orders.stream().map(Order::getId).collect(Collectors.toList());

        model.addAttribute("orders", ordersDto);
        model.addAttribute("ids", ids);

        return "user/show-orders-history";
    }

}
