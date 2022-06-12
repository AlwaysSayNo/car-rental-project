package com.epam.nazar.grinko.controllers;

import com.epam.nazar.grinko.domians.User;
import com.epam.nazar.grinko.dto.UserDto;
import com.epam.nazar.grinko.securities.jwt.IllegalJwtContentException;
import com.epam.nazar.grinko.securities.jwt.JwtTokenProvider;
import com.epam.nazar.grinko.services.UserService;
import com.epam.nazar.grinko.utils.Utility;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
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
                                  @PathVariable String role){
        String username = tokenProvider.getUsername(tokenProvider.resolveToken(request));
        Optional<User> user = userService.getUserByEmail(username);
        UserDto userDto = userService.convertUserToUserDto(user.orElseThrow(IllegalJwtContentException::new));

        model.addAttribute("userDto", userDto);
        model.addAttribute("role", role);

        return "common/profile";
    }

    @GetMapping("/{role}/profile/edit")
    public String showEditProfilePage(HttpServletRequest request, Model model,
                                      @PathVariable String role){
        String username = tokenProvider.getUsername(tokenProvider.resolveToken(request));
        User user = userService.getUserByEmail(username).orElseThrow(IllegalJwtContentException::new);

        UserDto userDto = userService.convertUserToUserDto(user);

        model.addAttribute("userDto", userDto);
        model.addAttribute("oldEmail", user.getEmail());
        model.addAttribute("role", role);

        return "common/edit-profile";
    }

    @PatchMapping("/{role}/profile/edit")
    public String saveUserChanges(@ModelAttribute UserDto userDto, @ModelAttribute("oldEmail") String oldEmail,
                                  @PathVariable String role, Model model){
        User oldEmailUser = userService.getUserByEmail(oldEmail).orElseThrow(IllegalJwtContentException::new);
        User currEmailUser = userService.getUserByEmail(userDto.getEmail()).orElseThrow(IllegalJwtContentException::new);

        if(!oldEmailUser.getId().equals(currEmailUser.getId())){
            model.addAttribute("userAlreadyExistsError", true);
            model.addAttribute("userDto", userDto);

            //log.info("PROFILE-EDIT FAILURE: user already exists ({}, {})", userDto.getEmail(), userDto.getPassword());

            return "common/edit-profile";
        }

        userService.updateUserById(
                userService.convertUserDtoToUser(userDto), oldEmailUser.getId()
        );

        //log.info("PROFILE-EDIT: ({}, {}, {})", userDto.getEmail(), userDto.getFirstName(), userDto.getLastName());

        String url = "/car-rental-service/" + role + "/profile/edit";
        return "redirect:" + url;
    }


}
