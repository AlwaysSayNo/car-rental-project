package com.epam.nazar.grinko.constants;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:/META-INF/my-application.properties")
@Getter
@Accessors(fluent = true)
public class BillConstants {

    @Value("${min.driver.price}")
    private Long MIN_DRIVER_PRICE;
    @Value("${percent.of.car.price}")
    private Double PERCENT_OF_CAR_PRICE;

}
