package com.epam.nazar.grinko.controllers.admin;

import com.epam.nazar.grinko.domians.User;
import com.epam.nazar.grinko.dto.UserDto;
import com.epam.nazar.grinko.domians.helpers.UserRole;
import com.epam.nazar.grinko.services.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("car-rental-service/admin")
@AllArgsConstructor
@Slf4j
public class AdminController {

    private final UserService userService;

    @GetMapping("/managers")
    public String showAllManagers(@RequestParam(value = "sortBy", required = false) String sortBy,
                                  @RequestParam(value = "direction", required = false) String direction,
                                  @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                  @RequestParam(value = "size", required = false, defaultValue = "8") Integer size,
                                  @RequestParam(value = "filterBy", required = false) String filterBy,
                                  @RequestParam(value = "filterValue", required = false) String filterValue, Model model){
        PageRequest pageRequest = userService.getManipulationService().createRequest(page - 1, size, sortBy, direction);
        Page<User> managers = userService.getUsersByRole(pageRequest, UserRole.ROLE_MANAGER, filterBy, filterValue);

        Page<UserDto> managersDto = managers.map(userService::mapToDto);
        List<Long> allId = managers.stream().map(User::getId).collect(Collectors.toList());

        model.addAttribute("managers", managersDto);
        model.addAttribute("ids", allId);

        return "admin/managers/show-managers";
    }


    @GetMapping("/registered-users")
    public String showAllRegistered(@RequestParam(value = "sortBy", required = false) String sortBy,
                                    @RequestParam(value = "direction", required = false) String direction,
                                    @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                    @RequestParam(value = "size", required = false, defaultValue = "8") Integer size,
                                    @RequestParam(value = "filterBy", required = false) String filterBy,
                                    @RequestParam(value = "filterValue", required = false) String filterValue, Model model){
        PageRequest pageRequest = userService.getManipulationService().createRequest(page - 1, size, sortBy, direction);
        Page<User> users = userService.getUsersByRole(pageRequest, UserRole.ROLE_USER, filterBy, filterValue);

        Page<UserDto> usersDto = users.map(userService::mapToDto);
        List<Long> allId = users.stream().map(User::getId).collect(Collectors.toList());

        model.addAttribute("users", usersDto);
        model.addAttribute("ids", allId);

        return "admin/users/show-users";
    }

}
