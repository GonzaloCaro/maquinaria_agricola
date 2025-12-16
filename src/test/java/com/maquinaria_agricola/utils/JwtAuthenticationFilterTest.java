package com.maquinaria_agricola.utils;

import com.maquinaria_agricola.model.usuario.Rol;
import com.maquinaria_agricola.model.usuario.RoleUser; // O AsignacionRol, según tu modelo
import com.maquinaria_agricola.model.usuario.Usuario;
import com.maquinaria_agricola.service.usuario.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsService userDetailsService; // Se inyecta pero tu código no lo usa en el filtro

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext(); // Limpieza vital antes de cada test
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext(); // Limpieza después
    }

    // --- TEST 1: Autenticación vía COOKIE ---
    @Test
    @DisplayName("Debería autenticar extrayendo el token desde la Cookie 'jwtToken'")
    void doFilterInternal_ConCookie_AutenticaCorrectamente() throws ServletException, IOException {
        // GIVEN
        String token = "cookie.token.value";
        String username = "agricultor";

        // 1. Simulamos la Cookie
        Cookie cookie = new Cookie("jwtToken", token);
        when(request.getCookies()).thenReturn(new Cookie[] { cookie });

        // 2. Simulamos validación JWT
        when(jwtUtils.validateJwtToken(token)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(token)).thenReturn(username);

        // 3. Simulamos búsqueda de usuario y construcción de roles
        Usuario usuario = crearUsuarioConRol("agricultor", "ADMIN");
        when(usuarioService.getByUserName(username)).thenReturn(usuario);

        // WHEN
        // Llamamos al método público que ejecuta el protected internamente
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // THEN
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth, "La autenticación no debe ser nula");
        assertEquals(username, auth.getPrincipal());

        // Verificamos que se haya mapeado el rol correctamente (Tu código agrega
        // "ROLE_" + upperCase)
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));

        verify(filterChain).doFilter(request, response);
    }

    // --- TEST 2: Autenticación vía HEADER (Bearer) ---
    @Test
    @DisplayName("Debería autenticar extrayendo el token desde el Header Authorization")
    void doFilterInternal_ConHeader_AutenticaCorrectamente() throws ServletException, IOException {
        // GIVEN
        String token = "header.token.value";
        String username = "invitado";

        // 1. Sin cookies, pero con Header
        when(request.getCookies()).thenReturn(null);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        // 2. Validaciones
        when(jwtUtils.validateJwtToken(token)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(token)).thenReturn(username);

        // 3. Usuario
        Usuario usuario = crearUsuarioConRol("invitado", "USER");
        when(usuarioService.getByUserName(username)).thenReturn(usuario);

        // WHEN
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // THEN
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    // --- TEST 3: Prioridad (Cookie gana a Header) ---
    @Test
    @DisplayName("Si hay Cookie y Header, debería usar la Cookie")
    void doFilterInternal_PreferenciaCookie() throws ServletException, IOException {
        // GIVEN
        String cookieToken = "token.cookie";
        String headerToken = "token.header";

        // Simulamos ambos
        Cookie cookie = new Cookie("jwtToken", cookieToken);
        when(request.getCookies()).thenReturn(new Cookie[] { cookie });
        // (No necesitamos mockear getHeader porque tu código entra al if de cookies y
        // no sale a buscar header si encuentra token)

        when(jwtUtils.validateJwtToken(cookieToken)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(cookieToken)).thenReturn("user");
        when(usuarioService.getByUserName("user")).thenReturn(crearUsuarioConRol("user", "TEST"));

        // WHEN
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // THEN
        // Verificamos que se validó el token de la cookie, no el del header
        verify(jwtUtils).validateJwtToken(cookieToken);
        verify(jwtUtils, never()).validateJwtToken(headerToken);
    }

    // --- TEST 4: Token Inválido ---
    @Test
    @DisplayName("Si el token es inválido, no autentica")
    void doFilterInternal_TokenInvalido_NoAutentica() throws ServletException, IOException {
        // GIVEN
        when(request.getCookies()).thenReturn(null);
        when(request.getHeader("Authorization")).thenReturn("Bearer token.malo");
        when(jwtUtils.validateJwtToken("token.malo")).thenReturn(false);

        // WHEN
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // THEN
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(usuarioService);
    }

    // --- TEST 5: Usuario Sin Rol (Null Safety) ---
    @Test
    @DisplayName("Si el usuario no tiene rol asignado, autentica pero sin authorities")
    void doFilterInternal_UsuarioSinRol_NoFalla() throws ServletException, IOException {
        // GIVEN
        String token = "valid.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtils.validateJwtToken(token)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(token)).thenReturn("user_no_role");

        // Usuario sin rol (RoleUser es null)
        Usuario usuarioSinRol = new Usuario();
        usuarioSinRol.setUserName("user_no_role");
        usuarioSinRol.setRole(null);

        when(usuarioService.getByUserName("user_no_role")).thenReturn(usuarioSinRol);

        // WHEN
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // THEN
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertTrue(auth.getAuthorities().isEmpty(), "La lista de roles debería estar vacía");
    }

    // --- Helper para crear objetos complejos anidados ---
    private Usuario crearUsuarioConRol(String username, String rolNombre) {
        Rol rol = new Rol();
        rol.setNombre(rolNombre);

        RoleUser roleUser = new RoleUser();
        roleUser.setRol(rol);

        Usuario usuario = new Usuario();
        usuario.setUserName(username);
        usuario.setRole(roleUser);

        return usuario;
    }
}