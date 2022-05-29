package com.epam.nazar.grinko.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class CarDto {

    private String brand;
    private String name;
    private String number;
    private String segment;
    private String color;
    private long pricePerDay;
    private String status;

}
