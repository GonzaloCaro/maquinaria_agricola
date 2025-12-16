package com.maquinaria_agricola.controller.usuario;

import com.maquinaria_agricola.DTO.usuario.LoginRequest;
import com.maquinaria_agricola.DTO.usuario.RegisterRequest;
import com.maquinaria_agricola.model.usuario.Rol;
import com.maquinaria_agricola.model.usuario.Usuario;
import com.maquinaria_agricola.repository.usuario.UsuarioRepository;
import com.maquinaria_agricola.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private AuthenticationManager authenticationManager;
    private JwtUtils jwtUtils;
    private UsuarioRepository usuarioRepository;
    private PasswordEncoder passwordEncoder;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        jwtUtils = mock(JwtUtils.class);
        usuarioRepository = mock(UsuarioRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);

        authController = new AuthController(authenticationManager, jwtUtils, usuarioRepository, passwordEncoder);
    }

    @Test
    void registerUser_Success() {
        RegisterRequest req = new RegisterRequest();
        req.setUserName("newuser");
        req.setEmail("test@example.com");
        req.setNombre("Nombre");
        req.setApellido("Apellido");
        req.setContrasena("password");

        when(usuarioRepository.findByUserName("newuser")).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encoded-pass");

        ResponseEntity<?> response = authController.registerUser(req);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Usuario registrado correctamente", response.getBody());

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        assertEquals("encoded-pass", captor.getValue().getContrasena());
    }

    @Test
    void registerUser_UsernameTaken_ReturnsBadRequest() {
        RegisterRequest req = new RegisterRequest();
        req.setUserName("existing");

        when(usuarioRepository.findByUserName("existing")).thenReturn(Optional.of(new Usuario()));

        ResponseEntity<?> response = authController.registerUser(req);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("El nombre de usuario ya está en uso", response.getBody());
    }

    @Test
    void registerUser_EmailTaken_ReturnsBadRequest() {
        RegisterRequest req = new RegisterRequest();
        req.setUserName("newuser");
        req.setEmail("taken@example.com");

        when(usuarioRepository.findByUserName("newuser")).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail("taken@example.com")).thenReturn(Optional.of(new Usuario()));

        ResponseEntity<?> response = authController.registerUser(req);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("El correo electrónico ya está registrado", response.getBody());
    }

    @Test
    void forgotPassword_EmailFound_ReturnsOk() {
        Usuario usuario = new Usuario();
        usuario.setEmail("test@example.com");
        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.of(usuario));

        ResponseEntity<?> response = authController.forgotPassword(Map.of("email", "test@example.com"));

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Correo de recuperación enviado", response.getBody());
    }

    @Test
    void forgotPassword_EmailNotFound_ReturnsNotFound() {
        when(usuarioRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.forgotPassword(Map.of("email", "missing@example.com"));

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Correo no encontrado", response.getBody());
    }

    @Test
    void authenticateUser_Success() {
        // Datos de prueba
        String username = "testuser";
        String password = "password";
        UUID userId = UUID.randomUUID();
        String jwtToken = "fake-jwt-token";

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUserName(username);
        loginRequest.setPassword(password);

        // Mock del Authentication y Security
        User springUser = new User(username, password, List.of());
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(springUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);

        // Mock del JWT
        when(jwtUtils.generateJwtToken(springUser)).thenReturn(jwtToken);

        // Mock del usuario en repo
        Usuario usuario = new Usuario();
        usuario.setId(userId);
        usuario.setUserName(username);
        usuario.setRole(null); // sin rol para este test
        when(usuarioRepository.findByUserName(username)).thenReturn(Optional.of(usuario));

        // Mock del HttpServletResponse
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Llamada al método
        ResponseEntity<?> result = authController.authenticateUser(loginRequest, response);

        // Verificaciones
        assertEquals(200, result.getStatusCodeValue());
        assertTrue(result.getBody() instanceof com.maquinaria_agricola.DTO.usuario.response.LoginResponse);

        var loginResp = (com.maquinaria_agricola.DTO.usuario.response.LoginResponse) result.getBody();
        assertEquals(jwtToken, loginResp.getAccessToken());
        assertEquals(userId, loginResp.getUserId());
        assertEquals(username, loginResp.getUserName());
        assertNull(loginResp.getRole());

        // Verificar que se agregue la cookie
        verify(response).addCookie(argThat(cookie -> "jwtToken".equals(cookie.getName()) &&
                jwtToken.equals(cookie.getValue()) &&
                cookie.getMaxAge() == 3600));

        // Verificar que AuthenticationManager fue llamado con el token correcto
        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor = ArgumentCaptor
                .forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(captor.capture());
        assertEquals(username, captor.getValue().getPrincipal());
        assertEquals(password, captor.getValue().getCredentials());
    }
}
