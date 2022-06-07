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
    public ResponseEntity<?> authenticate(@ModelAttribute("requestDto") AuthenticationRequestDto requestDto) {
        Map<Object, Object> response = new HashMap<>();
        try {
            User user = authenticationService.authenticateUser(requestDto);
            String token = jwtTokenProvider.createToken(requestDto.getEmail(), user.getRole().name());
            response.put("email", requestDto.getEmail());
            response.put("token", token);
            System.out.println("Token: " + token);
            return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                    .location(URI.create("/car-rental-service/" + user.getRole().name().toLowerCase(Locale.ROOT) + "/profile"))
                    .body(response);
            //return ResponseEntity.ok().body(response);
        } catch (AuthenticationException e) {
            response.put("invalidCredentialsException", true);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .location(URI.create("/car-rental-service/sign-in"))
                    .body(response);
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
