package com.epam.nazar.grinko.controllers;

import com.epam.nazar.grinko.dto.UserDto;
import com.epam.nazar.grinko.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@AllArgsConstructor
@RequestMapping("car-rental-service")
public class CommonController {

    private UserService userService;

    @GetMapping
    public String showStartPage(){
        return "index";
    }

    @GetMapping("/about-us")
    public String showAboutUsPage(){
        return "common/about-us";
    }

    @GetMapping("/{role}/profile")
    public String showStartPage(Model model, @PathVariable String role){
        model.addAttribute("userDto", new UserDto());
        model.addAttribute("role", role);
        return "profile";
    }



}
