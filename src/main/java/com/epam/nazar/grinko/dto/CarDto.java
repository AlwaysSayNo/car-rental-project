package com.epam.nazar.grinko.dto;

import com.epam.nazar.grinko.constants.ErrorsMessagesConstants;
import com.epam.nazar.grinko.constants.RegexpConstants;
import com.epam.nazar.grinko.domians.helpers.CarSegment;
import com.epam.nazar.grinko.domians.helpers.CarStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

@Setter
@Getter
@ToString
@Accessors(chain = true)
public class CarDto {

    @NotBlank(message = ErrorsMessagesConstants.REQUIRED)
    private String brand;
    @NotBlank(message = ErrorsMessagesConstants.REQUIRED)
    private String name;
    @NotBlank(message = ErrorsMessagesConstants.REQUIRED)
    @Pattern(regexp = RegexpConstants.CAR_NUMBER_REGEXP, message = ErrorsMessagesConstants.INVALID_STRUCTURE)
    private String number;
    @NotBlank(message = ErrorsMessagesConstants.REQUIRED)
    private String color;
    @Min(value = 50, message = ErrorsMessagesConstants.INVALID_RANGE)
    @Max(value = 10_000, message = ErrorsMessagesConstants.INVALID_RANGE)
    private long pricePerDay;

    private String segment;
    private String status;

}
