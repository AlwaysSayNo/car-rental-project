package com.epam.nazar.grinko.services;

import com.epam.nazar.grinko.domians.Bill;
import com.epam.nazar.grinko.domians.helpers.CarSegment;
import com.epam.nazar.grinko.dto.BillDto;
import com.epam.nazar.grinko.dto.CarDto;
import com.epam.nazar.grinko.repositories.BillRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource("classpath:/META-INF/my-application.properties")
public class BillService {

    @Value("${min.driver.price}")
    Long MIN_DRIVER_PRICE;
    @Value("${percent.of.car.price}")
    Double PERCENT_OF_CAR_PRICE;

    private final BillRepository billRepository;
    private final OrderService orderService;
    private final CarService carService;
    private final PaymentDetailsService paymentDetailsService;

    public BillService(BillRepository billRepository, OrderService orderService,
                       PaymentDetailsService paymentDetailsService, CarService carService) {
        this.billRepository = billRepository;
        this.orderService = orderService;
        this.paymentDetailsService = paymentDetailsService;
        this.carService = carService;
    }

    public void addBill(Bill bill){
        orderService.addOrder(bill.getOrder());
        billRepository.save(bill);
    }


    public Bill convertBillDtoToBill(BillDto billDto){
        return new Bill().setOrder(orderService.convertOrderDtoToOrder(billDto.getOrder()))
                .setPaymentDetails(paymentDetailsService.mapToObject(billDto.getPaymentDetails()))
                .setStartDate(billDto.getStartDate())
                .setExpirationDate(billDto.getExpirationDate())
                .setCarPrice(billDto.getCarPrice())
                .setWithDriver(billDto.isWithDriver())
                .setDriverPrice(billDto.getDriverPrice())
                .setTotalPrice(billDto.getTotalPrice())
                .setStatus(billDto.getStatus());
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
