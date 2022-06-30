package com.epam.nazar.grinko.validation.annotations;

import com.epam.nazar.grinko.validation.validators.MinDateTodayValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MinDateTodayValidator.class)
public @interface MinDateToday {
    String message() default "{min.day.today.error}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
