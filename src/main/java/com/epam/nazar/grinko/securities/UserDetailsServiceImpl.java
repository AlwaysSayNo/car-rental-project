package com.epam.nazar.grinko.securities;

import com.epam.nazar.grinko.domians.User;
import com.epam.nazar.grinko.services.AuthenticationService;
import com.epam.nazar.grinko.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service("userDetailsServiceImpl")
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.getByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("User doesn't exists"));
        return AuthenticationService.getUserDetails(user);
    }
}
