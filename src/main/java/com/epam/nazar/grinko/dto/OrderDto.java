package com.epam.nazar.grinko.dto;

import com.epam.nazar.grinko.domians.helpers.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.Valid;

@Getter
@Setter
@Accessors(chain = true)
public class OrderDto {

    @Valid
    private CarDto car;
    @Valid
    private UserDto user;
    private OrderStatus status;

}
