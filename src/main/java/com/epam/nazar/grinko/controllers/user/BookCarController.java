package com.epam.nazar.grinko.controllers.user;

import com.epam.nazar.grinko.domians.Bill;
import com.epam.nazar.grinko.domians.Car;
import com.epam.nazar.grinko.domians.Order;
import com.epam.nazar.grinko.domians.User;
import com.epam.nazar.grinko.domians.helpers.BillStatus;
import com.epam.nazar.grinko.domians.helpers.CarStatus;
import com.epam.nazar.grinko.domians.helpers.OrderStatus;
import com.epam.nazar.grinko.domians.helpers.UserStatus;
import com.epam.nazar.grinko.dto.*;
import com.epam.nazar.grinko.exceptions.IllegalPathVariableException;
import com.epam.nazar.grinko.securities.jwt.JwtTokenProvider;
import com.epam.nazar.grinko.services.BillService;
import com.epam.nazar.grinko.services.car.CarService;
import com.epam.nazar.grinko.services.order.OrderService;
import com.epam.nazar.grinko.services.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("car-rental-service/user/cars/{id}")
@AllArgsConstructor
public class BookCarController {

    private final UserService userService;
    private final CarService carService;
    private final BillService billService;
    private final OrderService orderService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/book")
        public String showBookPage(@PathVariable("id") Long carId, Model model){
        CarDto carDto = carService.mapToDto(carService.getById(carId));
        BillDto billDto = new BillDto()
                .setDriverPrice(billService.getDriverPrice(carDto))
                .setCarPrice(carDto.getPricePerDay());

        model.addAttribute("carDto", carDto);
        model.addAttribute("billDto", billDto);

        return "user/book-form";
    }

    @PostMapping("/book")
    public String calculateBill(@PathVariable("id") Long carId, @ModelAttribute("billDto") BillDto billDto,
                                RedirectAttributes redirectAttributes){
        if(!billDto.isWithDriver()) billDto.setDriverPrice(0);
        billDto.setTotalPrice(billService.getTotalPrice(billDto));

        redirectAttributes.addFlashAttribute("billDto", billDto);
        String url = "/car-rental-service/user/cars/" + carId + "/book/payment";

        return "redirect:" + url;
    }

    @GetMapping("/book/payment")
    public String showPaymentRequisitesPage(@PathVariable("id") Long carId, Model model){
        BillDto billDto = (BillDto) model.getAttribute("billDto");

        model.addAttribute("billDto", billDto);
        model.addAttribute("paymentDetailsDto", new PaymentDetailsDto());

        return "user/payment-form";
    }

    @PostMapping("/book/payment")
    public String processPayment(@PathVariable("id") Long carId, @ModelAttribute("billDto") BillDto billDto,
                                 @ModelAttribute("paymentDetailsDto") PaymentDetailsDto paymentDetailsDto){
        CarDto carDto = carService.mapToDto(carService.getById(carId));
        carDto.setStatus(CarStatus.ON_PROCESSING.name());

        UserDto userDto = userService.mapToDto(userService.getById(carId));

        OrderDto orderDto = new OrderDto().setCar(carDto)
                .setUser(userDto)
                .setStatus(OrderStatus.UNDER_CONSIDERATION);

        billDto.setOrder(orderDto)
                .setStatus(BillStatus.PAID);

        Bill bill = billService.mapToObject(billDto);
        orderService.save(bill.getOrder());
        billService.addBill(bill);

        return "redirect:/car-rental-service/user/active-orders";
    }

    @ModelAttribute
    private void checkRequestValidity(@PathVariable("id") Long carId){
        Car car = carService.getById(carId);

        if(!car.getStatus().equals(CarStatus.NOT_RENTED))
            throw new IllegalPathVariableException();
    }

    @ModelAttribute
    private void checkUserStatus(HttpServletRequest request){
        User user = userService.getByEmail(
                jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(request))
        ).orElseThrow(IllegalPathVariableException::new);

        List<Order> orderList = orderService.getAllByUserIdAndStatus(user.getId(),
                OrderStatus.IN_USE, OrderStatus.REPAIR_PAYMENT, OrderStatus.UNDER_CONSIDERATION);

        if(!orderList.isEmpty())
            throw new IllegalStateException();

        if(!user.getStatus().equals(UserStatus.ACTIVE))
            throw new IllegalPathVariableException();
    }

}
