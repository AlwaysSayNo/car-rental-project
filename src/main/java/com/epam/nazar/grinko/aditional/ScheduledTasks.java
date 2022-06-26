package com.epam.nazar.grinko.aditional;

import com.epam.nazar.grinko.domians.Order;
import com.epam.nazar.grinko.services.CancellationService;
import com.epam.nazar.grinko.services.order.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;

@Component
@PropertySource("classpath:/META-INF/my-application.properties")
@AllArgsConstructor
@Slf4j
public class ScheduledTasks {

    private final OrderService orderService;
    private final CancellationService cancellationService;

    @Scheduled(cron = "1 0 * * ?")
    public void endExpiredOrders(){
        List<Order> orders = orderService.endExpiredSuccessfully(Calendar.getInstance());
        log.info("Affected rows by ending expired orders: {}", orders.size());
    }

    //?
    @Scheduled(cron = "1 0 * * ?")
    public void cancelUnverifiedOrders(){
        List<Order> orders = cancellationService.cancelUnverifiedOrders(Calendar.getInstance());

        log.info("Affected rows by canceling expired orders: {}", orders.size());
    }

}
