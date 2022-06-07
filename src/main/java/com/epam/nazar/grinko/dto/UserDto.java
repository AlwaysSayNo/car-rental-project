package com.epam.nazar.grinko.dto;

import com.epam.nazar.grinko.domians.helpers.UserStatus;
import com.epam.nazar.grinko.securities.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private String email;
    private String password;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private UserStatus status;
    private UserRole role;
}
