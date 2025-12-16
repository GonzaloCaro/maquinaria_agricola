package com.maquinaria_agricola.service.usuario;

import com.maquinaria_agricola.model.usuario.Rol;
import com.maquinaria_agricola.model.usuario.RoleUser;
import com.maquinaria_agricola.model.usuario.Usuario;
import com.maquinaria_agricola.repository.usuario.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    // --- TEST: Usuario Encontrado (Happy Path) ---
    @Test
    @DisplayName("Debería retornar UserDetails con roles cuando el usuario existe y tiene datos completos")
    void loadUserByUsername_Exito() {
        // GIVEN
        String username = "agricultor";

        // Construimos la cadena completa para evitar NullPointerException
        Rol rol = new Rol();
        rol.setNombre("ADMIN"); // Spring Security lo transformará a "ROLE_ADMIN"

        RoleUser roleUser = new RoleUser();
        roleUser.setRol(rol);

        Usuario usuario = new Usuario();
        usuario.setUserName(username);
        usuario.setContrasena("pass123");
        usuario.setRole(roleUser); // Vinculación vital

        when(usuarioRepository.findByUserName(username)).thenReturn(Optional.of(usuario));

        // WHEN
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        // THEN
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals("pass123", userDetails.getPassword());

        // Verificamos Authorities.
        // OJO: El builder .roles("ADMIN") agrega automáticamente el prefijo "ROLE_"
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    // --- TEST: Usuario No Encontrado ---
    @Test
    @DisplayName("Debería lanzar UsernameNotFoundException si el usuario no existe en BD")
    void loadUserByUsername_NoEncontrado() {
        // GIVEN
        String username = "fantasma";
        when(usuarioRepository.findByUserName(username)).thenReturn(Optional.empty());

        // WHEN & THEN
        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(username);
        });

        assertTrue(ex.getMessage().contains(username));
    }

    // --- TEST: Usuario Existe pero Datos Incompletos (Null Safety) ---
    @Test
    @DisplayName("Debería lanzar NullPointerException si el usuario no tiene rol asignado")
    void loadUserByUsername_UsuarioSinRol_LanzaNPE() {
        // GIVEN
        String username = "user_roto";
        Usuario usuarioSinRol = new Usuario();
        usuarioSinRol.setUserName(username);
        usuarioSinRol.setRole(null); // Esto causará el fallo en tu código actual

        when(usuarioRepository.findByUserName(username)).thenReturn(Optional.of(usuarioSinRol));

        // WHEN & THEN
        // Tu código hace: usuario.getRole().getRol()... sin verificar null.
        // Este test confirma ese comportamiento (útil para refactorizar en el futuro).
        assertThrows(NullPointerException.class, () -> {
            customUserDetailsService.loadUserByUsername(username);
        });
    }
}