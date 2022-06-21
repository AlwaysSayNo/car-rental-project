package com.epam.nazar.grinko.controllers.manager;

import com.epam.nazar.grinko.domians.ManagerDecision;
import com.epam.nazar.grinko.domians.Order;
import com.epam.nazar.grinko.domians.helpers.OrderStatus;
import com.epam.nazar.grinko.dto.ManagerDecisionDto;
import com.epam.nazar.grinko.dto.OrderDto;
import com.epam.nazar.grinko.exceptions.JwtAuthenticationException;
import com.epam.nazar.grinko.securities.jwt.JwtTokenProvider;
import com.epam.nazar.grinko.services.ManagerDecisionService;
import com.epam.nazar.grinko.services.OrderService;
import com.epam.nazar.grinko.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("car-rental-service/manager")
@AllArgsConstructor
public class ManagerController {

    private final UserService userService;
    private final OrderService orderService;
    private final ManagerDecisionService decisionService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/new-orders")
    public void showNewOrders(Model model){
        List<Order> orders = orderService.getOrdersWithStatus(OrderStatus.UNDER_CONSIDERATION);

        List<OrderDto> ordersDto = orders.stream().map(orderService::mapToDto).collect(Collectors.toList());
        List<Long> ids = orders.stream().map(Order::getId).collect(Collectors.toList());

        model.addAttribute("orders", ordersDto);
        model.addAttribute("ids", ids);
    }

    @GetMapping("/active-orders")
    public void showActiveOrders(Model model){
        OrderStatus[] availableStatuses = {OrderStatus.IN_USE, OrderStatus.REPAIR_PAYMENT};
        List<Order> orders = orderService.getOrdersWithStatusIn(availableStatuses);

        List<OrderDto> ordersDto = orders.stream().map(orderService::mapToDto).collect(Collectors.toList());
        List<Long> ids = orders.stream().map(Order::getId).collect(Collectors.toList());

        model.addAttribute("orders", ordersDto);
        model.addAttribute("ids", ids);
    }

    @GetMapping("/orders-history")
    public void showOrdersHistory(HttpServletRequest request, Model model){
        String email = jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(request));
        Long managerId = userService.getUserIdByEmail(email).orElseThrow(JwtAuthenticationException::new);
        List<ManagerDecision> decisions = decisionService.getAllByManagerId(managerId);

        List<ManagerDecisionDto> decisionsDto = decisions.stream()
                .map(decisionService::mapToDto).collect(Collectors.toList());
        List<Long> ids = decisions.stream().map(ManagerDecision::getId).collect(Collectors.toList());

        model.addAttribute("decisions", decisionsDto);
        model.addAttribute("ids", ids);
    }


}
