package com.epam.nazar.grinko.controllers.user;

import com.epam.nazar.grinko.domians.Order;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/car-rental-service/user")
@AllArgsConstructor
public class RegisteredUserController {

    private final UserService userService;
    private final OrderService orderService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/orders-history")
    public String showHistoryPage(@RequestParam(value = "sortBy", required = false) String sortBy,
                                  @RequestParam(value = "direction", required = false) String direction,
                                  @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                  @RequestParam(value = "size", required = false, defaultValue = "8") Integer size,
                                  @RequestParam(value = "filterBy", required = false) String filterBy,
                                  @RequestParam(value = "filterValue", required = false) String filterValue,
                                  Model model, HttpServletRequest servletRequest){
        Long userId = userService.getUserIdByEmail(jwtTokenProvider.getUsername(
                jwtTokenProvider.resolveToken(servletRequest))
        ).orElseThrow(IllegalJwtContentException::new);

        List<OrderStatus> availableStatuses = Arrays.asList(
                OrderStatus.CANCELED, OrderStatus.ENDED_SUCCESSFULLY, OrderStatus.ENDED_WITH_BREAKDOWN);

        PageRequest request = orderService.getManipulationService().createRequest(page - 1, size, sortBy, direction);
        Page<Order> orders = orderService.getAllByUserIdAndStatus(request, userId, availableStatuses, filterBy, filterValue);

        Page<OrderDto> ordersDto = orders.map(orderService::mapToDto);
        List<Long> ids = orders.stream().map(Order::getId).collect(Collectors.toList());

        model.addAttribute("orders", ordersDto);
        model.addAttribute("ids", ids);

        return "/user/orders-history/all-orders-history";
    }

}
