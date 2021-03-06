package com.epam.nazar.grinko.controllers.admin;


import com.epam.nazar.grinko.domians.User;
import com.epam.nazar.grinko.domians.helpers.UserRole;
import com.epam.nazar.grinko.domians.helpers.UserStatus;
import com.epam.nazar.grinko.dto.UserDto;
import com.epam.nazar.grinko.engines.FilterPresetEngine;
import com.epam.nazar.grinko.engines.PaginationPresetEngine;
import com.epam.nazar.grinko.engines.SortPresetEngine;
import com.epam.nazar.grinko.services.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("car-rental-service/admin/registered-users")
@AllArgsConstructor
@Slf4j
public class AllUsersController {

    private final UserService userService;

    @GetMapping()
    public String showAllRegistered(@RequestParam(value = "sortBy", required = false) String sortBy,
                                    @RequestParam(value = "direction", required = false) String direction,
                                    @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                    @RequestParam(value = "size", required = false, defaultValue = "8") Integer size,
                                    @RequestParam(value = "filterBy", required = false) String filterBy,
                                    @RequestParam(value = "filterValue", required = false) String filterValue, Model model){
        log.info("ADMIN showAllRegistered: sortBy={}, direction={}, filterBy={}, filterValue={}, page={}, size={}",
                sortBy, direction, filterBy, filterValue, page, size);

        PageRequest pageRequest = userService.getManipulationService().createRequest(page - 1, size, sortBy, direction);
        Page<User> users = userService.getUsersByRole(pageRequest, UserRole.ROLE_USER, filterBy, filterValue);

        Page<UserDto> usersDto = users.map(userService::mapToDto);
        List<Long> allId = users.stream().map(User::getId).collect(Collectors.toList());

        model.addAttribute("users", usersDto);
        model.addAttribute("ids", allId);

        PaginationPresetEngine.updateModelForPagination(model, usersDto, page, size);
        SortPresetEngine.updateModelForSorting(model, sortBy, direction);
        FilterPresetEngine.updateModelForFiltering(model, filterBy, filterValue);

        return "admin/users/all-users";
    }

    @ModelAttribute("statuses")
    public List<String> getStatuses(){
        return getAvailableStatuses();
    }

    @ModelAttribute("filtersMap")
    public Map<String, List<String>> getFiltersMap(){
        Map<String, List<String>> filtersMap = new HashMap<>();

        List<String> statuses = getAvailableStatuses();
        filtersMap.put("status", statuses);

        return filtersMap;
    }

    @ModelAttribute("sortsMap")
    public Map<String, List<String>> getSortsMap(){
        Map<String, List<String>> sortsMap = new HashMap<>();
        List<String> directions = Arrays.asList("ASC", "DESC");

        sortsMap.put("id", directions);
        sortsMap.put("email", directions);
        sortsMap.put("firstName", directions);
        sortsMap.put("lastName", directions);

        return sortsMap;
    }

    private List<String> getAvailableStatuses(){
        return Arrays.stream(UserStatus.values())
                .map(UserStatus::name)
                .collect(Collectors.toList());
    }

}
