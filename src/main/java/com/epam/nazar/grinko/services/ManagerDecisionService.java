package com.epam.nazar.grinko.services;

import com.epam.nazar.grinko.domians.ManagerDecision;
import com.epam.nazar.grinko.domians.Order;
import com.epam.nazar.grinko.domians.User;
import com.epam.nazar.grinko.dto.ManagerDecisionDto;
import com.epam.nazar.grinko.dto.OrderDto;
import com.epam.nazar.grinko.dto.UserDto;
import com.epam.nazar.grinko.repositories.ManagerDecisionRepository;
import com.epam.nazar.grinko.services.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ManagerDecisionService {

    private final ManagerDecisionRepository decisionRepository;
    private final UserService userService;
    private final OrderService orderService;

    public void save(ManagerDecision decision){
        decisionRepository.save(decision);
    }

    public ManagerDecision getById(Long decisionId) {
        return decisionRepository.getById(decisionId);
    }

    public List<ManagerDecision> getAll(){
        return decisionRepository.findAll();
    }

    public List<ManagerDecision> getAllByManagerId(Long managerId){
        return decisionRepository.findDistinctByManagerId(managerId);
    }

    public List<ManagerDecision> getDecisionsByOrderId(Long orderId){
        return decisionRepository.findDistinctByOrderId(orderId);
    }

    public ManagerDecision mapToObject(ManagerDecisionDto decisionDto){
        User manager = userService.mapToObject(decisionDto.getManager());
        Order order = orderService.mapToObject(decisionDto.getOrder());

        return new ManagerDecision().setManager(manager)
                .setOrder(order);
    }

    public ManagerDecisionDto mapToDto(ManagerDecision decision){
        UserDto managerDto = userService.mapToDto(decision.getManager());
        OrderDto orderDto = orderService.mapToDto(decision.getOrder());

        return new ManagerDecisionDto().setManager(managerDto)
                .setOrder(orderDto);
    }

}
