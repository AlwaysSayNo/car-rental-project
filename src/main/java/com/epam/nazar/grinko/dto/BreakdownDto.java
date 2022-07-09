package com.epam.nazar.grinko.dto;

import com.epam.nazar.grinko.domians.helpers.BreakdownStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BreakdownDto {
    
    private Long price;
    private String message;
    private BreakdownStatus status;
    private OrderDto order;

}
