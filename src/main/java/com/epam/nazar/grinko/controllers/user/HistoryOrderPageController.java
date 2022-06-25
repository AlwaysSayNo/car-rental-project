package com.epam.nazar.grinko.controllers.user;

import com.epam.nazar.grinko.domians.Bill;
import com.epam.nazar.grinko.domians.Order;
import com.epam.nazar.grinko.domians.helpers.OrderStatus;
import com.epam.nazar.grinko.exceptions.IllegalPathVariableException;
import com.epam.nazar.grinko.exceptions.JwtAuthenticationException;
import com.epam.nazar.grinko.securities.jwt.JwtTokenProvider;
import com.epam.nazar.grinko.services.*;
import com.epam.nazar.grinko.services.order.OrderService;
import com.epam.nazar.grinko.services.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("car-rental-service/registered-user/orders-history/{id}")
@AllArgsConstructor
public class HistoryOrderPageController {

    private final UserService userService;
    private final OrderService orderService;
    private final BillService billService;
    private final BreakdownService breakdownService;
    private final CancellationService cancellationService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping()
    public String showHistoryOrderPage(@PathVariable("id") Long orderId, Model model){
        Order order = orderService.getById(orderId);
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


    @ModelAttribute
    private void checkRequestValidity(@PathVariable("id") Long orderId, HttpServletRequest request){
        Order order = orderService.getById(orderId);
        String email = jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(request));
        Long userId = userService.getUserIdByEmail(email).orElseThrow(JwtAuthenticationException::new);

        List<OrderStatus> availableStatuses = Arrays.asList(OrderStatus.CANCELED, OrderStatus.ENDED_SUCCESSFULLY,
                OrderStatus.ENDED_WITH_BREAKDOWN);

        if(!order.getUser().getId().equals(userId) || !availableStatuses.contains(order.getStatus()))
            throw new IllegalPathVariableException();
    }

}
