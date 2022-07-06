package com.epam.nazar.grinko.dto;

import com.epam.nazar.grinko.constants.ErrorsMessagesConstants;
import com.epam.nazar.grinko.constants.RegexpConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class UserDto {
    @NotBlank(message = ErrorsMessagesConstants.REQUIRED)
    @Pattern(regexp = RegexpConstants.EMAIL, message = ErrorsMessagesConstants.INVALID_STRUCTURE)
    private String email;
    @NotBlank(message = ErrorsMessagesConstants.REQUIRED)
    @Pattern(regexp = RegexpConstants.PASSWORD, message = ErrorsMessagesConstants.INVALID_STRUCTURE)
    private String password;
    @NotBlank(message = ErrorsMessagesConstants.REQUIRED)
    @Pattern(regexp = RegexpConstants.PHONE_NUMBER, message = ErrorsMessagesConstants.INVALID_STRUCTURE)
    private String phoneNumber;
    @NotBlank(message = ErrorsMessagesConstants.REQUIRED)
    @Pattern(regexp = RegexpConstants.FIRST_NAME, message = ErrorsMessagesConstants.INVALID_STRUCTURE)
    private String firstName;
    @NotBlank(message = ErrorsMessagesConstants.REQUIRED)
    @Pattern(regexp = RegexpConstants.LAST_NAME, message = ErrorsMessagesConstants.INVALID_STRUCTURE)
    private String lastName;

    private String status;
    private String role;
}
