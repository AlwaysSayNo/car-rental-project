package com.epam.nazar.grinko.controllers.manager;

import com.epam.nazar.grinko.domians.ManagerDecision;
import com.epam.nazar.grinko.domians.Order;
import com.epam.nazar.grinko.domians.helpers.OrderStatus;
import com.epam.nazar.grinko.dto.ManagerDecisionDto;
import com.epam.nazar.grinko.dto.OrderDto;
import com.epam.nazar.grinko.exceptions.JwtAuthenticationException;
import com.epam.nazar.grinko.securities.jwt.JwtTokenProvider;
import com.epam.nazar.grinko.services.ManagerDecisionService;
import com.epam.nazar.grinko.services.order.OrderService;
import com.epam.nazar.grinko.services.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
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
    public String showNewOrders(@RequestParam(value = "sortBy", required = false) String sortBy,
                                @RequestParam(value = "direction", required = false) String direction,
                                @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                @RequestParam(value = "size", required = false, defaultValue = "8") Integer size,
                                @RequestParam(value = "filterBy", required = false) String filterBy,
                                @RequestParam(value = "filterValue", required = false) String filterValue, Model model){
        PageRequest request = orderService.getManipulationService().createRequest(page - 1, size, sortBy, direction);
        Page<Order> orders = orderService.getOrdersWithStatus(request, OrderStatus.UNDER_CONSIDERATION, filterBy, filterValue);

        Page<OrderDto> ordersDto = orders.map(orderService::mapToDto);
        List<Long> ids = orders.stream().map(Order::getId).collect(Collectors.toList());

        model.addAttribute("orders", ordersDto);
        model.addAttribute("ids", ids);

        return "manager/new-orders/all-new-orders";
    }

    @GetMapping("/active-orders")
    public String showActiveOrders(@RequestParam(value = "sortBy", required = false) String sortBy,
                                   @RequestParam(value = "direction", required = false) String direction,
                                   @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                   @RequestParam(value = "size", required = false, defaultValue = "8") Integer size,
                                   @RequestParam(value = "filterBy", required = false) String filterBy,
                                   @RequestParam(value = "filterValue", required = false) String filterValue, Model model){
        List<OrderStatus> availableStatuses = Arrays.asList(OrderStatus.IN_USE, OrderStatus.REPAIR_PAYMENT);

        PageRequest request = orderService.getManipulationService().createRequest(page - 1, size, sortBy, direction);
        Page<Order> orders = orderService.getOrdersWithStatusIn(request, availableStatuses, filterBy, filterValue);

        Page<OrderDto> ordersDto = orders.map(orderService::mapToDto);
        List<Long> ids = orders.stream().map(Order::getId).collect(Collectors.toList());

        model.addAttribute("orders", ordersDto);
        model.addAttribute("ids", ids);

        return "manager/active-orders/all-active-orders";
    }

    @GetMapping("/orders-history")
    public String showOrdersHistory(HttpServletRequest request, Model model){
        String email = jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(request));
        Long managerId = userService.getUserIdByEmail(email).orElseThrow(JwtAuthenticationException::new);
        List<ManagerDecision> decisions = decisionService.getAllByManagerId(managerId);

        List<ManagerDecisionDto> decisionsDto = decisions.stream()
                .map(decisionService::mapToDto).collect(Collectors.toList());
        List<Long> ids = decisions.stream().map(ManagerDecision::getId).collect(Collectors.toList());

        model.addAttribute("decisions", decisionsDto);
        model.addAttribute("ids", ids);

        return "manager/decision-history/all-decision-history";
    }


}
