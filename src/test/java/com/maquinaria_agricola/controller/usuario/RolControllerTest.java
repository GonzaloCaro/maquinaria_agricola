package com.maquinaria_agricola.controller.usuario;

import com.maquinaria_agricola.hateoas.RolModelAssembler;
import com.maquinaria_agricola.model.ResponseWrapper;
import com.maquinaria_agricola.model.usuario.Rol;
import com.maquinaria_agricola.service.usuario.RolService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RolControllerTest {

    private RolService rolService;
    private RolModelAssembler rolModelAssembler;
    private RolController rolController;

    @BeforeEach
    void setUp() {
        rolService = mock(RolService.class);
        rolModelAssembler = mock(RolModelAssembler.class);
        rolController = new RolController(rolService, null, rolModelAssembler);
    }

    @Test
    void getAllRoles_ReturnsRoles() {
        Rol rol1 = new Rol();
        rol1.setId(UUID.randomUUID());
        rol1.setNombre("ADMIN");
        Rol rol2 = new Rol();
        rol2.setId(UUID.randomUUID());
        rol2.setNombre("USER");
        List<Rol> roles = List.of(rol1, rol2);

        when(rolService.getAllRoles()).thenReturn(roles);
        when(rolModelAssembler.toModel(rol1)).thenReturn(EntityModel.of(rol1));
        when(rolModelAssembler.toModel(rol2)).thenReturn(EntityModel.of(rol2));

        ResponseEntity<CollectionModel<EntityModel<Rol>>> response = rolController.getAllRoles();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().getContent().size());
        verify(rolService).getAllRoles();
    }

    @Test
    void getAllRoles_NoRoles_ReturnsNoContent() {
        when(rolService.getAllRoles()).thenReturn(List.of());

        ResponseEntity<CollectionModel<EntityModel<Rol>>> response = rolController.getAllRoles();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void getRolById_ReturnsRol() {
        UUID id = UUID.randomUUID();
        Rol rol = new Rol();
        rol.setId(id);
        rol.setNombre("ADMIN");

        when(rolService.getRolById(id)).thenReturn(rol);
        when(rolModelAssembler.toModel(rol)).thenReturn(EntityModel.of(rol));

        ResponseEntity<EntityModel<Rol>> response = rolController.getRolById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(rol.getId(), response.getBody().getContent().getId());
        verify(rolService).getRolById(id);
    }

    @Test
    void createRol_ReturnsCreatedRol() {
        Rol rol = new Rol();
        rol.setNombre("TEST");

        when(rolService.createRol(rol)).thenReturn(rol);
        when(rolModelAssembler.toModel(rol)).thenReturn(EntityModel.of(rol));

        ResponseEntity<EntityModel<Rol>> response = rolController.createRol(rol);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(rol.getNombre(), response.getBody().getContent().getNombre());
        verify(rolService).createRol(rol);
    }

    @Test
    void deleteRol_ReturnsResponseWrapper() {
        UUID id = UUID.randomUUID();

        ResponseEntity<?> response = rolController.deleteRol(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseWrapper<?> body = (ResponseWrapper<?>) response.getBody();
        verify(rolService).deleteRol(id);
    }

    @Test
    void getRolById_NullId_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> rolController.getRolById(null));
    }

    @Test
    void createRol_NullRol_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> rolController.createRol(null));
    }

    @Test
    void deleteRol_NullId_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> rolController.deleteRol(null));
    }
}
