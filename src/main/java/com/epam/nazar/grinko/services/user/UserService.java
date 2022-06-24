package com.epam.nazar.grinko.services.user;

import com.epam.nazar.grinko.domians.User;
import com.epam.nazar.grinko.domians.helpers.UserStatus;
import com.epam.nazar.grinko.dto.UserDto;
import com.epam.nazar.grinko.repositories.UserRepository;
import com.epam.nazar.grinko.domians.helpers.UserRole;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserQueryManipulationService manipulationService;
    @Lazy
    private final PasswordEncoder passwordEncoder;

    public Optional<User> getByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public void updateUserById(User user, Long id){
        String password = encodePassword(user);
        userRepository.updateUserById(user.getEmail(), user.getFirstName(), user.getLastName(), password, user.getRole(), user.getPhoneNumber(), user.getStatus(), id);
    }

    public void updateUserStatusById(UserStatus status, Long id){
        userRepository.updateUserStatusById(status, id);
    }

    public Page<User> getUsersByRole(PageRequest request, UserRole role, String filterBy, String filterValue){
        Map<String, String> byRole = new HashMap<>();
        byRole.put("role", role.name());

        if(filterBy != null)
            byRole.put(filterBy, filterValue);

        return manipulationService.evaluateQuery(request, byRole);
    }

    public User getById(Long id){
        return userRepository.getById(id);
    }

    public void deleteById(Long id){
        userRepository.deleteById(id);
    }

    public void save(User newUser){
        newUser.setPassword(encodePassword(newUser));
        userRepository.save(newUser);
    }

    public boolean existsByEmail(String email){
        return userRepository.existsByEmail(email);
    }

    public boolean existsByIdAndRole(Long id, UserRole role){
        return userRepository.existsByIdAndRole(id, role);
    }

    public Optional<Long> getUserIdByEmail(String email){
        return userRepository.getIdByEmail(email);
    }

    public String encodePassword(User user){
        return passwordEncoder.encode(user.getPassword());
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

    public UserDto mapToDto(User user){
        return new UserDto().setEmail(user.getEmail())
                .setPassword(user.getPassword())
                .setPhoneNumber(user.getPhoneNumber())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setStatus(user.getStatus())
                .setRole(user.getRole());
    }

    public UserQueryManipulationService getManipulationService() {
        return manipulationService;
    }
}
