package com.epam.nazar.grinko.dto;

import com.epam.nazar.grinko.domians.helpers.BillStatus;
import com.epam.nazar.grinko.validation.annotations.MinDateToday;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Calendar;

@Getter
@Setter
@Accessors(chain = true)
public class BillDto {

    private OrderDto order;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @MinDateToday
    private Calendar startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @MinDateToday
    private Calendar expirationDate;
    private long carPrice;
    private boolean withDriver;
    private long driverPrice;
    private long totalPrice;
    private BillStatus status;

}
