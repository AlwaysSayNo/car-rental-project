package com.epam.nazar.grinko.controllers.admin;

import com.epam.nazar.grinko.constants.ViewExceptionsConstants;
import com.epam.nazar.grinko.domians.helpers.UserRole;
import com.epam.nazar.grinko.domians.helpers.UserStatus;
import com.epam.nazar.grinko.dto.UserDto;
import com.epam.nazar.grinko.services.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/car-rental-service/admin/managers")
@AllArgsConstructor
@Slf4j
public class ManagerCreationController {

    private final UserService userService;

    @GetMapping("/new")
    public String showManagerCreationPage(Model model){
        model.addAttribute("manager", new UserDto());

        return "admin/managers/add-manager";
    }

    @PostMapping("/new")
    public String createNewManager(@Valid @ModelAttribute("manager") UserDto userDto,
                                   BindingResult bindingResult,
                                   Model model){
        if(bindingResult.hasErrors()) {
            log.info("CAR-EDIT FAILURE: invalid data.");

            return "redirect:/car-rental-service/admin/managers/new";
        }

        if(userService.existsByEmail(userDto.getEmail())){
            Long managerId = userService.getUserIdByEmail(userDto.getEmail())
                    .orElseThrow(IllegalArgumentException::new);
            log.info("MANAGER-CREATION FAILURE: oldEmailUser.id={}, email={}",
                    managerId, userDto.getEmail());

            model.addAttribute(ViewExceptionsConstants.USER_ALREADY_EXISTS_EXCEPTION, true);
            model.addAttribute("manager", userDto);

            return "admin/managers/add-manager";
        }

        userDto.setRole(UserRole.ROLE_MANAGER.name())
                .setStatus(UserStatus.ACTIVE.name())
                .setPassword(userService.encodePassword(userDto.getPassword()));
        userService.save(userService.mapToObject(userDto));


        Long managerId = userService.getUserIdByEmail(userDto.getEmail())
                .orElseThrow(IllegalArgumentException::new);
        log.info("MANAGER-CREATION SUCCESS: manager.id={}, manager={}", managerId, userDto);

        return "redirect:/car-rental-service/admin/managers";
    }

}
