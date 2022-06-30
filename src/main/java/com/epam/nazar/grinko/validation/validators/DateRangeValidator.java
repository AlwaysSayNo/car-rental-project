package com.epam.nazar.grinko.validation.validators;

import com.epam.nazar.grinko.validation.annotations.DateRange;
import lombok.SneakyThrows;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.Calendar;

public class DateRangeValidator implements ConstraintValidator<DateRange, Object> {
    @SneakyThrows
    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        if(o == null) return true;
        Class<?> clazz = o.getClass();

        DateRange dateRange = clazz.getAnnotation(DateRange.class);

        Field startDateField = clazz.getDeclaredField(dateRange.startDate());
        startDateField.setAccessible(true);
        Calendar startDate = (Calendar) startDateField.get(o);

        Field endDateField = clazz.getDeclaredField(dateRange.endDate());
        endDateField.setAccessible(true);
        Calendar endDate = (Calendar) endDateField.get(o);

        return startDate.before(endDate);
    }
}
