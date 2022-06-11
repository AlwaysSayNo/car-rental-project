package com.epam.nazar.grinko.controllers;

import com.epam.nazar.grinko.domians.User;
import com.epam.nazar.grinko.dto.AuthenticationRequestDto;
import com.epam.nazar.grinko.dto.UserDto;
import com.epam.nazar.grinko.securities.jwt.JwtTokenProvider;
import com.epam.nazar.grinko.services.AuthenticationService;
import com.epam.nazar.grinko.services.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Controller
@RequestMapping("car-rental-service")
@Slf4j
@AllArgsConstructor
public class AuthenticationController {

    AuthenticationService authenticationService;
    UserService userService;
    JwtTokenProvider jwtTokenProvider;

    @GetMapping("/sign-in")
    public String showSignInPage(Model model){
        model.addAttribute("requestDto", new AuthenticationRequestDto());
        return "sign-in";
    }

    @PostMapping("/sign-in")
    public String authenticate(@ModelAttribute("requestDto") AuthenticationRequestDto requestDto,
                                          HttpServletResponse response, Model model) {
        try {
            User user = authenticationService.authenticateUser(requestDto);
            jwtTokenProvider.setCookieToken(response, user);

            String role = user.getRole().name().toLowerCase(Locale.ROOT).replace("role_", "");
            String url = "/car-rental-service/" + role + "/profile";

            log.info("SIGN-IN SUCCESS: ({}, {}, {})", user.getEmail(), user.getFirstName(), user.getLastName());

            return "redirect:" + url;
        } catch (AuthenticationException e) {
            model.addAttribute("invalidCredentialsException", true);

            log.info("SIGN-IN FAILURE: invalid credentials ({}, {})", requestDto.getEmail(), requestDto.getPassword());

            return "sign-in";
        }
    }

    @GetMapping("/sign-up")
    public String showSignUpPage(Model model){
        model.addAttribute("userDto", new UserDto());
        return "sign-up";
    }

    @PostMapping("/sign-up")
    public String register(@ModelAttribute("userDto") UserDto userDto,
                           HttpServletResponse response, Model model) {
        if(userService.existsUserByEmail(userDto.getEmail())){
            model.addAttribute("userAlreadyExistsError", true);
            model.addAttribute("userDto", new UserDto());

            log.info("SIGN-UP FAILURE: user already exists ({}, {})", userDto.getEmail(), userDto.getPassword());

            return "sign-up";
        }

        userService.setBasicMetaParameters(userDto);
        User user = userService.convertUserDtoToUser(userDto);
        userService.addNewUser(user);

        jwtTokenProvider.setCookieToken(response, user);
        String role = user.getRole().name().toLowerCase(Locale.ROOT).replace("role_", "");
        String url = "/car-rental-service/" + role + "/profile";

        log.info("SIGN-UP SUCCESS: ({}, {}, {})", user.getEmail(), user.getFirstName(), user.getLastName());

        return "redirect:" + url;
    }

    @PostMapping("/sign-out")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
        securityContextLogoutHandler.logout(request, response, null);

        jwtTokenProvider.removeCookieToken(response);
        log.info("SIGN-OUT SUCCESS");

        return "redirect:/car-rental-service/sign-in";
    }

}
