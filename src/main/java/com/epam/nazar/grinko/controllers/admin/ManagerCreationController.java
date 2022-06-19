package com.epam.nazar.grinko.controllers.admin;

import com.epam.nazar.grinko.constants.ViewExceptionsConstants;
import com.epam.nazar.grinko.domians.helpers.UserRole;
import com.epam.nazar.grinko.domians.helpers.UserStatus;
import com.epam.nazar.grinko.dto.UserDto;
import com.epam.nazar.grinko.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("car-rental-service/admin/managers")
@AllArgsConstructor
public class ManagerCreationController {

    private final UserService userService;

    @GetMapping("/new")
    public String showManagerCreationPage(Model model){
        model.addAttribute("userDto", new UserDto());
        return "admin/managers/add-new-manager";
    }

    @PostMapping("/new")
    public String createNewManager(@ModelAttribute("userDto") UserDto userDto, Model model){
        if(userService.existsByEmail(userDto.getEmail())){
            model.addAttribute(ViewExceptionsConstants.USER_ALREADY_EXISTS_EXCEPTION, true);
            model.addAttribute("userDto", new UserDto());

            return "redirect:/car-rental-service/admin/managers";
        }

        userDto.setRole(UserRole.ROLE_MANAGER).setStatus(UserStatus.ACTIVE);
        userService.save(userService.mapToObject(userDto));

        return "redirect:/car-rental-service/admin/managers";
    }

}
