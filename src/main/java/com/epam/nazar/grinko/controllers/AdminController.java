package com.epam.nazar.grinko.controllers;

import com.epam.nazar.grinko.constants.ViewExceptionsConstants;
import com.epam.nazar.grinko.domians.Car;
import com.epam.nazar.grinko.domians.CarBrand;
import com.epam.nazar.grinko.domians.CarColor;
import com.epam.nazar.grinko.domians.User;
import com.epam.nazar.grinko.domians.helpers.CarSegment;
import com.epam.nazar.grinko.domians.helpers.CarStatus;
import com.epam.nazar.grinko.domians.helpers.UserStatus;
import com.epam.nazar.grinko.dto.CarDto;
import com.epam.nazar.grinko.dto.UserDto;
import com.epam.nazar.grinko.exceptions.IllegalPathVariableException;
import com.epam.nazar.grinko.securities.UserRole;
import com.epam.nazar.grinko.services.CarBrandService;
import com.epam.nazar.grinko.services.CarColorService;
import com.epam.nazar.grinko.services.CarService;
import com.epam.nazar.grinko.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("car-rental-service/admin")
@AllArgsConstructor
public class AdminController {

    private final CarService carService;
    private final UserService userService;
    private final CarBrandService carBrandService;
    private final CarColorService carColorService;


    // ? pagination
    @GetMapping("/cars")
    public String showAllCarsPage(Model model){
        List<Car> allCars = carService.getAll();
        List<CarDto> allCarDto = allCars.stream().map(carService::mapToDto).collect(Collectors.toList());
        List<Long> allId = allCars.stream().map(Car::getId).collect(Collectors.toList());

        model.addAttribute("cars", allCarDto);
        model.addAttribute("ids", allId);

        return "admin/cars/show-cars";
    }

    @GetMapping("/cars/new")
    public String showCarCreationPage(Model model, CarDto carDto){
        model.addAttribute("carDto", carDto);
        return "admin/cars/add-new-car";
    }

    @PostMapping("/cars/new")
    public String createNewCar(@ModelAttribute("carDto") CarDto carDto, Model model){
        if(carService.existsCarByNumber(carDto.getNumber())){
            model.addAttribute(ViewExceptionsConstants.CAR_NUMBER_ALREADY_EXISTS_EXCEPTION, true);
            model.addAttribute("carDto", new CarDto());

            return "admin/cars/add-new-car";
        }

        carColorService.addColorIfExists(carDto.getColor());
        carBrandService.addBrandIfExists((carDto.getBrand()));

        Car car = carService.mapToObject(carDto);
        carService.save(car);

        return "redirect:/car-rental-service/admin/cars";
    }

    @GetMapping("/cars/{id}/edit")
    public String showCarEditPage(Model model, @PathVariable long id){
        Car car = carService.getById(id);
        CarDto carDto = carService.mapToDto(car);

        model.addAttribute("carDto", carDto);
        model.addAttribute("carId", id);

        return "admin/cars/edit-car-by-id";
    }

    @PatchMapping("/cars/{id}/edit")
    public String saveCarChanges(@ModelAttribute("carDto") CarDto carDto, @PathVariable long id, Model model){
        Car currCar = carService.mapToObject(carDto);
        Car tmp = carService.getByNumber(currCar.getNumber()).orElseThrow(NullPointerException::new);

        if(!tmp.getId().equals(id)){
            model.addAttribute(ViewExceptionsConstants.CAR_NUMBER_ALREADY_EXISTS_EXCEPTION, true);
            model.addAttribute("carId", id);
            model.addAttribute("carDto", carDto);
            return "admin/cars/edit-car-by-id";
        }

        carService.updateCarById(id, currCar);
        return "redirect:/car-rental-service/admin/cars";
    }

    @DeleteMapping("/cars/{id}/edit")
    public String deleteCar(@PathVariable long id){
        carService.deleteById(id);
        return "redirect:/car-rental-service/admin/cars";
    }


    // ? pagination
    @GetMapping("/managers")
    public String showAllManagers(Model model){
        List<User> allManagers = userService.getUsersByRole(UserRole.ROLE_MANAGER);
        List<UserDto> allManagersDto = allManagers.stream().map(userService::convertUserToUserDto)
                .collect(Collectors.toList());
        List<Long> allId = allManagers.stream().map(User::getId).collect(Collectors.toList());

        model.addAttribute("managers", allManagersDto);
        model.addAttribute("ids", allId);

        return "admin/managers/show-managers";
    }

    @GetMapping("/manager/new")
    public String showManagerCreationPage(Model model){
        model.addAttribute("userDto", new UserDto());
        return "admin/managers/add-new-manager";
    }

    @PostMapping("/managers/new")
    public String createNewManager(@ModelAttribute("userDto") UserDto userDto, Model model){
        if(userService.existsByEmail(userDto.getEmail())){
            model.addAttribute("userAlreadyExistsError", true);
            model.addAttribute("userDto", new UserDto());

            return "redirect:/car-rental-service/admin/managers";
        }

        userDto.setRole(UserRole.ROLE_MANAGER).setStatus(UserStatus.ACTIVE);
        userService.save(userService.mapToObject(userDto));

        return "redirect:/car-rental-service/admin/managers";
    }

    @DeleteMapping("/managers/{id}")
    public String deleteManager(@PathVariable long id){
        if(!userService.existsByIdAndRole(id, UserRole.ROLE_MANAGER)){
            throw new IllegalPathVariableException();
        }

        userService.deleteById(id);
        return "redirect:/car-rental-service/admin/managers";
    }

    @PatchMapping("/managers/{id}")
    public String changeManagerStatus(@PathVariable long id, @PathParam("status") String status){
        if(!userService.existsByIdAndRole(id, UserRole.ROLE_MANAGER)){
            throw new IllegalPathVariableException();
        }

        userService.updateUserStatusById(UserStatus.valueOf(status), id);
        return "redirect:/car-rental-service/admin/managers";
    }


    // ? pagination
    @GetMapping("/registered-users")
    public String showAllRegistered(Model model){
        List<User> allRegistered = userService.getUsersByRole(UserRole.ROLE_USER);
        List<UserDto> allRegisteredDto = allRegistered.stream().map(userService::convertUserToUserDto)
                .collect(Collectors.toList());
        List<Long> allId = allRegistered.stream().map(User::getId).collect(Collectors.toList());

        model.addAttribute("registeredUsers", allRegisteredDto);
        model.addAttribute("ids", allId);

        return "admin/users/show-users";
    }

    @DeleteMapping("/registered-users/{id}")
    public String deleteUser(@PathVariable long id){
        if(!userService.existsByIdAndRole(id, UserRole.ROLE_USER)){
            throw new IllegalPathVariableException();
        }

        userService.deleteById(id);
        return "redirect:/car-rental-service/admin/registered-users";
    }

    @PatchMapping("/registered-users/{id}")
    public String changeUserStatus(@PathVariable long id, @PathParam("status") String status){
        if(!userService.existsByIdAndRole(id, UserRole.ROLE_USER)){
            throw new IllegalPathVariableException();
        }

        userService.updateUserStatusById(UserStatus.valueOf(status), id);
        return "redirect:/car-rental-service/admin/registered-users";
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
}
