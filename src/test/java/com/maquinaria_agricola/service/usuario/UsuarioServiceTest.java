package com.maquinaria_agricola.service.usuario;

import com.maquinaria_agricola.model.usuario.Rol;
import com.maquinaria_agricola.model.usuario.RoleUser;
import com.maquinaria_agricola.model.usuario.Usuario;
import com.maquinaria_agricola.repository.usuario.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void getAllUsuarios_retornaLista() {
        when(usuarioRepository.findAll()).thenReturn(List.of(new Usuario()));

        List<Usuario> usuarios = usuarioService.getAllUsuarios();

        assertEquals(1, usuarios.size());
        verify(usuarioRepository).findAll();
    }

    @Test
    void getByUserName_encontrado() {
        Usuario usuario = new Usuario();
        usuario.setUserName("test");

        when(usuarioRepository.findByUserName("test"))
                .thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.getByUserName("test");

        assertEquals("test", resultado.getUserName());
    }

    @Test
    void getByUserName_noEncontrado() {
        when(usuarioRepository.findByUserName("test"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> usuarioService.getByUserName("test"));
    }

    @Test
    void createUsuario_codificaContrasena() {
        Usuario usuario = new Usuario();
        usuario.setContrasena("1234");

        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Usuario creado = usuarioService.createUsuario(usuario);

        assertNotEquals("1234", creado.getContrasena());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void getUsuarioById_encontrado() {
        UUID id = UUID.randomUUID();
        Usuario usuario = new Usuario();
        usuario.setId(id);

        when(usuarioRepository.findById(id))
                .thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.getUsuarioById(id);

        assertEquals(id, resultado.getId());
    }

    @Test
    void getUsuarioById_noEncontrado() {
        UUID id = UUID.randomUUID();

        when(usuarioRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> usuarioService.getUsuarioById(id));
    }

    @Test
    void updateUsuario_actualizaCampos() {
        UUID id = UUID.randomUUID();

        Usuario existente = new Usuario();
        existente.setId(id);
        existente.setNombre("Viejo");

        Usuario cambios = new Usuario();
        cambios.setNombre("Nuevo");
        cambios.setContrasena("pass");

        when(usuarioRepository.findById(id))
                .thenReturn(Optional.of(existente));
        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Usuario actualizado = usuarioService.updateUsuario(id, cambios);

        assertEquals("Nuevo", actualizado.getNombre());
        assertNotEquals("pass", actualizado.getContrasena());
    }

    @Test
    void updateUsuario_noEncontrado() {
        UUID id = UUID.randomUUID();

        when(usuarioRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> usuarioService.updateUsuario(id, new Usuario()));
    }

    @Test
    void deleteUsuario_elimina() {
        UUID id = UUID.randomUUID();

        usuarioService.deleteUsuario(id);

        verify(usuarioRepository).deleteById(id);
    }

    @Test
    void loadUserByUsername_conRol() {
        Usuario usuario = new Usuario();
        usuario.setUserName("admin");
        usuario.setContrasena("pass");

        // Mock de la jerarquía de rol
        Rol role = new Rol();
        role.setNombre("ROLE_ADMIN");

        RoleUser roleUser = new RoleUser();
        roleUser.setRol(role);
        usuario.setRole(roleUser);

        when(usuarioRepository.findByUserName("admin"))
                .thenReturn(Optional.of(usuario));

        UserDetails details = usuarioService.loadUserByUsername("admin");

        assertEquals("admin", details.getUsername());
        assertEquals(1, details.getAuthorities().size());
        assertEquals("ROLE_ADMIN",
                details.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void loadUserByUsername_sinRol() {
        Usuario usuario = new Usuario();
        usuario.setUserName("user");
        usuario.setContrasena("pass");

        when(usuarioRepository.findByUserName("user"))
                .thenReturn(Optional.of(usuario));

        UserDetails details = usuarioService.loadUserByUsername("user");

        assertTrue(details.getAuthorities().isEmpty());
    }

    @Test
    void loadUserByUsername_noEncontrado() {
        when(usuarioRepository.findByUserName("x"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> usuarioService.loadUserByUsername("x"));
    }
}
