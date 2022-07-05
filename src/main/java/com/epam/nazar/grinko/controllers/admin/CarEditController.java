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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("/car-rental-service/admin/cars/{id}/edit")
@AllArgsConstructor
@Slf4j
public class CarEditController {

    private final CarBrandService carBrandService;
    private final CarColorService carColorService;
    private final CarService carService;

    @GetMapping()
    public String showCarEditPage(@PathVariable("id") Long carId, Model model){
        CarDto carDto = carService.mapToDto(carService.getById(carId));

        model.addAttribute("car", carDto);
        model.addAttribute("id", carId);

        return "admin/cars/edit-car";
    }

    @PostMapping()
    public String saveCarChanges(@PathVariable("id") Long carId,
                                 @Valid @ModelAttribute("car") CarDto carDto, BindingResult bindingResult,
                                 Model model){
        CarDto oldCarDto = carService.mapToDto(carService.getById(carId));

        if(bindingResult.hasErrors()) {
            log.info("CAR-EDIT FAILURE: invalid data.");

            return "admin/cars/edit-car";
        }

        if(existsAnotherWithNumber(carDto.getNumber(), carId)){
            log.info("CAR-EDIT FAILURE: carId={}, oldNumber={}, newNumber={}",
                    carId, oldCarDto.getNumber(), carDto.getNumber());

            model.addAttribute(ViewExceptionsConstants.CAR_NUMBER_ALREADY_EXISTS_EXCEPTION, true);
            model.addAttribute("car", carDto);
            return "admin/cars/edit-car";
        }

        carColorService.addColorIfExists(carDto.getColor());
        carBrandService.addBrandIfNotExists((carDto.getBrand()));

        carService.updateCarById(carId, carService.mapToObject(carDto).setId(carId));
        log.info("CAR-EDIT SUCCESS: carId={}, oldData={}, newData={}",
                carId, oldCarDto, carDto);

        return "redirect:/car-rental-service/admin/cars";
    }

    @PostMapping("/change-status")
    public String changeCarStatus(@PathVariable("id") Long carId,
                                  @RequestParam("newStatus") String newStatus){
        CarStatus status = CarStatus.valueOf(newStatus);
        Car car = carService.getById(carId);

        if(car.getStatus().equals(status)) {
            log.info("CAR-EDIT-STATUS FAILURE: oldStatus={}, newStatus={}", car.getStatus().name(), status.name());
            return "redirect:/car-rental-service/admin/cars/" + carId;
        }

        carService.updateCarStatusById(carId, status);
        log.info("CAR-EDIT-STATUS SUCCESS: carId={}, oldStatus={}, newStatus={}",
                carId, car.getStatus().name(), status.name());

        return "redirect:/car-rental-service/admin/cars";
    }

    @PostMapping("/delete")
    public String deleteCar(@PathVariable("id") Long carId){
        carService.deleteById(carId);
        return "redirect:/car-rental-service/admin/cars";
    }


    @ModelAttribute
    public void checkRequestValidity(@PathVariable("id") Long carId) {
        Car car = carService.getById(carId);
        CarStatus status = car.getStatus();

        if(!getAvailableStatuses().contains(status.name()))
            throw new IllegalPathVariableException("Car with id " + carId + " cannot be modified");
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

    private List<String> getAvailableStatuses() {
        return Stream.of(CarStatus.NOT_RENTED, CarStatus.ON_HOLD)
                .map(CarStatus::name)
                .collect(Collectors.toList());
    }

    private boolean existsAnotherWithNumber(String number, Long carId){
        Optional<Car> tmp = carService.getByNumber(number);
        return tmp.isPresent() && !carId.equals(tmp.get().getId());
    }

}
