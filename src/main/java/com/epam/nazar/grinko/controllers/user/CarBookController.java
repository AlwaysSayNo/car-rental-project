package com.epam.nazar.grinko.controllers.user;

import com.epam.nazar.grinko.constants.CarBookConstants;
import com.epam.nazar.grinko.domians.Bill;
import com.epam.nazar.grinko.domians.Car;
import com.epam.nazar.grinko.domians.Order;
import com.epam.nazar.grinko.domians.User;
import com.epam.nazar.grinko.domians.helpers.BillStatus;
import com.epam.nazar.grinko.domians.helpers.CarStatus;
import com.epam.nazar.grinko.domians.helpers.OrderStatus;
import com.epam.nazar.grinko.domians.helpers.UserStatus;
import com.epam.nazar.grinko.dto.*;
import com.epam.nazar.grinko.exceptions.IllegalJwtContentException;
import com.epam.nazar.grinko.exceptions.IllegalPathVariableException;
import com.epam.nazar.grinko.exceptions.OrdersOverflowException;
import com.epam.nazar.grinko.securities.jwt.JwtTokenProvider;
import com.epam.nazar.grinko.services.BillService;
import com.epam.nazar.grinko.services.car.CarService;
import com.epam.nazar.grinko.services.order.OrderService;
import com.epam.nazar.grinko.services.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/car-rental-service/user/cars/{id}/book")
@AllArgsConstructor
@Slf4j
public class CarBookController {

    private final UserService userService;
    private final CarService carService;
    private final BillService billService;
    private final OrderService orderService;
    private final JwtTokenProvider jwtTokenProvider;
    private final CarBookConstants bookConstants;

    @GetMapping()
        public String showBookPage(@PathVariable("id") Long carId, Model model){
        CarDto carDto = carService.mapToDto(carService.getById(carId));
        BillDto billDto = new BillDto()
                .setDriverPrice(billService.getDriverPrice(carDto))
                .setCarPrice(carDto.getPricePerDay());

        model.addAttribute("car", carDto);
        model.addAttribute("bill", billDto);
        model.addAttribute("id", carId);

        return "/user/cars/book-car";
    }

    @PostMapping()
    public String calculateBill(@PathVariable("id") Long carId,
                                @Valid @ModelAttribute("bill") BillDto billDto, BindingResult bindingResult,
                                RedirectAttributes redirectAttributes){
        if(hasDateErrors(billDto, bindingResult)){
            String url = "/car-rental-service/user/cars/" + carId +"/book";
            return "redirect:" + url;
        }

        CarDto carDto = carService.mapToDto(carService.getById(carId));
        long driverPrice = billDto.isWithDriver() ? billService.getDriverPrice(carDto) : 0;

        billDto.setDriverPrice(driverPrice)
                .setCarPrice(carDto.getPricePerDay());

        billDto.setTotalPrice(billService.getTotalPrice(billDto));

        redirectAttributes.addFlashAttribute("bill", billDto);
        String url = "/car-rental-service/user/cars/" + carId + "/book/payment";

        log.info("USER-CALCULATE-BILL SUCCESS: car={}, bill={}", billDto, carDto);

        return "redirect:" + url;
    }

    @GetMapping("/payment")
    public String showPaymentRequisitesPage(@PathVariable("id") Long carId, Model model){
        BillDto billDto = (BillDto) model.getAttribute("bill");

        model.addAttribute("bill", billDto);
        model.addAttribute("id", carId);

        return "/user/cars/payment-form";
    }

    @PostMapping("/payment")
    public String processPayment(@PathVariable("id") Long carId,
                                 @ModelAttribute("bill") BillDto billDto,
                                 HttpServletRequest request){
        Long userId = userService.getUserIdByEmail(
                jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(request))
        ).orElseThrow(IllegalJwtContentException::new);

        Car car = carService.getById(carId);
        car.setStatus(CarStatus.ON_PROCESSING);

        User user = userService.getById(userId);

        Order order = new Order().setCar(car)
                .setUser(user)
                .setStatus(OrderStatus.UNDER_CONSIDERATION);

        billDto.setStatus(BillStatus.PAID);
        Bill bill = localMap(billDto).setOrder(order);

        billService.save(bill);

        log.info("USER-PAYMENT SUCCESS: userId={}, carId={}, newBill={}, newOrder={}",
                userId, carId, billDto, order);

        return "redirect:/car-rental-service/user/active-orders";
    }

    @ModelAttribute
    private void checkRequestValidity(@PathVariable("id") Long carId){
        Car car = carService.getById(carId);

        if(!CarStatus.NOT_RENTED.equals(car.getStatus())) {
            log.info("USER-CAR-BOOK FAILURE: car status {} != NOT_RENTED", car.getStatus());
            throw new IllegalPathVariableException();
        }
    }

    @ModelAttribute
    private void checkUserStatus(HttpServletRequest request){
        User user = userService.getByEmail(
                jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(request))
        ).orElseThrow(IllegalPathVariableException::new);

        List<Order> orderList = orderService.getAllByUserIdAndStatus(user.getId(),
                OrderStatus.IN_USE, OrderStatus.REPAIR_PAYMENT, OrderStatus.UNDER_CONSIDERATION);

        if(bookConstants.MAX_BOOK_AMOUNT() <= orderList.size()) {
            log.info("USER-CAR-BOOK FAILURE: user`s orders amount {} already !< {}",
                    orderList.size(), bookConstants.MAX_BOOK_AMOUNT());
            throw new OrdersOverflowException();
        }

        if(!UserStatus.ACTIVE.equals(user.getStatus())) {
            log.info("USER-CAR-BOOK FAILURE: user status {} != ACTIVE", user.getStatus());
            throw new IllegalPathVariableException();
        }
    }

    private boolean hasDateErrors(BillDto billDto, BindingResult bindingResult){
        List<String> additionalErrors = bindingResult.getFieldErrors().stream().map(FieldError::getField).filter(field -> {
            List<String> fields = Arrays.asList("startDate", "expirationDate");
            return fields.contains(field);
        }).collect(Collectors.toList());

        return additionalErrors.size() > 0;
    }

    private Bill localMap(BillDto billDto){
        return new Bill().setStartDate(billDto.getStartDate())
                .setExpirationDate(billDto.getExpirationDate())
                .setCarPrice(billDto.getCarPrice())
                .setDriverPrice(billDto.getDriverPrice())
                .setTotalPrice(billDto.getTotalPrice())
                .setWithDriver(billDto.isWithDriver())
                .setStatus(billDto.getStatus());
    }

}
