package com.epam.nazar.grinko.services;

import com.epam.nazar.grinko.domians.Breakdown;
import com.epam.nazar.grinko.domians.Order;
import com.epam.nazar.grinko.domians.PaymentDetails;
import com.epam.nazar.grinko.domians.helpers.BreakdownStatus;
import com.epam.nazar.grinko.domians.helpers.OrderStatus;
import com.epam.nazar.grinko.domians.helpers.UserStatus;
import com.epam.nazar.grinko.dto.BreakdownDto;
import com.epam.nazar.grinko.dto.PaymentDetailsDto;
import com.epam.nazar.grinko.repositories.BreakdownRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class BreakdownService {

    private final BreakdownRepository breakdownRepository;
    private final OrderService orderService;
    private final UserService userService;
    private final PaymentDetailsService paymentDetailsService;

    public Optional<Breakdown> getByOrderId(Long orderId){
        return breakdownRepository.findDistinctByOrderId(orderId);
    }

    public BreakdownDto mapToDto(Breakdown breakdown){
        return new BreakdownDto().setPrice(breakdown.getPrice())
                .setMessage(breakdown.getMessage())
                .setStatus(breakdown.getStatus())
                .setOrder(orderService.mapToDto(breakdown.getOrder()));
    }

    public void save(Breakdown breakdown){
        breakdownRepository.save(breakdown);
    }

    public void updateBreakdownStatus(BreakdownStatus status, Long id){
        breakdownRepository.updateBreakdownStatusById(status, id);
    }

    public void updateBreakdownStatus(BreakdownStatus status, PaymentDetails paymentDetails, Long id){
        breakdownRepository.updateBreakdownStatusAndPaymentDetailsById(status, paymentDetails, id);
    }

    public void payFine(Order order, PaymentDetailsDto paymentDetailsDto){
        Breakdown breakdown = order.getBreakdown();

        PaymentDetails paymentDetails = paymentDetailsService.mapToObject(paymentDetailsDto);
        paymentDetailsService.saveIfNotExists(paymentDetails);

        breakdown.setPaymentDetails(paymentDetails);
        updateBreakdownStatus(BreakdownStatus.PAID, paymentDetails, breakdown.getId());

        orderService.updateOrderStatus(OrderStatus.ENDED_WITH_BREAKDOWN, order.getId());

        userService.updateUserStatusById(UserStatus.ACTIVE, order.getUser().getId());
    }

}
