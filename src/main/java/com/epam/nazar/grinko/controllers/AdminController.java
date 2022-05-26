package com.epam.nazar.grinko.controllers;

import com.epam.nazar.grinko.domians.Car;
import com.epam.nazar.grinko.services.CarService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/car-rental-service/admin")
public class AdminController {

    private final CarService carService;

    public AdminController(CarService carService) {
        this.carService = carService;
    }

    // pagination
    @GetMapping("/cars")
    public String showAllCars(Model model){
        model.addAttribute("cars", carService.getAllCars());
        return "admin/show-cars";
    }

    @GetMapping("/cars/new")
    public String showCarCreationForm(Model model, Car car){
        model.addAttribute("car", car);
        return "admin/add-new-car";
    }

    // Валидация формы на стороне клиента.
    @PostMapping("/cars")
    public String addNewCar(Model model, @ModelAttribute("car") Car car){
        boolean isAdded = carService.addNewCar(car);
        if(!isAdded) {
            model.addAttribute("alreadyExistsError", true);
            return "admin/add-new-car";
        }
        return "redirect:/cars";
    }

    @GetMapping("/cars/{id}")
    public String showCarWithId(Model model, @PathVariable long id){
        Optional<Car> carOptional = carService.getCarById(id);
        if(carOptional.isPresent()) {
            model.addAttribute("car", carOptional.get());
            return "admin/show-car-with-id";
        }
        // Exception handler
        return "errors/404";
    }

    @DeleteMapping("cars/{id}")
    public String deleteCarWithId(@PathVariable long id){
        boolean isDeleted = carService.deleteCarById(id);
        if(isDeleted) return "redirect:/cars";
        // Exception handler
        return "errors/404";
    }

    @GetMapping("cars/{id}/edit")
    public String editCarWithId(Model model, @PathVariable long id){
        Optional<Car> carOptional = carService.getCarById(id);
        if(!carOptional.isPresent()) return "errors/404";

        model.addAttribute("car", carOptional.get());
        return "";
    }



}
