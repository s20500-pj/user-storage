package org.example.user;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.example.dto.UserDto;

import java.util.Optional;

@ApplicationScoped
public class UserMapper {

    @Inject
    private UserRepository userRepository;

    public User toEntity(UserDto userDto) {
        return Optional.ofNullable(userDto.getId())
                .flatMap(userRepository::findById)
                .map(entity -> entity.toEntity(userDto))
                .orElseGet(() -> new User().toEntity(userDto));
    }

    public UserDto toDto(User user) {
        return user.toDto();
    }
}
