package com.epam.nazar.grinko.controllers.admin;

import com.epam.nazar.grinko.domians.User;
import com.epam.nazar.grinko.domians.helpers.UserRole;
import com.epam.nazar.grinko.domians.helpers.UserStatus;
import com.epam.nazar.grinko.exceptions.IllegalPathVariableException;
import com.epam.nazar.grinko.services.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

@Controller
@RequestMapping("car-rental-service/admin/managers/managers/{id}/edit")
@AllArgsConstructor
public class EditManagerController {

    private final UserService userService;

    @DeleteMapping()
    public String deleteManager(@PathVariable("id") Long managerId){
        userService.deleteById(managerId);
        return "redirect:/car-rental-service/admin/managers";
    }

    @PatchMapping()
    public String changeManagerStatus(@PathVariable("id") Long managerId, @PathParam("status") String status){
        userService.updateUserStatusById(UserStatus.valueOf(status), managerId);
        String url = "/car-rental-service/admin/managers/" + managerId;
        return "redirect:" + url;
    }


    @ModelAttribute
    public void checkRequestValidity(@PathVariable("id") Long managerId) {
        User user = userService.getById(managerId);

        if(!user.getRole().equals(UserRole.ROLE_MANAGER))
            throw new IllegalPathVariableException("User with id " + managerId + " is not manager");
    }

}
