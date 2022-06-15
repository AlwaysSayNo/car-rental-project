package com.epam.nazar.grinko.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Calendar;

@Getter
@Setter
@Accessors(chain = true)
public class PaymentDetailsDto {

    private String firstName;
    private String lastName;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Calendar dateOfBirth;
    private String passportNumber;
    private String cardNumber;
    private String expirationDate;
    private Long CVC;

}
