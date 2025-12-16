package com.maquinaria_agricola.service.usuario;

import com.maquinaria_agricola.model.usuario.Area;
import com.maquinaria_agricola.repository.usuario.AreaRepository;

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
class AreaServiceTest {

    @Mock
    private AreaRepository areaRepository;

    @InjectMocks
    private AreaService areaService;

    // --- TEST: getAllAreas ---
    @Test
    @DisplayName("Debería retornar una lista de áreas cuando existen registros")
    void getAllAreas_Exito() {
        // GIVEN
        Area area1 = new Area();
        area1.setNombre("Cultivo");
        Area area2 = new Area();
        area2.setNombre("Riego");

        when(areaRepository.findAll()).thenReturn(Arrays.asList(area1, area2));

        // WHEN
        List<Area> resultado = areaService.getAllAreas();

        // THEN
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(areaRepository, times(1)).findAll();
    }

    // --- TEST: createArea ---
    @Test
    @DisplayName("Debería guardar y retornar el área creada")
    void createArea_Exito() {
        // GIVEN
        Area areaParaGuardar = new Area();
        areaParaGuardar.setNombre("Mantenimiento");

        Area areaGuardada = new Area();
        areaGuardada.setId(UUID.randomUUID());
        areaGuardada.setNombre("Mantenimiento");

        when(areaRepository.save(areaParaGuardar)).thenReturn(areaGuardada);

        // WHEN
        Area resultado = areaService.createArea(areaParaGuardar);

        // THEN
        assertNotNull(resultado);
        assertNotNull(resultado.getId()); // Verificamos que ahora tiene ID
        assertEquals("Mantenimiento", resultado.getNombre());
        verify(areaRepository).save(areaParaGuardar);
    }

    // --- TEST: getAreaById (Exitoso) ---
    @Test
    @DisplayName("Debería retornar el área cuando el ID existe")
    void getAreaById_Exito() {
        // GIVEN
        UUID id = UUID.randomUUID();
        Area area = new Area();
        area.setId(id);
        area.setNombre("Logística");

        when(areaRepository.findById(id)).thenReturn(Optional.of(area));

        // WHEN
        Area resultado = areaService.getAreaById(id);

        // THEN
        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        assertEquals("Logística", resultado.getNombre());
    }

    // --- TEST: getAreaById (No Encontrado) ---
    @Test
    @DisplayName("Debería lanzar RuntimeException cuando el ID no existe al buscar")
    void getAreaById_NoEncontrado() {
        // GIVEN
        UUID id = UUID.randomUUID();
        when(areaRepository.findById(id)).thenReturn(Optional.empty());

        // WHEN & THEN
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            areaService.getAreaById(id);
        });

        assertEquals("Área no encontrada con ID: " + id, exception.getMessage());
    }

    // --- TEST: updateArea (Exitoso) ---
    @Test
    @DisplayName("Debería actualizar el nombre del área cuando el ID existe")
    void updateArea_Exito() {
        // GIVEN
        UUID id = UUID.randomUUID();

        // Área existente en BD
        Area areaExistente = new Area();
        areaExistente.setId(id);
        areaExistente.setNombre("Nombre Antiguo");

        // Datos nuevos (DTO simulado o entidad con datos nuevos)
        Area areaDetalles = new Area();
        areaDetalles.setNombre("Nombre Actualizado");

        when(areaRepository.findById(id)).thenReturn(Optional.of(areaExistente));
        // Simulamos que save retorna el mismo objeto (ya modificado por referencia en
        // el servicio)
        when(areaRepository.save(areaExistente)).thenReturn(areaExistente);

        // WHEN
        Area resultado = areaService.updateArea(id, areaDetalles);

        // THEN
        assertNotNull(resultado);
        assertEquals("Nombre Actualizado", resultado.getNombre()); // Verificamos que el cambio se aplicó
        verify(areaRepository).save(areaExistente);
    }

    // --- TEST: updateArea (No Encontrado) ---
    @Test
    @DisplayName("Debería lanzar RuntimeException al intentar actualizar un ID inexistente")
    void updateArea_NoEncontrado() {
        // GIVEN
        UUID id = UUID.randomUUID();
        Area areaDetalles = new Area();
        areaDetalles.setNombre("Intento Fallido");

        when(areaRepository.findById(id)).thenReturn(Optional.empty());

        // WHEN & THEN
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            areaService.updateArea(id, areaDetalles);
        });

        assertEquals("Área no encontrada con ID: " + id, exception.getMessage());
        // Importante: Verificamos que NUNCA se intente guardar si no se encontró
        verify(areaRepository, never()).save(any());
    }

    // --- TEST: deleteArea ---
    @Test
    @DisplayName("Debería llamar al repositorio para eliminar por ID")
    void deleteArea_Exito() {
        // GIVEN
        UUID id = UUID.randomUUID();
        // deleteById retorna void, no necesitamos hacer "when" de retorno, solo
        // verificar llamada

        // WHEN
        areaService.deleteArea(id);

        // THEN
        verify(areaRepository, times(1)).deleteById(id);
    }
}