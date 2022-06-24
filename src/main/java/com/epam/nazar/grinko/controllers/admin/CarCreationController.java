package com.epam.nazar.grinko.controllers.admin;

import com.epam.nazar.grinko.constants.ViewExceptionsConstants;
import com.epam.nazar.grinko.domians.CarBrand;
import com.epam.nazar.grinko.domians.CarColor;
import com.epam.nazar.grinko.domians.helpers.CarSegment;
import com.epam.nazar.grinko.dto.CarDto;
import com.epam.nazar.grinko.services.car.CarBrandService;
import com.epam.nazar.grinko.services.car.CarColorService;
import com.epam.nazar.grinko.services.car.CarService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("car-rental-service/admin/cars")
@AllArgsConstructor
public class CarCreationController {

    private final CarService carService;
    private final CarBrandService carBrandService;
    private final CarColorService carColorService;

    @GetMapping("/new")
    public String showCarCreationPage(Model model){
        model.addAttribute("carDto", new CarDto());
        return "admin/cars/add-new-car";
    }

    @PostMapping("/new")
    public String createNewCar(@ModelAttribute("carDto") CarDto carDto, Model model){
        if(carService.existsCarByNumber(carDto.getNumber())){
            model.addAttribute(ViewExceptionsConstants.CAR_NUMBER_ALREADY_EXISTS_EXCEPTION, true);
            model.addAttribute("carDto", new CarDto());

            return "admin/cars/add-new-car";
        }

        carColorService.addColorIfExists(carDto.getColor());
        carBrandService.addBrandIfExists((carDto.getBrand()));

        carService.save(carService.mapToObject(carDto));

        return "redirect:/car-rental-service/admin/cars";
    }

    @ModelAttribute("brands")
    private List<CarBrand> addBrandsAttribute(){
        return carBrandService.getAllCarBrands();
    }

    @ModelAttribute("colors")
    private List<CarColor> addColorsAttribute(){
        return carColorService.getAll();
    }

    @ModelAttribute("segments")
    private List<CarSegment> addSegmentsAttribute(){
        return Arrays.asList(CarSegment.values());
    }

}
