package com.epam.nazar.grinko.controllers;

import com.epam.nazar.grinko.constants.ViewExceptionsConstants;
import com.epam.nazar.grinko.domians.User;
import com.epam.nazar.grinko.domians.helpers.UserStatus;
import com.epam.nazar.grinko.dto.AuthenticationRequestDto;
import com.epam.nazar.grinko.dto.UserDto;
import com.epam.nazar.grinko.domians.helpers.UserRole;
import com.epam.nazar.grinko.securities.jwt.JwtTokenProvider;
import com.epam.nazar.grinko.services.AuthenticationService;
import com.epam.nazar.grinko.services.user.UserService;
import com.epam.nazar.grinko.utils.Utility;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Locale;

@Controller
@RequestMapping("/car-rental-service")
@Slf4j
@AllArgsConstructor
public class AuthenticationController {

    AuthenticationService authenticationService;
    UserService userService;
    JwtTokenProvider jwtTokenProvider;

    @GetMapping("/sign-in")
    public String showSignInPage(Model model){
        model.addAttribute("authRequest", new AuthenticationRequestDto());
        return "sign-in";
    }

    @PostMapping("/sign-in")
    public String authenticate(@Valid @ModelAttribute("authRequest") AuthenticationRequestDto requestDto,
                               BindingResult bindingResult,
                               HttpServletResponse response, Model model) {
        if(bindingResult.hasErrors()) return "sign-in";
        try {
            User user = authenticationService.authenticateUser(requestDto);
            jwtTokenProvider.setCookieToken(response, user);

            String role = Utility.getRole(user.getRole().name());
            String url = "/car-rental-service/" + role + "/profile";

            return "redirect:" + url;
        } catch (AuthenticationException e) {
            model.addAttribute(ViewExceptionsConstants.INVALID_CREDENTIALS_EXCEPTION, true);

            return "sign-in";
        }
    }

    @GetMapping("/sign-up")
    public String showSignUpPage(Model model){
        model.addAttribute("user", new UserDto());
        return "sign-up";
    }

    @PostMapping("/sign-up")
    public String register(@Valid @ModelAttribute("user") UserDto userDto,
                           BindingResult bindingResult,
                           HttpServletResponse response, Model model) {
        if(bindingResult.hasErrors()) return "sign-up";
        if(userService.existsByEmail(userDto.getEmail())){
            model.addAttribute(ViewExceptionsConstants.USER_ALREADY_EXISTS_EXCEPTION, true);
            model.addAttribute("user", new UserDto());

            return "sign-up";
        }

        userDto.setRole(UserRole.ROLE_USER).setStatus(UserStatus.ACTIVE);
        User user = userService.mapToObject(userDto);
        userService.save(user);

        jwtTokenProvider.setCookieToken(response, user);
        String role = user.getRole().name().toLowerCase(Locale.ROOT).replace("role_", "");
        String url = "/car-rental-service/" + role + "/profile";

        return "redirect:" + url;
    }

    @PostMapping("/sign-out")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
        securityContextLogoutHandler.logout(request, response, null);
        jwtTokenProvider.removeCookieToken(response);

        String url = "/car-rental-service/sign-in";

        return "redirect:" + url;
    }

}
