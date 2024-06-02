import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.example.common.PageRequest;
import org.example.dto.UserDto;
import org.example.email.EmailService;
import org.example.user.User;
import org.example.user.UserMapper;
import org.example.user.UserRepository;
import org.example.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    private UserDto userDto;
    private User user;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setId(null);
        userDto.setUsername("someName");
        userDto.setEmail("someName@example.com");

        user = new User();
        user.setId(1L);
        user.setUsername("someName");
        user.setEmail("someName@example.com");
        user.setToken(UUID.randomUUID().toString());
    }

    @Test
    void testAddNewUser() {
        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);
        when(userRepository.existsByUsername(userDto.getUsername())).thenReturn(false);
        when(userRepository.add(user)).thenReturn(user);

        UserDto result = userService.add(userDto);

        assertNotNull(result);
        assertEquals("someName", result.getUsername());
        assertEquals("someName@example.com", result.getEmail());
        verify(userRepository, times(1)).add(user);
        verify(emailService, times(1)).sendConfirmationEmail(user);
    }

    @Test
    void testAddExistingUserThrowsException() {
        when(userRepository.existsByUsername(userDto.getUsername())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.add(userDto));
    }

    @Test
    void testUpdateUser() {
        userDto.setId(1L);
        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);
        when(userRepository.update(user)).thenReturn(user);

        UserDto result = userService.add(userDto);

        assertNotNull(result);
        assertEquals("someName", result.getUsername());
        verify(userRepository, times(1)).update(user);
    }

    @Test
    void testGetUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.get(1L);

        assertNotNull(result);
        assertEquals("someName", result.getUsername());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUserByIdThrowsIllegalArgumentException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.get(1L));
    }

    @Test
    void testGetAllUsers() {
        PageRequest pageRequest = PageRequest.builder().build();
        List<User> users = List.of(user);
        when(userRepository.findAll(pageRequest)).thenReturn(users);

        List<UserDto> result = userService.getAllUsers(pageRequest);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("someName", result.get(0).getUsername());
        verify(userRepository, times(1)).findAll(pageRequest);
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userRepository).delete(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).delete(1L);
    }

    @Test
    void testActivateAccount() {
        when(userRepository.findByActivationToken("some-activation-token")).thenReturn(Optional.of(user));

        userService.activateAccount("some-activation-token");

        assertTrue(user.isActivated());
        assertNull(user.getToken());
        verify(userRepository, times(1)).update(user);
    }

    @Test
    void testActivateAccountThrowsException() {
        when(userRepository.findByActivationToken("invalid-token")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.activateAccount("invalid-token"));
    }
}