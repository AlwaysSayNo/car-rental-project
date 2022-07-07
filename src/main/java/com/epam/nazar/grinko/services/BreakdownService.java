package com.epam.nazar.grinko.services;

import com.epam.nazar.grinko.domians.Breakdown;
import com.epam.nazar.grinko.domians.Order;
import com.epam.nazar.grinko.domians.helpers.BreakdownStatus;
import com.epam.nazar.grinko.domians.helpers.OrderStatus;
import com.epam.nazar.grinko.domians.helpers.UserStatus;
import com.epam.nazar.grinko.dto.BreakdownDto;
import com.epam.nazar.grinko.repositories.BreakdownRepository;
import com.epam.nazar.grinko.services.order.OrderService;
import com.epam.nazar.grinko.services.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class BreakdownService {

    private final BreakdownRepository breakdownRepository;
    private final OrderService orderService;
    private final UserService userService;

    public Optional<Breakdown> getByOrderId(Long orderId){
        return breakdownRepository.findDistinctByOrderId(orderId);
    }

    public BreakdownDto mapToDto(Breakdown breakdown){
        return new BreakdownDto().setPrice(breakdown.getPrice())
                .setMessage(breakdown.getMessage())
                .setStatus(breakdown.getStatus())
                .setOrder(orderService.mapToDto(breakdown.getOrder()));
    }

    public Breakdown mapToObject(BreakdownDto breakdownDto){

        return new Breakdown().setPrice(breakdownDto.getPrice())
                .setMessage(breakdownDto.getMessage())
                .setStatus(breakdownDto.getStatus())
                .setOrder(orderService.mapToObject(breakdownDto.getOrder()));
}

    public void save(Breakdown breakdown){
        breakdownRepository.save(breakdown);
    }

    public void updateBreakdownStatus(BreakdownStatus status, Long id){
        breakdownRepository.updateBreakdownStatusById(status, id);
    }

    public void payFine(Order order){
        Breakdown breakdown = order.getBreakdown();

        updateBreakdownStatus(BreakdownStatus.PAID, breakdown.getId());

        orderService.updateOrderStatus(OrderStatus.ENDED_WITH_BREAKDOWN, order.getId());

        userService.updateUserStatusById(UserStatus.ACTIVE, order.getUser().getId());
    }

}
