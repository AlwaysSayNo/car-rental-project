package com.epam.nazar.grinko.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotBlank;


@Getter
@Setter
@Accessors(chain = true)
public class CancellationDto {

    @NotBlank
    private String message;
    private OrderDto order;

}
