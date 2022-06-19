package com.epam.nazar.grinko.services;

import com.epam.nazar.grinko.domians.User;
import com.epam.nazar.grinko.domians.helpers.UserRole;
import com.epam.nazar.grinko.domians.helpers.UserStatus;
import com.epam.nazar.grinko.dto.AuthenticationRequestDto;
import com.epam.nazar.grinko.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.stream.Collectors;

import static com.epam.nazar.grinko.services.AuthenticationService.getUserDetails;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void testSuccessfulAuthentication(){
        String email = "test@gmail.com";
        String password = "test";

        AuthenticationRequestDto requestDto = new AuthenticationRequestDto().setEmail(email).setPassword(password);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);

        when(authenticationManager.authenticate(token)).thenReturn(token);
        when(userRepository.findByEmail(email)).thenReturn(
                Optional.of(new User().setEmail(email).setPassword(encode(password)))
        );


        User user = authenticationService.authenticateUser(requestDto);

        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getPassword()).isEqualTo(encode(password));
    }

    @Test
    void testUnsuccessfulAuthentication(){
        String email = "test@gmail.com";
        String password = "test";

        AuthenticationRequestDto requestDto = new AuthenticationRequestDto().setEmail(email).setPassword(password);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);

        when(authenticationManager.authenticate(token)).thenThrow(UsernameNotFoundException.class);

        assertThrows(UsernameNotFoundException.class, ()->authenticationService.authenticateUser(requestDto));
    }

    @Test
    void testGetActiveUserDetailsFromUser(){
        User user = new User().setId(1L)
                .setEmail("test@gmail.com")
                .setPassword(encode("test"))
                .setStatus(UserStatus.ACTIVE)
                .setRole(UserRole.ROLE_USER);

        UserDetails details = getUserDetails(user);
        assertThat(details.getUsername()).isEqualTo(user.getEmail());
        assertThat(details.getPassword()).isEqualTo(user.getPassword());
        assertThat(details.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .hasSize(1).contains(user.getRole().name());
        assertTrue(details.isEnabled());
        assertTrue(details.isAccountNonExpired());
        assertTrue(details.isAccountNonLocked());
        assertTrue(details.isCredentialsNonExpired());
    }

    @Test
    void testGetOnHoldUserDetailsFromUser(){
        User user = new User().setId(1L)
                .setEmail("test@gmail.com")
                .setPassword(encode("test"))
                .setStatus(UserStatus.ON_HOLD)
                .setRole(UserRole.ROLE_USER);

        UserDetails details = getUserDetails(user);
        assertThat(details.getUsername()).isEqualTo(user.getEmail());
        assertThat(details.getPassword()).isEqualTo(user.getPassword());
        assertThat(details.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .hasSize(1).contains(user.getRole().name());
        assertTrue(details.isEnabled());
        assertTrue(details.isAccountNonExpired());
        assertTrue(details.isAccountNonLocked());
        assertTrue(details.isCredentialsNonExpired());
    }

    @Test
    void testGetBannedUserDetailsFromUser(){
        User user = new User().setId(1L)
                .setEmail("test@gmail.com")
                .setPassword(encode("test"))
                .setStatus(UserStatus.BANNED)
                .setRole(UserRole.ROLE_USER);

        UserDetails details = getUserDetails(user);
        assertThat(details.getUsername()).isEqualTo(user.getEmail());
        assertThat(details.getPassword()).isEqualTo(user.getPassword());
        assertThat(details.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .hasSize(1).contains(user.getRole().name());
        assertFalse(details.isEnabled());
        assertTrue(details.isAccountNonExpired());
        assertFalse(details.isAccountNonLocked());
        assertTrue(details.isCredentialsNonExpired());
    }

    private String encode(String password){
        return password;
    }
}
