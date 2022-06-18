package com.epam.nazar.grinko.dto;

import com.epam.nazar.grinko.domians.helpers.UserStatus;
import com.epam.nazar.grinko.domians.helpers.UserRole;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserDto {
    private String email;
    private String password;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private UserStatus status;
    private UserRole role;
}
