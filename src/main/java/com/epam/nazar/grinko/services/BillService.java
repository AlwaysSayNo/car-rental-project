package com.epam.nazar.grinko.services;

import com.epam.nazar.grinko.domians.Bill;
import com.epam.nazar.grinko.domians.helpers.CarSegment;
import com.epam.nazar.grinko.dto.BillDto;
import com.epam.nazar.grinko.dto.CarDto;
import com.epam.nazar.grinko.repositories.BillRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@PropertySource("classpath:/META-INF/my-application.properties")
public class BillService {

    @Value("${min.driver.price}")
    Long MIN_DRIVER_PRICE;
    @Value("${percent.of.car.price}")
    Double PERCENT_OF_CAR_PRICE;

    private final BillRepository billRepository;
    private final OrderService orderService;
    private final PaymentDetailsService paymentDetailsService;

    public BillService(BillRepository billRepository, OrderService orderService,
                       PaymentDetailsService paymentDetailsService) {
        this.billRepository = billRepository;
        this.orderService = orderService;
        this.paymentDetailsService = paymentDetailsService;
    }

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
        double driverPrice = carDto.getPricePerDay() * PERCENT_OF_CAR_PRICE;
        driverPrice = driverPrice > MIN_DRIVER_PRICE ? driverPrice : MIN_DRIVER_PRICE;
        driverPrice *= getSegmentMarkup(carDto.getSegment());
        if(driverPrice % 10 != 0) driverPrice = ((driverPrice / 10) + 1) * 10;
        return (long) driverPrice;
    }

    public long getTotalPrice(BillDto billDto){
        long timeDifference  = billDto.getExpirationDate().getTime().getTime() - billDto.getStartDate().getTime().getTime();
        long days = (timeDifference / (1000*60*60*24)) % 365;

        return days * billDto.getCarPrice() + billDto.getDriverPrice();
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
            default:
                return 1;
        }
    }
}
