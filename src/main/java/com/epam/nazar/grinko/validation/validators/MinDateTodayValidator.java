package com.epam.nazar.grinko.validation.validators;

import com.epam.nazar.grinko.validation.annotations.MinDateToday;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Calendar;

public class MinDateTodayValidator implements ConstraintValidator<MinDateToday, Calendar> {
    @Override
    public boolean isValid(Calendar date, ConstraintValidatorContext constraintValidatorContext) {
        if(date == null) return true;
        Calendar now = Calendar.getInstance();
        return now.before(date);
    }
}
