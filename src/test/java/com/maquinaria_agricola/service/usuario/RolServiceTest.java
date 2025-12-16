package com.maquinaria_agricola.service.usuario;

import com.maquinaria_agricola.model.usuario.Rol;
import com.maquinaria_agricola.repository.usuario.RolRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RolServiceTest {

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private RolService rolService;

    // --- TEST: getAllRoles ---
    @Test
    @DisplayName("Debería retornar una lista de roles")
    void getAllRoles_Exito() {
        // GIVEN
        Rol rol1 = new Rol();
        rol1.setNombre("ADMIN");
        Rol rol2 = new Rol();
        rol2.setNombre("USER");

        when(rolRepository.findAll()).thenReturn(Arrays.asList(rol1, rol2));

        // WHEN
        List<Rol> resultado = rolService.getAllRoles();

        // THEN
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(rolRepository, times(1)).findAll();
    }

    // --- TEST: createRol ---
    @Test
    @DisplayName("Debería guardar y retornar el rol creado")
    void createRol_Exito() {
        // GIVEN
        Rol rolNuevo = new Rol();
        rolNuevo.setNombre("SUPERVISOR");

        Rol rolGuardado = new Rol();
        rolGuardado.setId(UUID.randomUUID());
        rolGuardado.setNombre("SUPERVISOR");

        when(rolRepository.save(rolNuevo)).thenReturn(rolGuardado);

        // WHEN
        Rol resultado = rolService.createRol(rolNuevo);

        // THEN
        assertNotNull(resultado);
        assertNotNull(resultado.getId());
        assertEquals("SUPERVISOR", resultado.getNombre());
        verify(rolRepository).save(rolNuevo);
    }

    // --- TEST: getRolById (Exitoso) ---
    @Test
    @DisplayName("Debería retornar el rol cuando existe el ID")
    void getRolById_Exito() {
        // GIVEN
        UUID id = UUID.randomUUID();
        Rol rol = new Rol();
        rol.setId(id);
        rol.setNombre("INVITADO");

        when(rolRepository.findById(id)).thenReturn(Optional.of(rol));

        // WHEN
        Rol resultado = rolService.getRolById(id);

        // THEN
        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        assertEquals("INVITADO", resultado.getNombre());
    }

    // --- TEST: getRolById (Fallo) ---
    @Test
    @DisplayName("Debería lanzar RuntimeException si el rol no existe")
    void getRolById_NoEncontrado() {
        // GIVEN
        UUID id = UUID.randomUUID();
        when(rolRepository.findById(id)).thenReturn(Optional.empty());

        // WHEN & THEN
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            rolService.getRolById(id);
        });

        assertEquals("Rol no encontrado con ID: " + id, ex.getMessage());
    }

    // --- TEST: updateRol (Exitoso) ---
    @Test
    @DisplayName("Debería actualizar el nombre del rol correctamente")
    void updateRol_Exito() {
        // GIVEN
        UUID id = UUID.randomUUID();

        Rol rolExistente = new Rol();
        rolExistente.setId(id);
        rolExistente.setNombre("OLD_NAME");

        Rol rolDetalles = new Rol();
        rolDetalles.setNombre("NEW_NAME");

        when(rolRepository.findById(id)).thenReturn(Optional.of(rolExistente));
        when(rolRepository.save(rolExistente)).thenReturn(rolExistente);

        // WHEN
        Rol resultado = rolService.updateRol(id, rolDetalles);

        // THEN
        // Verificamos que el objeto retornado tenga el nuevo nombre
        assertEquals("NEW_NAME", resultado.getNombre());
        verify(rolRepository).save(rolExistente);
    }

    // --- TEST: updateRol (Fallo) ---
    @Test
    @DisplayName("Debería lanzar excepción al intentar actualizar un rol inexistente")
    void updateRol_NoEncontrado() {
        // GIVEN
        UUID id = UUID.randomUUID();
        Rol rolDetalles = new Rol();
        rolDetalles.setNombre("INTENTO");

        when(rolRepository.findById(id)).thenReturn(Optional.empty());

        // WHEN & THEN
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            rolService.updateRol(id, rolDetalles);
        });

        assertEquals("Rol no encontrado con ID: " + id, ex.getMessage());
        // Seguridad: Asegurar que nunca se llame a save() si no se encontró el ID
        verify(rolRepository, never()).save(any());
    }

    // --- TEST: deleteRol ---
    @Test
    @DisplayName("Debería llamar al repositorio para eliminar")
    void deleteRol_Exito() {
        // GIVEN
        UUID id = UUID.randomUUID();

        // WHEN
        rolService.deleteRol(id);

        // THEN
        verify(rolRepository, times(1)).deleteById(id);
    }
}