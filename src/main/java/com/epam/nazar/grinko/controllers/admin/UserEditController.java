package com.epam.nazar.grinko.controllers.admin;

import com.epam.nazar.grinko.domians.User;
import com.epam.nazar.grinko.domians.helpers.UserRole;
import com.epam.nazar.grinko.domians.helpers.UserStatus;
import com.epam.nazar.grinko.exceptions.IllegalPathVariableException;
import com.epam.nazar.grinko.services.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("car-rental-service/admin/registered-users/{id}/edit")
@AllArgsConstructor
@Slf4j
public class UserEditController {

    private final UserService userService;

    @PostMapping("/delete")
    public String deleteUser(@PathVariable("id") Long userId){
        userService.deleteById(userId);
        log.info("USER-DELETE SUCCESS: managerId={}", userId);
        return "redirect:/car-rental-service/admin/registered-users";
    }

    @PostMapping("change-status")
    public String changeUserStatus(@PathVariable("id") Long userId, @PathParam("newStatus") String newStatus){
        UserStatus status = UserStatus.valueOf(newStatus);
        User user = userService.getById(userId);

        if(user.getStatus().equals(status) || !getAvailableStatuses().contains(newStatus)) {
            log.info("REGISTERED-EDIT-STATUS FAILURE: userId={}, oldStatus={}, newStatus={}",
                    userId, user.getStatus().name(), newStatus);
            return "redirect:/car-rental-service/admin/registered-users/";
        }

        userService.updateUserStatusById(UserStatus.valueOf(newStatus), userId);
        log.info("REGISTERED-EDIT-STATUS SUCCESS: userId={}, oldStatus={}, newStatus={}",
                userId, user.getStatus().name(), status.name());

        return "redirect:/car-rental-service/admin/registered-users";
    }


    @ModelAttribute
    public void checkRequestValidity(@PathVariable("id") Long userId) {
        User user = userService.getById(userId);

        if(!user.getRole().equals(UserRole.ROLE_USER))
            throw new IllegalPathVariableException("User with id " + userId + " is not user");
    }

    private List<String> getAvailableStatuses(){
        return Arrays.stream(new UserStatus[]{UserStatus.ACTIVE, UserStatus.BANNED})
                .map(UserStatus::name)
                .collect(Collectors.toList());
    }

}
