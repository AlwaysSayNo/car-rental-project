package com.epam.nazar.grinko.services;

import com.epam.nazar.grinko.domians.Breakdown;
import com.epam.nazar.grinko.dto.BreakdownDto;
import com.epam.nazar.grinko.repositories.BreakdownRepository;
import com.epam.nazar.grinko.repositories.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class BreakdownService {

    private final BreakdownRepository breakdownRepository;
    private final OrderService orderService;

    public Optional<Breakdown> getByOrderId(Long orderId){
        return breakdownRepository.findDistinctByOrderId(orderId);
    }

    public BreakdownDto convertBreakdownToBreakdownDto(Breakdown breakdown){
        return new BreakdownDto().setPrice(breakdown.getPrice())
                .setMessage(breakdown.getMessage())
                .setStatus(breakdown.getStatus())
                .setOrder(orderService.convertOrderToOrderDto(breakdown.getOrder()));
    }

}
