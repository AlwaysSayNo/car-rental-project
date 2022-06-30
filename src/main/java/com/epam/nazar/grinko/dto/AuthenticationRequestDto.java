package com.epam.nazar.grinko.dto;

import com.epam.nazar.grinko.constants.ErrorsMessagesConstants;
import com.epam.nazar.grinko.constants.RegexpConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;

@Getter
@Setter
@Accessors(chain = true)
public class AuthenticationRequestDto {
    @NotBlank(message = ErrorsMessagesConstants.REQUIRED)
    @Pattern(regexp = RegexpConstants.EMAIL, message = ErrorsMessagesConstants.INVALID_STRUCTURE)
    private String email;
    @NotBlank(message = ErrorsMessagesConstants.REQUIRED)
    @Pattern(regexp = RegexpConstants.PASSWORD, message = ErrorsMessagesConstants.INVALID_STRUCTURE)
    private String password;
}
