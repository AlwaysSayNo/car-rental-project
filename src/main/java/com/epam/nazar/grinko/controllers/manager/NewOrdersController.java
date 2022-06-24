package com.epam.nazar.grinko.controllers.manager;

import com.epam.nazar.grinko.domians.*;
import com.epam.nazar.grinko.domians.helpers.CarStatus;
import com.epam.nazar.grinko.domians.helpers.OrderStatus;
import com.epam.nazar.grinko.dto.BillDto;
import com.epam.nazar.grinko.dto.CancellationDto;
import com.epam.nazar.grinko.dto.OrderDto;
import com.epam.nazar.grinko.exceptions.IllegalPathVariableException;
import com.epam.nazar.grinko.securities.jwt.JwtTokenProvider;
import com.epam.nazar.grinko.services.*;
import com.epam.nazar.grinko.services.car.CarService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("car-rental-service/manager/new-orders/{id}")
@AllArgsConstructor
public class NewOrdersController {

    private final UserService userService;
    private final CarService carService;
    private final OrderService orderService;
    private final BillService billService;
    private final ManagerDecisionService decisionService;
    private final CancellationService cancellationService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping()
    public String showNewOrderPage(@PathVariable("id") Long orderId, Model model){
        OrderDto orderDto = orderService.mapToDto(orderService.getById(orderId));
        BillDto billDto = billService.mapToDto(billService.getByOrderId(orderId)
                .orElseThrow(IllegalPathVariableException::new));

        model.addAttribute("order", orderDto);
        model.addAttribute("cancellation", new CancellationDto());
        model.addAttribute("bill", billDto);
        return "manager/new-orders/show-new-order";
    }

    @PostMapping()
    public String handleNewOrder(@PathVariable("id") Long orderId, @RequestParam(value = "status") String orderStatus,
                                 @ModelAttribute("order") OrderDto order, HttpServletRequest request, Model model){
        OrderStatus status = OrderStatus.valueOf(orderStatus);
        Car car = carService.getByNumber(order.getCar().getNumber()).orElseThrow(IllegalPathVariableException::new);
        switch (status) {
            case IN_USE: {
                carService.updateCarStatusById(car.getId(), CarStatus.RENTED);
                break;
            }
            case CANCELED: {
                carService.updateCarStatusById(car.getId(), CarStatus.NOT_RENTED);

                CancellationDto cancellationDto = (CancellationDto) model.getAttribute("cancellation");
                if (cancellationDto == null) throw new NullPointerException();

                Cancellation cancellation = cancellationService.mapToObject(cancellationDto);
                cancellationService.save(cancellation);
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }

        orderService.updateOrderStatus(status, orderId);

        String email = jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(request));
        User manager = userService.getByEmail(email).orElseThrow(IllegalArgumentException::new);

        ManagerDecision decision = new ManagerDecision()
                .setManager(manager)
                .setOrder(orderService.getById(orderId));

        decisionService.save(decision);

        return "redirect:/car-rental-service/manager/new-orders";
    }

    @ModelAttribute
    public void checkRequestValidity(@PathVariable("id") Long orderId) {
        Order order = orderService.getById(orderId);

        if(!order.getStatus().equals(OrderStatus.UNDER_CONSIDERATION))
            throw new IllegalPathVariableException("Order with id " + orderId + " is not new");
    }

}
