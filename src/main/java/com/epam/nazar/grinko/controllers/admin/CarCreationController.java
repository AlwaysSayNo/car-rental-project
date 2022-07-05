package com.epam.nazar.grinko.controllers.admin;

import com.epam.nazar.grinko.constants.ViewExceptionsConstants;
import com.epam.nazar.grinko.domians.CarBrand;
import com.epam.nazar.grinko.domians.CarColor;
import com.epam.nazar.grinko.domians.helpers.CarSegment;
import com.epam.nazar.grinko.domians.helpers.CarStatus;
import com.epam.nazar.grinko.dto.CarDto;
import com.epam.nazar.grinko.services.car.CarBrandService;
import com.epam.nazar.grinko.services.car.CarColorService;
import com.epam.nazar.grinko.services.car.CarService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("car-rental-service/admin/cars")
@AllArgsConstructor
@Slf4j
public class CarCreationController {

    private final CarService carService;
    private final CarBrandService carBrandService;
    private final CarColorService carColorService;

    @GetMapping("/new")
    public String showCarCreationPage(Model model){
        model.addAttribute("car", new CarDto());
        return "admin/cars/add-car";
    }

    @PostMapping("/new")
    public String createNewCar(@Valid @ModelAttribute("car") CarDto carDto, BindingResult bindingResult,
                               Model model){
        if(bindingResult.hasErrors()) {
            log.info("CAR-CREATION FAILURE: invalid data.");
            return "admin/cars/edit-car";
        }

        if(carService.existsCarByNumber(carDto.getNumber())){
            log.info("CAR-CREATION FAILURE: car with number {} already exists.", carDto.getNumber());
            model.addAttribute(ViewExceptionsConstants.CAR_NUMBER_ALREADY_EXISTS_EXCEPTION, true);
            model.addAttribute("car", carDto);

            return "admin/cars/add-car";
        }

        carColorService.addColorIfExists(carDto.getColor());
        carBrandService.addBrandIfNotExists((carDto.getBrand()));

        carDto.setStatus(CarStatus.ON_HOLD.name());
        carService.save(carService.mapToObject(carDto));

        Long carId  = carService.getIdByNumber(carDto.getNumber());
        log.info("CAR-CREATION SUCCESS: carId={}, newCar={}", carId, carDto);

        return "redirect:/car-rental-service/admin/cars";
    }

    @ModelAttribute("brands")
    private List<String> addBrandsAttribute(){
        return getAvailableBrands();
    }

    @ModelAttribute("colors")
    private List<String> addColorsAttribute(){
        return getAvailableColors();
    }

    @ModelAttribute("segments")
    private List<String> addSegmentsAttribute(){
        return Arrays.stream(CarSegment.values()).map(CarSegment::name).collect(Collectors.toList());
    }

    private List<String> getAvailableBrands(){
        return carBrandService.getAll().stream()
                .map(CarBrand::getValue)
                .collect(Collectors.toList());
    }

    private List<String> getAvailableColors(){
        return carColorService.getAll().stream()
                .map(CarColor::getValue)
                .collect(Collectors.toList());
    }

}
