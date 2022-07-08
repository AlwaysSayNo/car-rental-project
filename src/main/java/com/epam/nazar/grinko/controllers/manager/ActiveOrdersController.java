package com.epam.nazar.grinko.controllers.manager;

import com.epam.nazar.grinko.domians.*;
import com.epam.nazar.grinko.domians.helpers.BreakdownStatus;
import com.epam.nazar.grinko.domians.helpers.CarStatus;
import com.epam.nazar.grinko.domians.helpers.OrderStatus;
import com.epam.nazar.grinko.domians.helpers.UserStatus;
import com.epam.nazar.grinko.dto.BillDto;
import com.epam.nazar.grinko.dto.BreakdownDto;
import com.epam.nazar.grinko.dto.OrderDto;
import com.epam.nazar.grinko.exceptions.IllegalPathVariableException;
import com.epam.nazar.grinko.securities.jwt.JwtTokenProvider;
import com.epam.nazar.grinko.services.*;
import com.epam.nazar.grinko.services.car.CarService;
import com.epam.nazar.grinko.services.order.OrderService;
import com.epam.nazar.grinko.services.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("car-rental-service/manager/active-orders/{id}")
@AllArgsConstructor
public class ActiveOrdersController {

    private final UserService userService;
    private final CarService carService;
    private final OrderService orderService;
    private final BillService billService;
    private final BreakdownService breakdownService;
    private final JwtTokenProvider jwtTokenProvider;

    //TODO сделать фильтрацию для заблокированного менеджера
    @GetMapping()
    public String showActiveOrderPage(@PathVariable("id") Long orderId, Model model){
        OrderDto orderDto = orderService.mapToDto(orderService.getById(orderId));
        BillDto billDto = billService.mapToDto(billService.getByOrderId(orderId)
                .orElseThrow(IllegalPathVariableException::new));

        model.addAttribute("order", orderDto);
        model.addAttribute("breakdown", new BreakdownDto());
        model.addAttribute("bill", billDto);

        return "manager/active-orders/show-active-order";
    }

    @PostMapping()
    public String recordBreakdown(@PathVariable("id") Long orderId, @ModelAttribute("order") OrderDto orderDto,
                                  @ModelAttribute("breakdown") BreakdownDto breakdownDto, HttpServletRequest request){
        Order order = orderService.getById(orderId);
        User user = userService.getById(order.getUser().getId());
        Car car = carService.getById(order.getCar().getId());

        orderService.updateOrderStatus(OrderStatus.REPAIR_PAYMENT, orderId);
        userService.updateUserStatusById(UserStatus.ON_HOLD, user.getId());
        carService.updateCarStatusById(car.getId(), CarStatus.ON_HOLD);

        breakdownDto.setStatus(BreakdownStatus.NOT_PAID)
                .setOrder(orderDto);

        breakdownService.save(breakdownService.mapToObject(breakdownDto));

        String email = jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(request));
        User manager = userService.getByEmail(email).orElseThrow(IllegalArgumentException::new);

        return "redirect:/car-rental-service/manager/active-orders";
    }

}
