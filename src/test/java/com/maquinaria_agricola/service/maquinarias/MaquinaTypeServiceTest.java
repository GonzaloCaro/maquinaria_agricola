package com.maquinaria_agricola.service.maquinarias;

import com.maquinaria_agricola.model.maquinarias.MaquinaType;
import com.maquinaria_agricola.repository.maquinarias.TipoMaquinaRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaquinaTypeServiceTest {

    @Mock
    private TipoMaquinaRepository repository;

    @InjectMocks
    private MaquinaTypeService service;

    // --- TEST: listarTodos ---
    @Test
    @DisplayName("Debería retornar lista de tipos de máquina")
    void listarTodos_Exito() {
        // GIVEN
        MaquinaType tipo1 = new MaquinaType();
        tipo1.setNombre("Tractor");
        MaquinaType tipo2 = new MaquinaType();
        tipo2.setNombre("Cosechadora");

        when(repository.findAll()).thenReturn(Arrays.asList(tipo1, tipo2));

        // WHEN
        List<MaquinaType> resultado = service.listarTodos();

        // THEN
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(repository, times(1)).findAll();
    }

    // --- TEST: guardar ---
    @Test
    @DisplayName("Debería guardar y retornar el tipo de máquina")
    void guardar_Exito() {
        // GIVEN
        MaquinaType nuevo = new MaquinaType();
        nuevo.setNombre("Sembradora");

        when(repository.save(nuevo)).thenReturn(nuevo);

        // WHEN
        MaquinaType resultado = service.guardar(nuevo);

        // THEN
        assertNotNull(resultado);
        assertEquals("Sembradora", resultado.getNombre());
        verify(repository).save(nuevo);
    }

    // --- TEST: obtener (Encontrado) ---
    @Test
    @DisplayName("Debería retornar un Optional con valor cuando el ID existe")
    void obtener_Encontrado() {
        // GIVEN
        UUID id = UUID.randomUUID();
        MaquinaType tipo = new MaquinaType();
        tipo.setId(id);
        tipo.setNombre("Fumigadora");

        when(repository.findById(id)).thenReturn(Optional.of(tipo));

        // WHEN
        Optional<MaquinaType> resultado = service.obtener(id);

        // THEN
        assertTrue(resultado.isPresent(), "El optional debería tener valor");
        assertEquals(id, resultado.get().getId());
        assertEquals("Fumigadora", resultado.get().getNombre());
    }

    // --- TEST: obtener (Vacio) ---
    @Test
    @DisplayName("Debería retornar un Optional vacío cuando el ID no existe")
    void obtener_NoEncontrado() {
        // GIVEN
        UUID id = UUID.randomUUID();
        // Simulamos que no encuentra nada
        when(repository.findById(id)).thenReturn(Optional.empty());

        // WHEN
        Optional<MaquinaType> resultado = service.obtener(id);

        // THEN
        // NOTA: Tu servicio actual NO lanza excepción, devuelve Optional vacío.
        // Validamos ese comportamiento:
        assertFalse(resultado.isPresent(), "El optional debería estar vacío");
    }

    // --- TEST: eliminar ---
    @Test
    @DisplayName("Debería llamar al repositorio para eliminar")
    void eliminar_Exito() {
        // GIVEN
        UUID id = UUID.randomUUID();

        // WHEN
        service.eliminar(id);

        // THEN
        verify(repository, times(1)).deleteById(id);
    }
}