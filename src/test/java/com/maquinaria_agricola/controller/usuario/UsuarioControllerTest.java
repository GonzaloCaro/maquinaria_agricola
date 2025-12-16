package com.maquinaria_agricola.controller.usuario;

import com.maquinaria_agricola.DTO.usuario.UsuarioDTO;
import com.maquinaria_agricola.hateoas.UsuarioModelAssembler;
import com.maquinaria_agricola.mapper.UsuarioMapper;
import com.maquinaria_agricola.model.ResponseWrapper;
import com.maquinaria_agricola.model.usuario.Usuario;
import com.maquinaria_agricola.service.usuario.UsuarioService;
import com.maquinaria_agricola.exception.ResourceNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioControllerTest {

    private UsuarioService usuarioService;
    private UsuarioMapper usuarioMapper;
    private UsuarioModelAssembler usuarioModelAssembler;
    private UsuarioController usuarioController;

    @BeforeEach
    void setUp() {
        usuarioService = mock(UsuarioService.class);
        usuarioMapper = mock(UsuarioMapper.class);
        usuarioModelAssembler = mock(UsuarioModelAssembler.class);
        usuarioController = new UsuarioController(usuarioService, usuarioMapper, usuarioModelAssembler);
    }

    @Test
    void getAllUsuarios_Success_ReturnsUsuarios() {
        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        List<Usuario> usuarios = List.of(usuario);

        when(usuarioService.getAllUsuarios()).thenReturn(usuarios);
        when(usuarioModelAssembler.toModel(usuario)).thenReturn(EntityModel.of(usuario));

        ResponseEntity<CollectionModel<EntityModel<Usuario>>> response = usuarioController.getAllUsuarios();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
    }

    @Test
    void getAllUsuarios_NoUsuarios_ThrowsException() {
        when(usuarioService.getAllUsuarios()).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> usuarioController.getAllUsuarios());
    }

    @Test
    void getUsuarioById_Success() {
        UUID id = UUID.randomUUID();
        Usuario usuario = new Usuario();
        usuario.setId(id);

        when(usuarioService.getUsuarioById(id)).thenReturn(usuario);
        when(usuarioModelAssembler.toModel(usuario)).thenReturn(EntityModel.of(usuario));

        ResponseEntity<EntityModel<Usuario>> response = usuarioController.getUsuarioById(id);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(id, response.getBody().getContent().getId());
    }

    @Test
    void getUsuarioById_NullId_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> usuarioController.getUsuarioById(null));
    }

    @Test
    void createUsuario_Success() {
        UsuarioDTO dto = new UsuarioDTO();
        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());

        when(usuarioMapper.toEntity(dto)).thenReturn(usuario);
        when(usuarioService.createUsuario(usuario)).thenReturn(usuario);
        when(usuarioModelAssembler.toModel(usuario)).thenReturn(EntityModel.of(usuario));

        ResponseEntity<EntityModel<Usuario>> response = usuarioController.createUsuario(dto);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(usuario.getId(), response.getBody().getContent().getId());
    }

    @Test
    void createUsuario_NullDTO_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> usuarioController.createUsuario(null));
    }

    @Test
    void updateUsuario_Success() {
        UUID id = UUID.randomUUID();
        UsuarioDTO dto = new UsuarioDTO();
        Usuario usuario = new Usuario();
        usuario.setId(id);

        when(usuarioMapper.toEntity(dto)).thenReturn(usuario);
        when(usuarioService.updateUsuario(id, usuario)).thenReturn(usuario);
        when(usuarioModelAssembler.toModel(usuario)).thenReturn(EntityModel.of(usuario));

        ResponseEntity<EntityModel<Usuario>> response = usuarioController.updateUsuario(id, dto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(id, response.getBody().getContent().getId());
    }

    @Test
    void updateUsuario_NullId_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> usuarioController.updateUsuario(null, new UsuarioDTO()));
    }

    @Test
    void deleteUsuario_Success() {
        UUID id = UUID.randomUUID();

        ResponseEntity<?> response = usuarioController.deleteUsuario(id);

        assertEquals(200, response.getStatusCodeValue());
        ResponseWrapper<?> wrapper = (ResponseWrapper<?>) response.getBody();
        verify(usuarioService).deleteUsuario(id);
    }

    @Test
    void deleteUsuario_NullId_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> usuarioController.deleteUsuario(null));
    }
}
