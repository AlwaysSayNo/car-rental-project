package com.epam.nazar.grinko.controllers.user;

import com.epam.nazar.grinko.domians.Order;
import com.epam.nazar.grinko.domians.helpers.OrderStatus;
import com.epam.nazar.grinko.dto.BreakdownDto;
import com.epam.nazar.grinko.dto.PaymentDetailsDto;
import com.epam.nazar.grinko.exceptions.IllegalPathVariableException;
import com.epam.nazar.grinko.exceptions.IllegalJwtContentException;
import com.epam.nazar.grinko.securities.jwt.JwtTokenProvider;
import com.epam.nazar.grinko.services.BreakdownService;
import com.epam.nazar.grinko.services.order.OrderService;
import com.epam.nazar.grinko.services.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Controller
@RequestMapping("/car-rental-service/registered-user/active-orders/{id}/pay-fine")
@AllArgsConstructor
public class PayFineController {

    private final UserService userService;
    private final BreakdownService breakdownService;
    private final OrderService orderService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping()
    public String showPayFinePage(HttpServletRequest request, Model model,
                                  @PathVariable("id") Long carId){
        Order order = getRepairOrderByFields(request, carId).orElseThrow(IllegalPathVariableException::new);
        BreakdownDto breakdownDto = breakdownService.mapToDto(
                breakdownService.getByOrderId(order.getId()).orElseThrow(IllegalPathVariableException::new)
        );

        model.addAttribute("breakdown", breakdownDto);
        model.addAttribute("paymentDetails", new PaymentDetailsDto());

        return "user/pay-fine-form";
    }

    @PostMapping()
    public String evaluatePayFine(HttpServletRequest request, @ModelAttribute("paymentDetails") PaymentDetailsDto paymentDetailsDto,
                                  @PathVariable("id") Long carId){
        Order order = getRepairOrderByFields(request, carId).orElseThrow(IllegalPathVariableException::new);
        breakdownService.payFine(order, paymentDetailsDto);

        return "redirect:/car-rental-service/user/cars";
    }

    @ModelAttribute
    public void checkRequestValidity(HttpServletRequest request, @PathVariable("id") Long carId) {
        if(!getRepairOrderByFields(request, carId).isPresent())
            throw new IllegalPathVariableException();
    }

    private Optional<Order> getRepairOrderByFields(HttpServletRequest request, Long carId){
        String email = jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(request));
        Long userId = userService.getUserIdByEmail(email).orElseThrow(IllegalJwtContentException::new);

        return orderService.getOrderWithStatus(userId, carId, OrderStatus.REPAIR_PAYMENT);
    }

}
