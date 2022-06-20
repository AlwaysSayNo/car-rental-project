package com.epam.nazar.grinko.controllers.user;

import com.epam.nazar.grinko.constants.ViewExceptionsConstants;
import com.epam.nazar.grinko.domians.Bill;
import com.epam.nazar.grinko.domians.Car;
import com.epam.nazar.grinko.domians.Order;
import com.epam.nazar.grinko.domians.helpers.BillStatus;
import com.epam.nazar.grinko.domians.helpers.CarStatus;
import com.epam.nazar.grinko.domians.helpers.OrderStatus;
import com.epam.nazar.grinko.dto.*;
import com.epam.nazar.grinko.exceptions.IllegalPathVariableException;
import com.epam.nazar.grinko.exceptions.IllegalJwtContentException;
import com.epam.nazar.grinko.securities.jwt.JwtTokenProvider;
import com.epam.nazar.grinko.services.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("car-rental-service/user")
@AllArgsConstructor
public class RegisteredUserController {


    private final CarService carService;
    private final UserService userService;
    private final OrderService orderService;
    private final BillService billService;
    private final BreakdownService breakdownService;
    private final JwtTokenProvider jwtTokenProvider;
    private final CancellationService cancellationService;

    @GetMapping("/cars")
    public String showAllCarsPage(Model model){
        List<Car> allCars = carService.getByStatusIn(CarStatus.NOT_RENTED);
        List<CarDto> allCarDto = allCars.stream().map(carService::mapToDto).collect(Collectors.toList());
        List<Long> allId = allCars.stream().map(Car::getId).collect(Collectors.toList());

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

    @GetMapping("/active-orders/{id}")
    public String showActiveOrderPage(Model model, @PathVariable("id") Long orderId){
        Order order = orderService.getById(orderId).orElseThrow(IllegalPathVariableException::new);
        Bill bill = order.getBill();

        model.addAttribute("order", orderService.mapToDto(order));
        model.addAttribute("bill", billService.mapToDto(bill));

        if(order.getStatus().equals(OrderStatus.REPAIR_PAYMENT)){
            model.addAttribute("breakdown", breakdownService.mapToDto(order.getBreakdown()));
        }

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

    @GetMapping("/orders-history/{id}")
    public String showHistoryOrderPage(Model model, @PathVariable("id") Long orderId){
        Order order = orderService.getById(orderId).orElseThrow(IllegalPathVariableException::new);
        Bill bill = order.getBill();

        model.addAttribute("order", orderService.mapToDto(order));
        model.addAttribute("bill", billService.mapToDto(bill));

        if(order.getStatus().equals(OrderStatus.ENDED_WITH_BREAKDOWN)){
            model.addAttribute("breakdown", breakdownService.mapToDto(order.getBreakdown()));
        }
        else if(order.getStatus().equals(OrderStatus.CANCELED)){
            model.addAttribute("cancellation", cancellationService.mapToDto(order.getCancellation()));
        }

        return "user/show-active-orders";
    }

}
