package com.epam.nazar.grinko.services;

import com.epam.nazar.grinko.domians.User;
import com.epam.nazar.grinko.domians.helpers.UserStatus;
import com.epam.nazar.grinko.dto.UserDto;
import com.epam.nazar.grinko.repositories.UserRepository;
import com.epam.nazar.grinko.domians.helpers.UserRole;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    @Lazy
    private final PasswordEncoder passwordEncoder;

    public Optional<User> getByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public void updateUserById(User user, long id){
        String password = encodePassword(user);
        userRepository.updateUserById(user.getEmail(), user.getFirstName(), user.getLastName(), password, user.getRole(), user.getPhoneNumber(), user.getStatus(), id);
    }

    public void updateUserStatusById(UserStatus status, long id){
        userRepository.updateUserStatusById(status, id);
    }

    public List<User> getUsersByRole(UserRole role){
        return userRepository.getAllByRole(role);
    }

    public User getById(long id){
        return userRepository.getById(id);
    }

    public void deleteById(long id){
        userRepository.deleteById(id);
    }

    public void save(User newUser){
        newUser.setPassword(encodePassword(newUser));
        userRepository.save(newUser);
    }

    public boolean existsByEmail(String email){
        return userRepository.existsByEmail(email);
    }

    public boolean existsByIdAndRole(long id, UserRole role){
        return userRepository.existsByIdAndRole(id, role);
    }

    public Optional<Long> getUserIdByEmail(String email){
        return userRepository.getIdByEmail(email);
    }

    public User mapToObject(UserDto userDto){
        return new User().setEmail(userDto.getEmail())
                .setPassword(userDto.getPassword())
                .setPhoneNumber(userDto.getPhoneNumber())
                .setFirstName(userDto.getFirstName())
                .setLastName(userDto.getLastName())
                .setStatus(userDto.getStatus())
                .setRole(userDto.getRole());
    }

    public String encodePassword(User user){
        return passwordEncoder.encode(user.getPassword());

    }

    public UserDto mapToDto(User user){
        return new UserDto().setEmail(user.getEmail())
                .setPassword(user.getPassword())
                .setPhoneNumber(user.getPhoneNumber())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setStatus(user.getStatus())
                .setRole(user.getRole());
    }
}
