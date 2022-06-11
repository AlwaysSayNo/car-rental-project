package com.epam.nazar.grinko.services;

import com.epam.nazar.grinko.domians.User;
import com.epam.nazar.grinko.domians.helpers.UserStatus;
import com.epam.nazar.grinko.dto.UserDto;
import com.epam.nazar.grinko.repositories.UserRepository;
import com.epam.nazar.grinko.securities.UserRole;
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

    public Optional<User> getUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public void updateUserById(User user, long id){
        encodePassword(user);
        userRepository.updateUserById(user.getEmail(), user.getFirstName(), user.getLastName(), user.getPassword(), user.getRole(), user.getPhoneNumber(), user.getStatus(), id);
    }

    public List<User> getUsersByRole(UserRole role){
        return userRepository.getAllByRole(role);
    }

    public User getUserById(long id){
        return userRepository.getById(id);
    }

    public void deleteManagerById(long id){
        userRepository.deleteByIdAndRole(id, UserRole.ROLE_MANAGER);
    }

    public void addNewUser(User newUser){
        encodePassword(newUser);
        userRepository.save(newUser);
    }

    public boolean existsUserByEmail(String email){
        return userRepository.existsByEmail(email);
    }

    public void setBasicMetaParameters(UserDto userDto){
        userDto.setStatus(UserStatus.ACTIVE);
        userDto.setRole(UserRole.ROLE_USER);
    }

    public User convertUserDtoToUser(UserDto userDto){
        return new User().setEmail(userDto.getEmail())
                .setPassword(userDto.getPassword())
                .setPhoneNumber(userDto.getPhoneNumber())
                .setFirstName(userDto.getFirstName())
                .setLastName(userDto.getLastName())
                .setStatus(userDto.getStatus())
                .setRole(userDto.getRole());
    }

    public void encodePassword(User user){
        String password = passwordEncoder.encode(user.getPassword());
        user.setPassword(password);

    }

    public UserDto convertUserToUserDto(User user){
        return new UserDto().setEmail(user.getEmail())
                .setPassword(user.getPassword())
                .setPassword(user.getPhoneNumber())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setStatus(user.getStatus())
                .setRole(user.getRole());
    }
}
