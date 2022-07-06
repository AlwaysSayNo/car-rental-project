package com.epam.nazar.grinko.controllers.admin;

import com.epam.nazar.grinko.domians.Car;
import com.epam.nazar.grinko.domians.User;
import com.epam.nazar.grinko.domians.helpers.CarStatus;
import com.epam.nazar.grinko.domians.helpers.UserRole;
import com.epam.nazar.grinko.domians.helpers.UserStatus;
import com.epam.nazar.grinko.exceptions.IllegalPathVariableException;
import com.epam.nazar.grinko.services.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

@Controller
@RequestMapping("car-rental-service/admin/managers/{id}/edit")
@AllArgsConstructor
@Slf4j
public class EditManagerController {

    private final UserService userService;

    @PostMapping("/delete")
    public String deleteManager(@PathVariable("id") Long managerId){
        userService.deleteById(managerId);
        log.info("MANAGER-DELETE SUCCESS: managerId={}", managerId);
        return "redirect:/car-rental-service/admin/managers";
    }

    @PostMapping("/change-status")
    public String changeManagerStatus(@PathVariable("id") Long managerId,
                                      @RequestParam("newStatus") String newStatus){
        UserStatus status = UserStatus.valueOf(newStatus);
        User manager = userService.getById(managerId);

        if(manager.getStatus().equals(status)) {
            log.info("MANAGER-EDIT-STATUS FAILURE: managerId={}, oldStatus={}, newStatus={}",
                    managerId, manager.getStatus().name(), newStatus);
            return "redirect:/car-rental-service/admin/cars/" + managerId;
        }

        userService.updateUserStatusById(status, managerId);
        log.info("MANAGER-EDIT-STATUS SUCCESS: managerId={}, oldStatus={}, newStatus={}",
                managerId, manager.getStatus().name(), status.name());

        return "redirect:/car-rental-service/admin/managers";
    }


    @ModelAttribute
    public void checkRequestValidity(@PathVariable("id") Long managerId) {
        User user = userService.getById(managerId);

        if(!user.getRole().equals(UserRole.ROLE_MANAGER))
            throw new IllegalPathVariableException("User with id " + managerId + " is not manager");
    }

}
