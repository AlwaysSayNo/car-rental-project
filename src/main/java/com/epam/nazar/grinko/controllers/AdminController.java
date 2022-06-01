package com.epam.nazar.grinko.controllers;

import com.epam.nazar.grinko.domians.Car;
import com.epam.nazar.grinko.domians.CarBrand;
import com.epam.nazar.grinko.domians.CarColor;
import com.epam.nazar.grinko.domians.helpers.CarSegment;
import com.epam.nazar.grinko.domians.helpers.CarStatus;
import com.epam.nazar.grinko.dto.CarDto;
import com.epam.nazar.grinko.services.CarBrandService;
import com.epam.nazar.grinko.services.CarColorService;
import com.epam.nazar.grinko.services.CarService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("car-rental-service/admin")
@AllArgsConstructor
public class AdminController {

    private final CarService carService;
    private final CarBrandService carBrandService;
    private final CarColorService carColorService;

    // ? pagination
    @GetMapping("/cars")
    public String showAllCars(Model model){
        model.addAttribute("cars", carService.getAllCars());
        return "admin/show-cars";
    }

    @GetMapping("/cars/new")
    public String showCarCreationForm(Model model, CarDto carDto){
        model.addAttribute("carDto", carDto);
        return "admin/add-new-car";
    }

    // Валидация формы только на стороне клиента.
    @PostMapping("/cars/new")
    public String addNewCar(@ModelAttribute("carDto") CarDto carDto, Model model){
        if(carService.existsCarByNumber(carDto.getNumber())){
            model.addAttribute("carNumberAlreadyExistsError", true);
            model.addAttribute("carDto", new CarDto());
            return "admin/add-new-car";
        }

        if(!carColorService.carColorExists(carDto.getColor())) {
            CarColor color = new CarColor().setValue(carDto.getColor());
            carColorService.addNewColor(color);
        }
        if(!carBrandService.carBrandExists(carDto.getBrand())){
            CarBrand brand = new CarBrand().setValue(carDto.getBrand());
            carBrandService.addNewBrand(brand);
        }

        Car car = carService.convertCarDtoToCar(carDto);
        carService.addNewCar(car);

        return "redirect:/car-rental-service/admin/cars";
    }

    @GetMapping("cars/{id}/edit")
    public String showCarEditForm(Model model, @PathVariable long id){
        Car car = carService.getCarById(id);
        CarDto carDto = carService.convertCarToCarDto(car);
        model.addAttribute("carDto", carDto);
        model.addAttribute("carId", id);
        return "admin/edit-car-by-id";
    }

    @PatchMapping("cars/{id}/edit")
    public String saveCarChanges(@ModelAttribute("carDto") CarDto carDto, @PathVariable long id, Model model){
        Car currCar = carService.convertCarDtoToCar(carDto);
        Car tmp = carService.getCarByNumber(currCar.getNumber());

        if(tmp != null && !tmp.getId().equals(id)){
            model.addAttribute("carNumberAlreadyExistsError", true);
            model.addAttribute("carId", id);
            model.addAttribute("carDto", carDto);
            return "admin/edit-car-by-id";
        }

        carService.updateCarById(currCar, id);
        return "redirect:/car-rental-service/admin/cars";
    }

    @DeleteMapping("cars/{id}/edit")
    public String deleteCar(Model model, @PathVariable long id){
        carService.deleteCarById(id);
        return "redirect:/car-rental-service/admin/cars";
    }

    @ModelAttribute("brands")
    private List<CarBrand> addBrandsAttribute(){
        return carBrandService.getAllCarBrands();
    }

    @ModelAttribute("colors")
    private List<CarColor> addColorsAttribute(){
        return carColorService.getAllCarColors();
    }

    @ModelAttribute("segments")
    private List<CarSegment> addSegmentsAttribute(){
        return Arrays.asList(CarSegment.values());
    }

    @ModelAttribute("statuses")
    private List<CarStatus> addStatusesAttribute(){
        return Arrays.asList(CarStatus.NOT_RENTED, CarStatus.ON_HOLD);
    }
}
