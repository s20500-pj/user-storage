package org.example.user;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.example.common.PageRequest;
import org.example.dto.UserDto;
import org.example.email.EmailService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
public class UserService {

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserMapper userMapper;

    @Inject
    private EmailService emailService;

    public UserDto add(UserDto dto) {
        if (Objects.nonNull(dto.getId())) {
            log.trace("Updating user {}", dto);
            return userMapper.toDto(userRepository.update(userMapper.toEntity(dto)));
        }
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        log.trace("Adding user {}", dto);
        User user = userRepository.add(userMapper.toEntity(dto));
        emailService.sendConfirmationEmail(user);
        return userMapper.toDto(user);
    }

    public UserDto get(Long id) {
        log.trace("Getting user with id: {}", id);
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return userMapper.toDto(user.get());
        }
        throw new IllegalArgumentException("User with given id doesn't exist");
    }

    public List<UserDto> getAllUsers(PageRequest pageRequest) {
        log.trace("Searching for users {}", pageRequest);
        return userRepository.findAll(pageRequest).stream()
                .map(User::toDto)
                .collect(Collectors.toList());
    }

    public void deleteUser(Long id) {
        log.trace("Deleting user with id: {}", id);
        userRepository.delete(id);
    }

    public void activateAccount(String token) {
        log.trace("User activation started for token {}", token);
        Optional<User> userOptional = userRepository.findByActivationToken(token);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Invalid activation token");
        }
        User user = userOptional.get();
        user.setActivated(true);
        user.setToken(null);
        userRepository.update(user);
        log.trace("Activated user with id {}", user.getId());
    }
}
