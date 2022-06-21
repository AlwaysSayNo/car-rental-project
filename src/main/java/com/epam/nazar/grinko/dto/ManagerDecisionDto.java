package com.epam.nazar.grinko.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class ManagerDecisionDto {

    private UserDto manager;
    private OrderDto order;

}
