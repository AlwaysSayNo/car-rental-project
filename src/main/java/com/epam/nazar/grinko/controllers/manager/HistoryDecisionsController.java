package com.epam.nazar.grinko.controllers.manager;

import com.epam.nazar.grinko.domians.ManagerDecision;
import com.epam.nazar.grinko.dto.ManagerDecisionDto;
import com.epam.nazar.grinko.exceptions.IllegalPathVariableException;
import com.epam.nazar.grinko.exceptions.JwtAuthenticationException;
import com.epam.nazar.grinko.securities.jwt.JwtTokenProvider;
import com.epam.nazar.grinko.services.ManagerDecisionService;
import com.epam.nazar.grinko.services.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("car-rental-service/manager/orders-history/{id}")
@AllArgsConstructor
public class HistoryDecisionsController {

    private final UserService userService;
    private final ManagerDecisionService decisionService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping()
    public String showActiveOrderPage(@PathVariable("id") Long decisionId, Model model){
        ManagerDecisionDto decisionDto = decisionService.mapToDto(decisionService.getById(decisionId));

        model.addAttribute("decision", decisionDto);

        return "manager/decision-history/show-decision-history";
    }


    @ModelAttribute
    private void checkRequestValidity(@PathVariable("id") Long decisionId, HttpServletRequest request){
        ManagerDecision decision = decisionService.getById(decisionId);

        String email = jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(request));
        Long managerId = userService.getUserIdByEmail(email).orElseThrow(JwtAuthenticationException::new);

        if(!decision.getManager().getId().equals(managerId))
            throw new IllegalPathVariableException();
    }

}
