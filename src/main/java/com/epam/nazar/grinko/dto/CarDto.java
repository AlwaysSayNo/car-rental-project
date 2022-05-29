package com.epam.nazar.grinko.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CarDto {

    private String brand;
    private String name;
    private String number;
    private String segment;
    private String color;
    private long pricePerDay;

}
