package com.epam.nazar.grinko.securities;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public enum UserRole {

    ROLE_USER,
    ROLE_MANAGER,
    ROLE_ADMIN;

    public Set<SimpleGrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(name());
        return new HashSet<>(Collections.singletonList(authority));
    }

}
