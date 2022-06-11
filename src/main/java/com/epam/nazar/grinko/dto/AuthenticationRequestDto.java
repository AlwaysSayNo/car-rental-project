package com.epam.nazar.grinko.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class AuthenticationRequestDto {
    private String email;
    private String password;
}
