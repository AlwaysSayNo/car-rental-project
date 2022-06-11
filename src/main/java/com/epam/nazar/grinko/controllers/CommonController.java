package com.epam.nazar.grinko.controllers;

import com.epam.nazar.grinko.domians.User;
import com.epam.nazar.grinko.dto.UserDto;
import com.epam.nazar.grinko.securities.jwt.JwtTokenProvider;
import com.epam.nazar.grinko.services.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    public String showProfilePage(Model model, @PathVariable String role){
        model.addAttribute("userDto", new UserDto());
        model.addAttribute("role", role);
        return "common/profile";
    }

    @GetMapping("/{role}/profile/edit")
    public String showEditProfilePage(HttpServletRequest request, Model model,
                                      @PathVariable String role){
        String username = tokenProvider.getUsername(tokenProvider.resolveToken(request));
        User user = userService.getUserByEmail(username).get();
        UserDto userDto = userService.convertUserToUserDto(user);

        model.addAttribute("userDto", userDto);
        model.addAttribute("oldEmail", user.getEmail());

        return "common/edit-profile";
    }

    // user.get
    @PatchMapping("/{role}/profile/edit")
    public String saveUserChanges(@ModelAttribute UserDto userDto, @ModelAttribute("oldEmail") String oldEmail,
                                  @PathVariable String role, Model model){
        User oldUser = userService.getUserByEmail(oldEmail).get();
        Optional<User> tmpUser = userService.getUserByEmail(userDto.getEmail());

        if(tmpUser.isPresent() && !tmpUser.get().getId().equals(oldUser.getId())){
            model.addAttribute("userAlreadyExistsError", true);
            model.addAttribute("userDto", userDto);

            log.info("PROFILE-EDIT FAILURE: user already exists ({}, {})", userDto.getEmail(), userDto.getPassword());

            return "common/edit-profile";
        }

        userService.updateUserById(userService.convertUserDtoToUser(userDto), oldUser.getId());

        log.info("PROFILE-EDIT: ({}, {}, {})", userDto.getEmail(), userDto.getFirstName(), userDto.getLastName());

        String url = "/car-rental-service/" + role + "/profile/edit";
        return "redirect:" + url;
    }


}
