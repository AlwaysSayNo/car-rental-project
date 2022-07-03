package com.epam.nazar.grinko.controllers;

import com.epam.nazar.grinko.constants.ViewExceptionsConstants;
import com.epam.nazar.grinko.domians.User;
import com.epam.nazar.grinko.dto.UserDto;
import com.epam.nazar.grinko.exceptions.IllegalJwtContentException;
import com.epam.nazar.grinko.securities.jwt.JwtTokenProvider;
import com.epam.nazar.grinko.services.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("car-rental-service")
@Slf4j
@AllArgsConstructor
public class CommonController {

    private final UserService userService;
    private final JwtTokenProvider tokenProvider;

    @GetMapping
    public String showStartPage(){
        return "index";
    }

    @GetMapping("/about-us")
    public String showAboutUsPage(){
        return "about-us";
    }

    @GetMapping("/{role}/profile")
    public String showProfilePage(HttpServletRequest request, Model model,
                                  @PathVariable("role") String role){
        String username = tokenProvider.getUsername(tokenProvider.resolveToken(request));
        Optional<User> user = userService.getByEmail(username);
        UserDto userDto = userService.mapToDto(user.orElseThrow(IllegalJwtContentException::new));

        model.addAttribute("user", userDto);
        model.addAttribute("role", role);

        return "common/profile";
    }

    @GetMapping("/{role}/profile/edit")
    public String showEditProfilePage(HttpServletRequest request, Model model,
                                      @PathVariable("role") String role){
        String username = tokenProvider.getUsername(tokenProvider.resolveToken(request));
        User user = userService.getByEmail(username).orElseThrow(IllegalJwtContentException::new);

        UserDto userDto = userService.mapToDto(user);

        model.addAttribute("user", userDto);
        model.addAttribute("oldEmail", user.getEmail());
        model.addAttribute("oldPassword", user.getPassword());
        model.addAttribute("role", role);

        return "common/edit-profile";
    }

    @PatchMapping("/{role}/profile/edit")
    public String saveUserChanges(@Valid @ModelAttribute("user") UserDto userDto, BindingResult bindingResult,
                                  @ModelAttribute("oldEmail") String oldEmail,
                                  @PathVariable("role") String role, Model model){
        if(bindingResult.hasErrors() && !checkOldPassword(bindingResult, userDto, oldEmail)) {
            log.info("PROFILE-EDIT FAILURE: invalid data.");
            return "common/edit-profile";
        }

        User oldEmailUser = userService.getByEmail(oldEmail).orElseThrow(IllegalJwtContentException::new);
        User currEmailUser = userService.getByEmail(userDto.getEmail()).orElse(null);

        if(currEmailUser != null && !oldEmailUser.getId().equals(currEmailUser.getId())){
            log.info("PROFILE-EDIT FAILURE: oldEmail={}, currEmail={}, oldEmailUser.id={}, currEmailUser.id={})",
                    oldEmailUser.getEmail(), currEmailUser.getEmail(), oldEmailUser.getId(), currEmailUser.getId());
            model.addAttribute(ViewExceptionsConstants.USER_ALREADY_EXISTS_EXCEPTION, true);
            model.addAttribute("user", userDto);

            return "common/edit-profile";
        }

        if(!userDto.getPassword().equals(oldEmailUser.getPassword()))
            userDto.setPassword(userService.encodePassword(userDto.getPassword()));
        userService.updateUserById(
                userService.mapToObject(userDto), oldEmailUser.getId()
        );
        log.info("PROFILE-EDIT SUCCESS: oldData={}, newData={}", userService.mapToDto(oldEmailUser), userDto);

        String url = "/car-rental-service/" + role + "/profile/edit";
        return "redirect:" + url;
    }

    @ModelAttribute
    public void logoutBefore(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
        securityContextLogoutHandler.logout(request, response, null);
        tokenProvider.removeCookieToken(response);
    }

    private boolean checkOldPassword(BindingResult bindingResult, UserDto userDto, String oldEmail) {
        if(bindingResult.getErrorCount() != 1 || !bindingResult.hasFieldErrors("password")) return false;
        String oldEncrypted = userService.getByEmail(oldEmail)
                .orElseThrow(IllegalArgumentException::new).getPassword();
        return userDto.getPassword().equals(oldEncrypted);
    }
}
