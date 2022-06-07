package com.epam.nazar.grinko.dto;

import com.epam.nazar.grinko.domians.helpers.CarSegment;
import com.epam.nazar.grinko.domians.helpers.CarStatus;
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
    private CarSegment segment;
    private String color;
    private long pricePerDay;
    private CarStatus status;

}
