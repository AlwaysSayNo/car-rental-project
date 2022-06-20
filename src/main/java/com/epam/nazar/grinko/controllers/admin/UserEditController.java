package com.epam.nazar.grinko.controllers.admin;

import com.epam.nazar.grinko.domians.User;
import com.epam.nazar.grinko.domians.helpers.UserRole;
import com.epam.nazar.grinko.domians.helpers.UserStatus;
import com.epam.nazar.grinko.exceptions.IllegalPathVariableException;
import com.epam.nazar.grinko.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

@Controller
@RequestMapping("car-rental-service/admin/registered-users/{id}")
@AllArgsConstructor
public class UserEditController {

    private final UserService userService;

    @DeleteMapping()
    public String deleteUser(@PathVariable("id") Long userId){
        userService.deleteById(userId);
        return "redirect:/car-rental-service/admin/registered-users";
    }

    @PatchMapping()
    public String changeUserStatus(@PathVariable("id") Long userId, @PathParam("status") String status){
        userService.updateUserStatusById(UserStatus.valueOf(status), userId);
        return "redirect:/car-rental-service/admin/registered-users";
    }


    @ModelAttribute
    public void checkRequestValidity(@PathVariable("id") Long userId) {
        User user = userService.getById(userId);

        if(!user.getRole().equals(UserRole.ROLE_USER))
            throw new IllegalPathVariableException("User with id " + userId + " is not user");
    }

}
