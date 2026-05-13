package org.example.security;

import io.jsonwebtoken.Jwts;
import org.example.domain.model.Role;
import org.example.domain.model.User;
import org.example.domain.port.PasswordEncoderPort;
import org.example.domain.port.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthFilterIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepositoryPort userRepositoryPort;

    @MockBean
    private PasswordEncoderPort passwordEncoderPort;

    @Autowired
    private JwtProvider jwtProvider;

    private UUID userId;
    private String validAccessToken;
    private String validRefreshToken;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        User user = new User();
        user.setUserId(userId);
        user.setUserLogin("TestUser");
        user.setUserPasswordHash("encodedPassword");
        user.setRoles(List.of(Role.USER));

        validAccessToken = jwtProvider.generateAccessToken(user);
        validRefreshToken = jwtProvider.generateRefreshToken(user);

        when(userRepositoryPort.findByUserLogin("TestUser")).thenReturn(Optional.of(user));
        when(userRepositoryPort.findByUserId(userId)).thenReturn(Optional.of(user));
        when(userRepositoryPort.existsByUserLogin("TestUser")).thenReturn(true);
        when(passwordEncoderPort.matches(eq("123password"), eq("encodedPassword")))
                .thenReturn(true);
    }

    @Test
    void testDoFilter_ShouldAllowAccessToLoginEndpoint() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"TestUser\",\"password\":\"123password\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testDoFilter_ShouldAllowAccessToRefreshAccessEndpoint() throws Exception {
        mockMvc.perform(post("/auth/refresh-access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"test\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDoFilter_ShouldAllowAccessToRefreshRefreshEndpoint() throws Exception {
        mockMvc.perform(post("/auth/refresh-refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"test\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDoFilter_ShouldAllowAccessToRegistrationEndpoint() throws Exception {
        User newUser = new User(
                UUID.randomUUID(), "NewUser",
                "encodedNewPassword", List.of(Role.USER));
        when(userRepositoryPort.existsByUserLogin("NewUser")).thenReturn(false);
        when(passwordEncoderPort.encode("123password"))
                .thenReturn("encodedNewPassword");
        doNothing().when(userRepositoryPort).save(any(User.class));
        when(userRepositoryPort.findByUserLogin("NewUser")).thenReturn(Optional.of(newUser));

        mockMvc.perform(post("/user/reg")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"NewUser\",\"password\":\"123password\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void testDoFilter_ShouldRejectRequestWithoutToken() throws Exception {
        mockMvc.perform(get("/user/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDoFilter_ShouldAcceptRequestWithValidToken() throws Exception {
        mockMvc.perform(get("/user/me")
                        .header("Authorization", "Bearer " + validAccessToken))
                .andExpect(status().isOk());

    }

    @Test
    void testDoFilter_ShouldRejectRequestWithInvalidToken() throws Exception {
        mockMvc.perform(get("/user/me")
                        .header("Authorization", "Bearer invalid.token.value"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDoFilter_ShouldRejectRequestWithInvalidTokenStartWithBasic() throws Exception {
        mockMvc.perform(get("/user/me")
                        .header("Authorization", "Basic " + validAccessToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDoFilter_ShouldRejectRequestWithExpiredToken() throws Exception {
        String expiredToken = Jwts.builder()
                .subject(userId.toString())
                .claim("roles", "ROLE_USER")
                .issuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24))
                .expiration(new Date(System.currentTimeMillis() - 1000 * 60))
                .signWith(jwtProvider.secretKey(), Jwts.SIG.HS256)
                .compact();

        mockMvc.perform(get("/user/me")
                        .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());
    }
}
