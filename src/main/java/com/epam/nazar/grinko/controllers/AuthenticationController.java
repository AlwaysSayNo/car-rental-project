package com.epam.nazar.grinko.controllers;

import com.epam.nazar.grinko.domians.User;
import com.epam.nazar.grinko.dto.AuthenticationRequestDto;
import com.epam.nazar.grinko.dto.UserDto;
import com.epam.nazar.grinko.securities.jwt.JwtTokenProvider;
import com.epam.nazar.grinko.services.AuthenticationService;
import com.epam.nazar.grinko.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("car-rental-service")
public class AuthenticationController {

    AuthenticationService authenticationService;
    UserService userService;
    JwtTokenProvider jwtTokenProvider;

    public AuthenticationController(AuthenticationService authenticationService, JwtTokenProvider jwtTokenProvider) {
        this.authenticationService = authenticationService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/sign-in")
    public String showSignInPage(Model model){
        model.addAttribute("requestDto", new AuthenticationRequestDto());
        return "sign-in";
    }

    @PostMapping("/sign-in")
    public String authenticate(@ModelAttribute("requestDto") AuthenticationRequestDto requestDto,
                                          HttpServletResponse response,
                               Model model) {
        try {
            User user = authenticationService.authenticateUser(requestDto);
            String token = jwtTokenProvider.createToken(requestDto.getEmail(), user.getRole().name());
            response.addCookie(jwtTokenProvider.createCookie(token));
            String role = user.getRole().name().toLowerCase(Locale.ROOT)
                    .replace("role_", "");
            String url = "/car-rental-service/" + role + "/profile";
            return "redirect:" + url;
        } catch (AuthenticationException e) {
            model.addAttribute("invalidCredentialsException", true);
            return "sign-in";
        }
    }

    @GetMapping("/sign-up")
    public String showSignUpPage(Model model){
        model.addAttribute("user", new User());
        return "sign-up";
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> register(@ModelAttribute UserDto userDto, Model model) {
        if(userService.existsUserByEmail(userDto.getEmail())){
            model.addAttribute("userAlreadyExistsError", true);
            model.addAttribute("user", new User());
            return null;
        }
        return null;
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
        securityContextLogoutHandler.logout(request, response, null);
    }

}
