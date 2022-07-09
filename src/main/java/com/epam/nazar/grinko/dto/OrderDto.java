package com.epam.nazar.grinko.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.Valid;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OrderDto {

    @Valid
    private CarDto car;
    @Valid
    private UserDto user;
    private String status;

}
