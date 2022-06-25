package com.epam.nazar.grinko.services;

import com.epam.nazar.grinko.constants.BillConstants;
import com.epam.nazar.grinko.domians.Bill;
import com.epam.nazar.grinko.domians.helpers.CarSegment;
import com.epam.nazar.grinko.dto.BillDto;
import com.epam.nazar.grinko.dto.CarDto;
import com.epam.nazar.grinko.repositories.BillRepository;
import com.epam.nazar.grinko.services.order.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;

@Service
@ComponentScan("com.epam.nazar.grinko")
@AllArgsConstructor
public class BillService {

    private final BillRepository billRepository;
    private final OrderService orderService;
    private final PaymentDetailsService paymentDetailsService;
    private final BillConstants constants;

    public void addBill(Bill bill){
        orderService.save(bill.getOrder());
        billRepository.save(bill);
    }

    public Optional<Bill> getByOrderId(Long orderId){
        return billRepository.findDistinctByOrderId(orderId);
    }

    public Bill mapToObject(BillDto billDto){
        return new Bill().setOrder(orderService.mapToObject(billDto.getOrder()))
                .setPaymentDetails(paymentDetailsService.mapToObject(billDto.getPaymentDetails()))
                .setStartDate(billDto.getStartDate())
                .setExpirationDate(billDto.getExpirationDate())
                .setCarPrice(billDto.getCarPrice())
                .setWithDriver(billDto.isWithDriver())
                .setDriverPrice(billDto.getDriverPrice())
                .setTotalPrice(billDto.getTotalPrice())
                .setStatus(billDto.getStatus());
    }

    public BillDto mapToDto(Bill bill){
        return new BillDto().setOrder(orderService.mapToDto(bill.getOrder()))
                .setPaymentDetails(paymentDetailsService.mapToDto(bill.getPaymentDetails()))
                .setStartDate(bill.getStartDate())
                .setExpirationDate(bill.getExpirationDate())
                .setCarPrice(bill.getCarPrice())
                .setWithDriver(bill.getWithDriver())
                .setDriverPrice(bill.getDriverPrice())
                .setTotalPrice(bill.getTotalPrice())
                .setStatus(bill.getStatus());
    }

    public long getDriverPrice(CarDto carDto){
        double driverPrice = carDto.getPricePerDay() * constants.PERCENT_OF_CAR_PRICE();
        driverPrice = driverPrice > constants.MIN_DRIVER_PRICE() ? driverPrice : constants.MIN_DRIVER_PRICE();
        driverPrice *= getSegmentMarkup(carDto.getSegment());
        if(driverPrice % 10 != 0) driverPrice = ((driverPrice / 10) + 1) * 10;
        return (long) driverPrice;
    }

    public long getTotalPrice(BillDto billDto){
        long timeDifference  = billDto.getExpirationDate().getTime().getTime() - billDto.getStartDate().getTime().getTime();
        long days = (timeDifference / (1000*60*60*24)) % 365;
        return days * (billDto.getCarPrice() + billDto.getDriverPrice());
    }

    public boolean isDateRangeCorrect(Calendar start, Calendar end){
        return end.compareTo(start) > 0;
    }

    private double getSegmentMarkup(CarSegment segment){
        switch (segment){
            case A:
            case B:
                return 1.3;
            case C:
            case D:
                return 1.5;
            case E:
                return 2;
            case F:
            case S:
                return 2.5;
            default: throw new IllegalArgumentException();
        }
    }

}
