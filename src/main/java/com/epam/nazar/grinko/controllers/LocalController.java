package com.epam.nazar.grinko.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/car-rental-service")
public class LocalController {

    @GetMapping("/international")
    public String changeLocal(HttpServletRequest request){
        String url = request.getHeader("Referer");
        return "redirect:" + url;
    }

}
