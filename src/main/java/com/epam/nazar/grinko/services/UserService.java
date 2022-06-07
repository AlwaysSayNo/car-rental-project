package com.epam.nazar.grinko.services;

import com.epam.nazar.grinko.domians.User;
import com.epam.nazar.grinko.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public Optional<User> getUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public boolean existsUserByEmail(String email){
        return userRepository.existsByEmail(email);
    }

}
