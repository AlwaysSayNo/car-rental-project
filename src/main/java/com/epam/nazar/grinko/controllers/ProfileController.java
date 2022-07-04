package com.epam.nazar.grinko.controllers;

import com.epam.nazar.grinko.constants.ViewExceptionsConstants;
import com.epam.nazar.grinko.domians.User;
import com.epam.nazar.grinko.dto.UserDto;
import com.epam.nazar.grinko.exceptions.IllegalJwtContentException;
import com.epam.nazar.grinko.securities.jwt.JwtTokenProvider;
import com.epam.nazar.grinko.services.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/car-rental-service")
@Slf4j
@AllArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final JwtTokenProvider tokenProvider;

    @GetMapping("/{userRole}/profile")
    public String showProfilePage(HttpServletRequest request, Model model,
                                  @PathVariable("userRole") String role){
        String username = tokenProvider.getUsername(tokenProvider.resolveToken(request));
        Optional<User> user = userService.getByEmail(username);
        UserDto userDto = userService.mapToDto(user.orElseThrow(IllegalJwtContentException::new));

        model.addAttribute("user", userDto);
        model.addAttribute("role", role);

        return "common/profile";
    }

    @GetMapping("/{userRole}/profile/edit")
    public String showEditProfilePage(@PathVariable("userRole") String role,
                                      HttpServletRequest request, Model model){
        String username = tokenProvider.getUsername(tokenProvider.resolveToken(request));
        User user = userService.getByEmail(username).orElseThrow(IllegalJwtContentException::new);

        UserDto userDto = userService.mapToDto(user);

        model.addAttribute("user", userDto);
        model.addAttribute("oldEmail", user.getEmail());
        model.addAttribute("oldPassword", user.getPassword());
        model.addAttribute("role", role);

        return "common/edit-profile";
    }

    //TODO сделать метод красивее
    @PostMapping("/{userRole}/profile/edit")
    public String saveUserChanges(@Valid @ModelAttribute("user") UserDto userDto, BindingResult bindingResult,
                                  @ModelAttribute("oldEmail") String oldEmail,
                                  @PathVariable("userRole") String role, Model model){
        if(bindingResult.hasErrors() && !checkOldPassword(bindingResult, userDto, oldEmail)) {
            log.info("PROFILE-EDIT FAILURE: invalid data.");
            String url = "/car-rental-service/" + role + "/profile/edit";
            return "redirect:" + url;
        }

        User oldEmailUser = userService.getByEmail(oldEmail).orElseThrow(IllegalJwtContentException::new);
        User currEmailUser = userService.getByEmail(userDto.getEmail()).orElse(null);

        if(currEmailUser != null && !oldEmailUser.getId().equals(currEmailUser.getId())){
            log.info("PROFILE-EDIT FAILURE: oldEmail={}, currEmail={}, oldEmailUser.id={}, currEmailUser.id={})",
                    oldEmailUser.getEmail(), currEmailUser.getEmail(), oldEmailUser.getId(), currEmailUser.getId());
            model.addAttribute(ViewExceptionsConstants.USER_ALREADY_EXISTS_EXCEPTION, true);

            model.addAttribute("user", userDto);
            model.addAttribute("oldEmail", oldEmail);
            model.addAttribute("oldPassword", oldEmailUser.getPassword());
            model.addAttribute("role", role);

            return "common/edit-profile";
        }

        if(!userDto.getPassword().equals(oldEmailUser.getPassword()))
            userDto.setPassword(userService.encodePassword(userDto.getPassword()));

        userService.updateUserById(
                userService.mapToObject(userDto), oldEmailUser.getId()
        );
        log.info("PROFILE-EDIT SUCCESS: oldData={}, newData={}", userService.mapToDto(oldEmailUser), userDto);

        String url = "/car-rental-service/" + role + "/profile";
        return "redirect:" + url;
    }

    private boolean checkOldPassword(BindingResult bindingResult, UserDto userDto, String oldEmail) {
        long nonPasswordErrorsCount = bindingResult.getFieldErrors()
                .stream().map(FieldError::getField)
                .filter(name -> !"password".equals(name)).count();
        if(nonPasswordErrorsCount != 0) return false;

        String oldEncrypted = userService.getByEmail(oldEmail)
                .orElseThrow(IllegalArgumentException::new).getPassword();
        return userDto.getPassword().equals(oldEncrypted);
    }

}
