package com.epam.nazar.grinko.controllers.user;

import com.epam.nazar.grinko.domians.Order;
import com.epam.nazar.grinko.domians.helpers.OrderStatus;
import com.epam.nazar.grinko.dto.BreakdownDto;
import com.epam.nazar.grinko.exceptions.IllegalPathVariableException;
import com.epam.nazar.grinko.exceptions.JwtAuthenticationException;
import com.epam.nazar.grinko.securities.jwt.JwtTokenProvider;
import com.epam.nazar.grinko.services.BreakdownService;
import com.epam.nazar.grinko.services.order.OrderService;
import com.epam.nazar.grinko.services.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/car-rental-service/user/active-orders/{id}/pay-fine")
@AllArgsConstructor
@Slf4j
public class OrderPayFineController {

    private final UserService userService;
    private final BreakdownService breakdownService;
    private final OrderService orderService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping()
    public String showPayFinePage(@PathVariable("id") Long orderId, Model model){
        BreakdownDto breakdownDto = breakdownService.mapToDto(
                breakdownService.getByOrderId(orderId).orElseThrow(IllegalPathVariableException::new)
        );
        model.addAttribute("breakdown", breakdownDto);
        model.addAttribute("id", orderId);

        return "user/active-orders/pay-fine-form";
    }

    @PostMapping()
    public String evaluatePayFine(@PathVariable("id") Long orderId){
        Order order = orderService.getById(orderId);
        breakdownService.payFine(order);

        log.info("USER-PAY-FINE SUCCESS: userId={}, carId={}, orderId={}, breakdown={}",
                order.getUser().getId(), order.getCar().getId(), orderId, order.getBreakdown().getId());

        return "redirect:/car-rental-service/user/cars";
    }

    @ModelAttribute
    public void checkRequestValidity(@PathVariable("id") Long orderId, HttpServletRequest request) {
        Order order = orderService.getById(orderId);
        String email = jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(request));
        Long userId = userService.getUserIdByEmail(email).orElseThrow(JwtAuthenticationException::new);

        if(!order.getUser().getId().equals(userId) || !OrderStatus.REPAIR_PAYMENT.equals(order.getStatus()))
            throw new IllegalPathVariableException();
    }

}
