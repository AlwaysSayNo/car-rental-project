package com.epam.nazar.grinko.controllers;

import com.epam.nazar.grinko.constants.ViewExceptionsConstants;
import com.epam.nazar.grinko.domians.Car;
import com.epam.nazar.grinko.domians.Order;
import com.epam.nazar.grinko.domians.helpers.BillStatus;
import com.epam.nazar.grinko.domians.helpers.CarStatus;
import com.epam.nazar.grinko.domians.helpers.OrderStatus;
import com.epam.nazar.grinko.dto.*;
import com.epam.nazar.grinko.securities.jwt.IllegalJwtContentException;
import com.epam.nazar.grinko.securities.jwt.JwtTokenProvider;
import com.epam.nazar.grinko.services.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("car-rental-service/user")
@AllArgsConstructor
public class RegisteredUserController {


    private final CarService carService;
    private final UserService userService;
    private final OrderService orderService;
    private final BillService billService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/cars")
    public String showAllCarsPage(Model model){
        List<Car> allCars = carService.getCarsWithStatus(CarStatus.NOT_RENTED);
        List<CarDto> allCarDto = allCars.stream().map(carService::convertCarToCarDto).collect(Collectors.toList());
        List<Long> allId = allCars.stream().map(Car::getId).collect(Collectors.toList());

        model.addAttribute("cars", allCarDto);
        model.addAttribute("ids", allId);

        return "user/show-cars";
    }

    @GetMapping("/cars/{id}/book")
    public String showBookPage(Model model, @PathVariable long id){
        CarDto carDto = carService.convertCarToCarDto(carService.getCarById(id));

        BillDto billDto = new BillDto();
        billDto.setDriverPrice(billService.getDriverPrice(carDto))
                .setCarPrice(carDto.getPricePerDay());

        model.addAttribute("carDto", carDto);
        model.addAttribute("billDto", billDto);

        return "user/book-form";
    }

    @PostMapping("/cars/{id}/book")
    public String calculateBill(@ModelAttribute("billDto") BillDto billDto, RedirectAttributes redirectAttributes,
                                @PathVariable long id){
        if(!billDto.isWithDriver()) billDto.setDriverPrice(0);
        billDto.setTotalPrice(billService.getTotalPrice(billDto));

        redirectAttributes.addFlashAttribute("billDto", billDto);
        String url = "/car-rental-service/user/cars/" + id + "/book/payment";

        return "redirect:" + url;
    }

    @GetMapping("/cars/{id}/book/payment")
    public String showPaymentRequisitesPage(Model model, @PathVariable long id){

        BillDto billDto = (BillDto) model.getAttribute("billDto");

        model.addAttribute("billDto", billDto);
        model.addAttribute("paymentDetailsDto", new PaymentDetailsDto());

        return "user/payment-form";
    }

    @PostMapping("/cars/{id}/book/payment")
    public String processPayment(@ModelAttribute("billDto") BillDto billDto,
                                 @ModelAttribute("paymentDetailsDto") PaymentDetailsDto paymentDetailsDto,
                                 Model model, @PathVariable long id){
        CarDto carDto = carService.convertCarToCarDto(carService.getCarById(id));
        if(!carDto.getStatus().equals(CarStatus.NOT_RENTED)){
            model.addAttribute(ViewExceptionsConstants.CAR_ALREADY_RENTED_EXCEPTION, true);

            return "redirect:/car-rental-service/user/cars";
        }
        carDto.setStatus(CarStatus.ON_PROCESSING);

        UserDto userDto = userService.convertUserToUserDto(userService.getUserById(id));

        OrderDto orderDto = new OrderDto().setCar(carDto)
                .setUser(userDto)
                .setStatus(OrderStatus.IN_USE);

        billDto.setOrder(orderDto)
                .setPaymentDetails(paymentDetailsDto)
                .setStatus(BillStatus.PAID);

        billService.addBill(billService.convertBillDtoToBill(billDto));

        return "redirect:/car-rental-service/user/active-orders";
    }

    @GetMapping("/active-orders")
    public String showActiveOrdersPage(Model model, HttpServletRequest request){
        Long id = userService.getUserIdByEmail(jwtTokenProvider.getUsername(
                jwtTokenProvider.resolveToken(request))
        ).orElseThrow(IllegalJwtContentException::new);

        List<Order> orders = orderService.getAllOrdersByUserIdAndStatus(
                id, OrderStatus.IN_USE, OrderStatus.REPAIR_PAYMENT);

        List<OrderDto> ordersDto = orders.stream().map(orderService::convertOrderToOrderDto).collect(Collectors.toList());
        List<Long> ids = orders.stream().map(Order::getId).collect(Collectors.toList());

        model.addAttribute("orders", ordersDto);
        model.addAttribute("ids", ids);

        return "user/show-active-orders";
    }

    @GetMapping("/active-orders/{id}")
    public String showActiveOrderPage(Model model, @PathVariable String id){

        return "user/show-active-orders";
    }

}
