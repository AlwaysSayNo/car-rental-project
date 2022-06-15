package com.epam.nazar.grinko.dto;

import com.epam.nazar.grinko.domians.helpers.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class OrderDto {

    private CarDto car;
    private UserDto user;
    private OrderStatus status;

}
