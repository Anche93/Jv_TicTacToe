package org.example.domain.service;

import org.example.domain.model.Role;
import org.example.domain.model.SignUpRequest;
import org.example.domain.model.User;
import org.example.domain.port.PasswordEncoderPort;
import org.example.domain.port.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceImpTest {

    private UserRepositoryPort userRepositoryPort;
    private UserServiceImp userServiceImp;
    private PasswordEncoderPort passwordEncoderPort;

    @BeforeEach
    void setUp() {
        userRepositoryPort = mock(UserRepositoryPort.class);
        passwordEncoderPort = mock(PasswordEncoderPort.class);
        userServiceImp = new UserServiceImp(userRepositoryPort, passwordEncoderPort);
    }

    @Test
    void testGetUserLogin_ShouldReturnUser_WhenUserExists() {
        String login = "Anchik";
        User expectedUser = new User();
        expectedUser.setUserLogin(login);

        when(userRepositoryPort.findByUserLogin(login)).thenReturn(Optional.of(expectedUser));
        Optional<User> result = userServiceImp.getUserLogin(login);

        assertTrue(result.isPresent());
        assertEquals(expectedUser, result.get());
        verify(userRepositoryPort, times(1)).findByUserLogin(login);
    }

    @Test
    void testGetUserLogin_ShouldReturnEmpty_WhenUserDoesNotExist() {
        String login = "nobody";
        when(userRepositoryPort.findByUserLogin(login)).thenReturn(Optional.empty());

        Optional<User> result = userServiceImp.getUserLogin(login);
        assertFalse(result.isPresent());
        verify(userRepositoryPort, times(1)).findByUserLogin(login);
    }

    @Test
    void testGetUserById_ShouldReturnUser_WhenUserExist() {
        UUID userId = UUID.randomUUID();
        User expectedUser = new User();
        expectedUser.setUserId(userId);

        when(userRepositoryPort.findByUserId(userId)).thenReturn(Optional.of(expectedUser));
        Optional<User> result = userServiceImp.getUserById(userId);

        assertTrue(result.isPresent());
        assertEquals(expectedUser, result.get());
        verify(userRepositoryPort, times(1)).findByUserId(userId);
    }

    @Test
    void testGetUserById_ShouldReturnEmpty_WhenUserDoesNotExist() {
        UUID userId = UUID.randomUUID();
        when(userRepositoryPort.findByUserId(userId)).thenReturn(Optional.empty());
        Optional<User> result = userServiceImp.getUserById(userId);

        assertFalse(result.isPresent());
        verify(userRepositoryPort, times(1)).findByUserId(userId);
    }

    @Test
    void testRegistration_ShouldRegisterSuccessfully_WhenLoginIsFree() {
        SignUpRequest request = new SignUpRequest("Anchik", "123Password");

        when(userRepositoryPort.existsByUserLogin(request.getLogin())).thenReturn(false);
        when(passwordEncoderPort.encode(request.getPassword())).thenReturn("encodedPassword");

        boolean result = userServiceImp.registration(request);
        assertTrue(result);
        verify(userRepositoryPort, times(1)).existsByUserLogin("Anchik");
        verify(passwordEncoderPort, times(1)).encode("123Password");
        verify(userRepositoryPort, times(1)).save(any(User.class));
    }

    @Test
    void testRegistration_ShouldReturnFalse_WhenLoginExists() {
        SignUpRequest request = new SignUpRequest("Anchik", "123Password");
        when(userRepositoryPort.existsByUserLogin(request.getLogin())).thenReturn(true);
        boolean result = userServiceImp.registration(request);

        assertFalse(result);
        verify(userRepositoryPort, times(1)).existsByUserLogin("Anchik");
        verify(passwordEncoderPort, never()).encode(any());
        verify(userRepositoryPort, never()).save(any());
    }

    @Test
    void testAssignDefaultRole_ShouldAssignedDefaultRoleToNewUser() {
        SignUpRequest request = new SignUpRequest("Anchik", "123Password");
        when(userRepositoryPort.existsByUserLogin(request.getLogin())).thenReturn(false);
        when(passwordEncoderPort.encode(request.getPassword())).thenReturn("encodedPassword");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        userServiceImp.registration(request);

        verify(userRepositoryPort).save(captor.capture());
        User savedUser = captor.getValue();

        assertNotNull(savedUser.getRoles());
        assertFalse(savedUser.getRoles().isEmpty());
        assertEquals(Role.USER, savedUser.getRoles().getFirst());
    }
}
