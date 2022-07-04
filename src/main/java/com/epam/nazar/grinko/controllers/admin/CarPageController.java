package com.epam.nazar.grinko.controllers.admin;

import com.epam.nazar.grinko.domians.helpers.CarStatus;
import com.epam.nazar.grinko.dto.CarDto;
import com.epam.nazar.grinko.services.car.CarService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("car-rental-service/admin/cars/{id}")
@AllArgsConstructor
@Slf4j
public class CarPageController {

    private final CarService carService;

    @GetMapping()
    public String showPage(@PathVariable("id") Long id, Model model){
        log.info("ADMIN showPage: carId={}", id);
        CarDto carDto = carService.mapToDto(carService.getById(id));
        model.addAttribute("car", carDto);

        return "admin/cars/car";
    }

    @ModelAttribute("statuses")
    private List<CarStatus> addStatusesAttribute(){
        return Arrays.asList(CarStatus.NOT_RENTED, CarStatus.ON_HOLD);
    }

}
