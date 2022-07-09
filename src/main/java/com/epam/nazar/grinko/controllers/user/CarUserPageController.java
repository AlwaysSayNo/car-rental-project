package com.epam.nazar.grinko.controllers.user;

import com.epam.nazar.grinko.domians.helpers.CarStatus;
import com.epam.nazar.grinko.dto.CarDto;
import com.epam.nazar.grinko.services.car.CarService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/car-rental-service/user/cars/{id}")
@AllArgsConstructor
@Slf4j
public class CarUserPageController {

    private final CarService carService;

    @GetMapping()
    public String showCarPage(@PathVariable("id") Long id, Model model){
        log.info("USER showPage: carId={}", id);
        CarDto carDto = carService.mapToDto(carService.getById(id));

        if(!CarStatus.NOT_RENTED.name().equals(carDto.getStatus())) {
            log.info("USER showCarPage ERROR: cars status {} != NOT_RENTED", carDto.getStatus());
            return "redirect:/car-rental-service/user/cars";
        }

        model.addAttribute("car", carDto);

        return "user/cars/car";
    }
    
}
