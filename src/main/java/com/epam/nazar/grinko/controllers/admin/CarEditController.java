package com.epam.nazar.grinko.controllers.admin;

import com.epam.nazar.grinko.constants.ViewExceptionsConstants;
import com.epam.nazar.grinko.domians.Car;
import com.epam.nazar.grinko.domians.CarBrand;
import com.epam.nazar.grinko.domians.CarColor;
import com.epam.nazar.grinko.domians.helpers.CarSegment;
import com.epam.nazar.grinko.domians.helpers.CarStatus;
import com.epam.nazar.grinko.dto.CarDto;
import com.epam.nazar.grinko.exceptions.IllegalPathVariableException;
import com.epam.nazar.grinko.services.car.CarBrandService;
import com.epam.nazar.grinko.services.car.CarColorService;
import com.epam.nazar.grinko.services.car.CarService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("car-rental-service/admin/cars/{id}/edit")
@AllArgsConstructor
public class CarEditController {

    private final CarBrandService carBrandService;
    private final CarColorService carColorService;
    private final CarService carService;

    @GetMapping()
    public String showCarEditPage(@PathVariable("id") Long carId, Model model){
        CarDto carDto = carService.mapToDto(carService.getById(carId));

        model.addAttribute("carDto", carDto);
        model.addAttribute("carId", carId);

        return "admin/cars/edit-car-by-id";
    }

    @PutMapping()
    public String saveCarChanges(@PathVariable("id") Long carId, @ModelAttribute("carDto") CarDto carDto,
                                 Model model){
        Car car = carService.mapToObject(carDto);

        if(existsAnotherWithNumber(car)){
            model.addAttribute(ViewExceptionsConstants.CAR_NUMBER_ALREADY_EXISTS_EXCEPTION, true);
            model.addAttribute("carDto", carDto);
            model.addAttribute("carId", carId);

            return "admin/cars/edit-car-by-id";
        }

        carService.updateCarById(carId, car);
        return "redirect:/car-rental-service/admin/cars";
    }

    @PatchMapping()
    public String changeCarStatus(@PathVariable("id") Long carId, @RequestParam("status") String newStatus){
        CarStatus status = CarStatus.valueOf(newStatus);

        if(status.equals(CarStatus.RENTED))
            throw new IllegalPathVariableException("Car with id " + carId + " cannot be modified");

        carService.updateCarStatusById(carId, status);

        return "redirect:/car-rental-service/admin/cars";
    }

    @DeleteMapping()
    public String deleteCar(@PathVariable("id") Long carId){
        carService.deleteById(carId);
        return "redirect:/car-rental-service/admin/cars";
    }


    @ModelAttribute
    public void checkRequestValidity(@PathVariable("id") Long carId) {
        Car car = carService.getById(carId);
        CarStatus status = car.getStatus();

        if(status.equals(CarStatus.RENTED) || status.equals(CarStatus.ON_PROCESSING))
            throw new IllegalPathVariableException("Car with id " + carId + " cannot be modified");
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

    @ModelAttribute("statuses")
    private List<CarStatus> addStatusesAttribute(){
        return Arrays.asList(CarStatus.NOT_RENTED, CarStatus.ON_HOLD);
    }


    private boolean existsAnotherWithNumber(Car car){
        Optional<Car> tmp = carService.getByNumber(car.getNumber());
        return tmp.isPresent() && !car.getId().equals(tmp.get().getId());
    }

}
