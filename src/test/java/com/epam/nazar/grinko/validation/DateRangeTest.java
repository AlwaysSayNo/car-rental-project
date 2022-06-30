package com.epam.nazar.grinko.validation;

import com.epam.nazar.grinko.validation.annotations.DateRange;
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
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

public class DateRangeTest {

    private static Validator validator;
    private Calendar firstDate;
    private Calendar secondDate;

    @DateRange(startDate = "startDate", endDate = "endDate", message = "The start date is less than the end date.")
    static class RangeCovering {
        private Calendar startDate;
        private Calendar endDate;

        public RangeCovering(Calendar startDate, Calendar endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }

    @DateRange(startDate = "date", endDate = "endDate", message = "The start date is less than the end date.")
    static class IncorrectRangeCovering {
        private Calendar startDate;
        private Calendar endDate;

        public IncorrectRangeCovering(Calendar startDate, Calendar endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }

    @BeforeAll
    public static void setUp(){
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    public void setUpMethod(){
        firstDate = Calendar.getInstance();
        secondDate = Calendar.getInstance();
    }

    @Test
    public void testCorrectDateRangeWhereEndDateGreaterThanStartDate(){
        secondDate.add(Calendar.DATE, 2);
        RangeCovering range = new RangeCovering(firstDate, secondDate);

        Set<ConstraintViolation<RangeCovering>> violations = validator.validate(range);
        assertThat(violations.size()).isEqualTo(0);
    }

    @Test
    public void testIncorrectDateRangeWhereEndDateLessThanStartDate(){
        secondDate.add(Calendar.DATE, -2);
        RangeCovering range = new RangeCovering(firstDate, secondDate);

        Set<ConstraintViolation<RangeCovering>> violations = validator.validate(range);
        assertThat(violations.size()).isEqualTo(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("The start date is less than the end date.");
    }

    @Test
    public void testIncorrectDateRangeWhereEndDateEqualsThanStartDate(){
        RangeCovering range = new RangeCovering(firstDate, secondDate);

        Set<ConstraintViolation<RangeCovering>> violations = validator.validate(range);
        assertThat(violations.size()).isEqualTo(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("The start date is less than the end date.");
    }

    @Test
    public void testRangeFieldNotFound(){
        IncorrectRangeCovering range = new IncorrectRangeCovering(firstDate, secondDate);

        assertThatExceptionOfType(NoSuchFieldException.class).isThrownBy(() -> validator.validate(range));
    }

}
