package com.epam.nazar.grinko.dto;

import lombok.Getter;

@Getter
public class AuthenticationRequestDto {
    private String email;
    private String password;
}
