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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("/car-rental-service/admin/cars/{id}")
@AllArgsConstructor
@Slf4j
public class CarAdminPageController {

    private final CarService carService;

    @GetMapping()
    public String showCarPage(@PathVariable("id") Long id, Model model){
        log.info("ADMIN showPage: carId={}", id);
        CarDto carDto = carService.mapToDto(carService.getById(id));
        model.addAttribute("car", carDto);

        return "admin/cars/car";
    }

    @ModelAttribute("statuses")
    private List<String> addStatusesAttribute(){
        return getAvailableStatuses();
    }

    private List<String> getAvailableStatuses() {
        return Stream.of(CarStatus.NOT_RENTED, CarStatus.ON_HOLD)
                .map(CarStatus::name)
                .collect(Collectors.toList());
    }

}
