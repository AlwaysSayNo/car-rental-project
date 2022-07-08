package com.epam.nazar.grinko.controllers.manager;

import com.epam.nazar.grinko.domians.*;
import com.epam.nazar.grinko.domians.helpers.BillStatus;
import com.epam.nazar.grinko.domians.helpers.CarStatus;
import com.epam.nazar.grinko.domians.helpers.OrderStatus;
import com.epam.nazar.grinko.domians.helpers.UserStatus;
import com.epam.nazar.grinko.dto.BillDto;
import com.epam.nazar.grinko.dto.CancellationDto;
import com.epam.nazar.grinko.dto.OrderDto;
import com.epam.nazar.grinko.exceptions.IllegalPathVariableException;
import com.epam.nazar.grinko.securities.jwt.JwtTokenProvider;
import com.epam.nazar.grinko.services.*;
import com.epam.nazar.grinko.services.car.CarService;
import com.epam.nazar.grinko.services.order.OrderService;
import com.epam.nazar.grinko.services.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequestMapping("/car-rental-service/manager/new-orders/{id}")
@AllArgsConstructor
@Slf4j
public class NewOrdersController {

    private final UserService userService;
    private final CarService carService;
    private final OrderService orderService;
    private final BillService billService;
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
        model.addAttribute("id", orderId);

        return "manager/new-orders/new-order";
    }

    @PostMapping()
    public String handleNewOrder(@PathVariable("id") Long orderId, @RequestParam(value = "orderStatus") String orderStatus,
                                 @ModelAttribute("cancellation") CancellationDto cancellationDto,
                                 BindingResult bindingResult){
        OrderStatus status = OrderStatus.valueOf(orderStatus);
        Order order = orderService.getById(orderId);
        Car car = order.getCar();

        switch (status) {
            case IN_USE: {
                carService.updateCarStatusById(car.getId(), CarStatus.RENTED);

                log.info("MANAGER ACCEPTING-ORDER-SUCCESS.");
                break;
            }
            case CANCELED: {
                if(bindingResult.hasErrors()) {
                    String url = "car-rental-service/manager/new-orders/" + orderId;

                    log.info("MANAGER CANCELLATION-ORDER-FAILURE: invalid data.");
                    return "redirect:/" + url;
                }

                carService.updateCarStatusById(car.getId(), CarStatus.NOT_RENTED);
                billService.updateStatusById(order.getBill().getId(), BillStatus.CANCELED);


                Cancellation cancellation = new Cancellation().setMessage(cancellationDto.getMessage())
                        .setOrder(order);
                cancellationService.save(cancellation);
                break;
            }
            default: {
                log.info("MANAGER ORDER-HANDLE-FAILURE: status={}", orderStatus);
                throw new IllegalArgumentException();
            }
        }

        orderService.updateOrderStatus(status, orderId);
        userService.updateUserStatusById(UserStatus.ACTIVE, order.getUser().getId());

        return "redirect:/car-rental-service/manager/new-orders";
    }

    @ModelAttribute
    public void checkRequestValidity(@PathVariable("id") Long orderId, HttpServletRequest request) {
        String email = jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(request));
        User manager = userService.getByEmail(email).orElseThrow(IllegalArgumentException::new);

        if(!UserStatus.ACTIVE.equals(manager.getStatus()))
            throw new IllegalStateException("Manager with status " + manager.getStatus().name() + "can`t handle orders.");

        Order order = orderService.getById(orderId);

        log.info("!OrderStatus.UNDER_CONSIDERATION.equals(order.getStatus()) == {}",
                !OrderStatus.UNDER_CONSIDERATION.equals(order.getStatus()));


        log.info("order.getStatus() == {}",
                order.getStatus());
        if(!OrderStatus.UNDER_CONSIDERATION.equals(order.getStatus()))
            throw new IllegalPathVariableException("Order with id " + orderId + " is not new");
    }

}
