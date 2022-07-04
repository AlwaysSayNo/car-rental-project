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
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/car-rental-service")
@Slf4j
@AllArgsConstructor
public class CommonController {

    private final JwtTokenProvider tokenProvider;

    @GetMapping
    public String showStartPage(){
        return "index";
    }

    @GetMapping("/about-us")
    public String showAboutUsPage(){
        return "about-us";
    }

    @ModelAttribute
    public void logoutBefore(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
        securityContextLogoutHandler.logout(request, response, null);
        tokenProvider.removeCookieToken(response);
    }

}
