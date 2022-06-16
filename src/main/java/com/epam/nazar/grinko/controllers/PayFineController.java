package com.epam.nazar.grinko.controllers;

import com.epam.nazar.grinko.domians.Breakdown;
import com.epam.nazar.grinko.domians.Order;
import com.epam.nazar.grinko.domians.helpers.OrderStatus;
import com.epam.nazar.grinko.dto.BreakdownDto;
import com.epam.nazar.grinko.exceptions.IllegalPathVariableException;
import com.epam.nazar.grinko.securities.jwt.IllegalJwtContentException;
import com.epam.nazar.grinko.securities.jwt.JwtTokenProvider;
import com.epam.nazar.grinko.services.BreakdownService;
import com.epam.nazar.grinko.services.OrderService;
import com.epam.nazar.grinko.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Controller
@RequestMapping("/car-rental-service/user/active-orders/{id}/pay-fine")
@AllArgsConstructor
public class PayFineController {

    private final UserService userService;
    private final BreakdownService breakdownService;
    private final OrderService orderService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping()
    public String showPayFinePage(HttpServletRequest request,Model model,
                                  @PathVariable("id") Long carId){
        Order order = getOrderByFields(request, carId, OrderStatus.REPAIR_PAYMENT)
                .orElseThrow(IllegalPathVariableException::new);
        BreakdownDto breakdownDto = breakdownService.convertBreakdownToBreakdownDto(
                breakdownService.getByOrderId(order.getId()).orElseThrow(IllegalPathVariableException::new)
        );

        model.addAttribute("breakdown", breakdownDto);

        return "user/payment-form";
    }

    @ModelAttribute
    public void checkRequestValidity(HttpServletRequest request, @PathVariable("id") Long carId) {
        if(!getOrderByFields(request, carId, OrderStatus.REPAIR_PAYMENT).isPresent())
            throw new IllegalPathVariableException();
    }

    private Optional<Order> getOrderByFields(HttpServletRequest request, Long carId, OrderStatus status){
        String email = jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(request));
        Long userId = userService.getUserIdByEmail(email).orElseThrow(IllegalJwtContentException::new);

        return orderService.getOrderWithStatus(userId, carId, status);
    }

}
