package com.epam.nazar.grinko.controllers.admin;

import com.epam.nazar.grinko.domians.Car;
import com.epam.nazar.grinko.domians.User;
import com.epam.nazar.grinko.domians.helpers.UserStatus;
import com.epam.nazar.grinko.dto.CarDto;
import com.epam.nazar.grinko.dto.UserDto;
import com.epam.nazar.grinko.exceptions.IllegalPathVariableException;
import com.epam.nazar.grinko.domians.helpers.UserRole;
import com.epam.nazar.grinko.services.CarService;
import com.epam.nazar.grinko.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("car-rental-service/admin")
@AllArgsConstructor
public class AdminController {

    private final CarService carService;
    private final UserService userService;

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

}
