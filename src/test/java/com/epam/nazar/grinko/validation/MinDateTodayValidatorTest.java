package com.epam.nazar.grinko.validation;

import com.epam.nazar.grinko.validation.annotations.MinDateToday;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Calendar;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MinDateTodayValidatorTest {

    private static Validator validator;
    private Calendar now;

    static class DateCovering {
        @MinDateToday(message = "This date is later than today.")
        Calendar date;

        public DateCovering(Calendar date) {
            this.date = date;
        }
    }

    @BeforeAll
    public static void setUp(){
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    public void setUpMethod(){
        now = Calendar.getInstance();
    }

    @Test
    public void testDateAfterCurrentDate(){
        now.add(Calendar.DATE, 2);
        DateCovering date = new DateCovering(now);

        Set<ConstraintViolation<DateCovering>> violations = validator.validate(date);
        assertThat(violations.size()).isEqualTo(0);
    }

    @Test
    public void testDateBeforeCurrentDate(){
        now.add(Calendar.DATE, -2);
        DateCovering date = new DateCovering(now);

        Set<ConstraintViolation<DateCovering>> violations = validator.validate(date);
        assertThat(violations.size()).isEqualTo(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("This date is later than today.");
    }

    @Test
    public void testDateThatEqualsCurrentDate(){
        DateCovering date = new DateCovering(now);

        Set<ConstraintViolation<DateCovering>> violations = validator.validate(date);
        assertThat(violations.size()).isEqualTo(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("This date is later than today.");
    }


    @Test
    public void testNullDate(){
        DateCovering date = new DateCovering(null);

        Set<ConstraintViolation<DateCovering>> violations = validator.validate(date);
        assertThat(violations.size()).isEqualTo(0);
    }

}
