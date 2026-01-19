package github.maxsuel.agregadordeinvestimentos.controller;

import github.maxsuel.agregadordeinvestimentos.dto.CreateUserDto;
import github.maxsuel.agregadordeinvestimentos.entity.User;
import github.maxsuel.agregadordeinvestimentos.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    @DisplayName("Should return 201 Created and location header")
    void shouldCreateUser() {
        // Arrange
        var dto = new CreateUserDto("username", "username@email.com", "123");
        var userId = UUID.randomUUID();
        when(userService.createUser(dto)).thenReturn(userId);

        // Act
        var response = userController.createUser(dto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("/users/" + userId, response.getHeaders().getLocation().getPath());
        verify(userService, times(1)).createUser(dto);
    }

    @Test
    @DisplayName("Should return 200 OK when user exists")
    void shouldGetUserById() {
        // Arrange
        var userId = UUID.randomUUID();
        var user = new User("username", "username@email.com", "123");
        when(userService.getUserById(userId.toString())).thenReturn(Optional.of(user));

        // Act
        var response = userController.getUserById(userId.toString());

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    @DisplayName("Should return 404 Not Found when user does not exist")
    void shouldReturnNotFound() {
        // Arrange
        var userId = UUID.randomUUID().toString();
        when(userService.getUserById(userId)).thenReturn(Optional.empty());

        // Act
        var response = userController.getUserById(userId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
