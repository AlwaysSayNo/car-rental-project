package com.epam.nazar.grinko.controllers.user;

import com.epam.nazar.grinko.domians.Car;
import com.epam.nazar.grinko.domians.Order;
import com.epam.nazar.grinko.domians.helpers.CarStatus;
import com.epam.nazar.grinko.domians.helpers.OrderStatus;
import com.epam.nazar.grinko.dto.*;
import com.epam.nazar.grinko.exceptions.IllegalJwtContentException;
import com.epam.nazar.grinko.securities.jwt.JwtTokenProvider;
import com.epam.nazar.grinko.services.car.CarService;
import com.epam.nazar.grinko.services.order.OrderService;
import com.epam.nazar.grinko.services.user.UserService;
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
@RequestMapping("car-rental-service/user")
@AllArgsConstructor
public class RegisteredUserController {

    private final CarService carService;
    private final UserService userService;
    private final OrderService orderService;
    private final JwtTokenProvider jwtTokenProvider;

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
