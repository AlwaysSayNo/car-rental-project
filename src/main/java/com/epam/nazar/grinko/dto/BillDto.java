package com.epam.nazar.grinko.dto;

import com.epam.nazar.grinko.constants.ErrorsMessagesConstants;
import com.epam.nazar.grinko.domians.helpers.BillStatus;
import com.epam.nazar.grinko.validation.annotations.MinDateToday;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Calendar;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class BillDto {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @MinDateToday
    private Calendar startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @MinDateToday
    private Calendar expirationDate;
    @Min(value = 50, message = ErrorsMessagesConstants.INVALID_RANGE)
    @Max(value = 10_000, message = ErrorsMessagesConstants.INVALID_RANGE)
    private long carPrice;
    @Min(value = 50, message = ErrorsMessagesConstants.INVALID_RANGE)
    @Max(value = 10_000, message = ErrorsMessagesConstants.INVALID_RANGE)
    private long driverPrice;
    @Min(value = 50, message = ErrorsMessagesConstants.INVALID_RANGE)
    @Max(value = 1_000_000, message = ErrorsMessagesConstants.INVALID_RANGE)
    private long totalPrice;

    private OrderDto order;
    private boolean withDriver;
    private BillStatus status;

}
