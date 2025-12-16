package com.maquinaria_agricola.service.maquinarias;

import com.maquinaria_agricola.model.maquinarias.Maquina;
import com.maquinaria_agricola.model.maquinarias.MaquinaType;
import com.maquinaria_agricola.repository.maquinarias.MaquinaRepository;
import com.maquinaria_agricola.repository.maquinarias.TipoMaquinaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaquinaServiceTest {

    @Mock
    private MaquinaRepository maquinaRepository;

    @Mock
    private TipoMaquinaRepository typeRepository;

    @InjectMocks
    private MaquinaService maquinaService;

    // --- TEST: buscarUltimas ---
    @Test
    void buscarUltimas_DeberiaLlamarAlMetodoCorrectoDelRepo() {
        // GIVEN
        when(maquinaRepository.findTop6ByOrderByIdDesc())
                .thenReturn(Arrays.asList(new Maquina(), new Maquina()));

        // WHEN
        List<Maquina> result = maquinaService.buscarUltimas();

        // THEN
        assertEquals(2, result.size());
        verify(maquinaRepository).findTop6ByOrderByIdDesc();
    }

    // --- TEST: buscarPopulares ---
    @Test
    void buscarPopulares_DeberiaLlamarAlMetodoCorrectoDelRepo() {
        // GIVEN
        when(maquinaRepository.findTop6ByOrderByIdAsc())
                .thenReturn(Collections.singletonList(new Maquina()));

        // WHEN
        List<Maquina> result = maquinaService.buscarPopulares();

        // THEN
        assertEquals(1, result.size());
        verify(maquinaRepository).findTop6ByOrderByIdAsc();
    }

    // --- TEST: buscarMaquinas (Filtros) ---
    @Test
    void buscarMaquinas_PorAhoraRetornaFindAll() {
        // GIVEN: Tu implementación actual ignora los parámetros y llama a findAll
        when(maquinaRepository.findAll()).thenReturn(Collections.emptyList());

        // WHEN
        List<Maquina> result = maquinaService.buscarMaquinas("Tipo", "Marca", "Modelo", 2020, "Activo");

        // THEN
        assertNotNull(result);
        verify(maquinaRepository).findAll();
    }

    // --- TEST: guardar ---
    @Test
    void guardar_DeberiaGuardarEnRepo() {
        // GIVEN
        Maquina maquina = new Maquina();
        when(maquinaRepository.save(maquina)).thenReturn(maquina);

        // WHEN
        Maquina result = maquinaService.guardar(maquina);

        // THEN
        assertNotNull(result);
        verify(maquinaRepository).save(maquina);
    }

    // --- TEST: obtener (ID existente) ---
    @Test
    void obtener_SiExiste_RetornaMaquina() {
        // GIVEN
        UUID id = UUID.randomUUID();
        Maquina maquina = new Maquina();
        when(maquinaRepository.findById(id)).thenReturn(Optional.of(maquina));

        // WHEN
        Maquina result = maquinaService.obtener(id);

        // THEN
        assertNotNull(result);
        verify(maquinaRepository).findById(id);
    }

    // --- TEST: obtener (ID no existente) ---
    @Test
    void obtener_SiNoExiste_RetornaNull() {
        // GIVEN
        UUID id = UUID.randomUUID();
        // Tu servicio hace .orElse(null), así que probamos que retorne null
        when(maquinaRepository.findById(id)).thenReturn(Optional.empty());

        // WHEN
        Maquina result = maquinaService.obtener(id);

        // THEN
        assertNull(result, "Debería retornar null si no encuentra la máquina");
    }

    // --- TEST: eliminar ---
    @Test
    void eliminar_DeberiaLlamarDeleteById() {
        UUID id = UUID.randomUUID();
        maquinaService.eliminar(id);
        verify(maquinaRepository).deleteById(id);
    }

    // --- TEST COMPLEJO: listarPorTipo (Caso Exitoso) ---
    @Test
    @DisplayName("listarPorTipo: Si el Tipo existe, busca sus máquinas")
    void listarPorTipo_TipoExiste_RetornaLista() {
        // GIVEN
        UUID typeId = UUID.randomUUID();
        MaquinaType tipoMock = new MaquinaType();
        tipoMock.setId(typeId);

        // 1. Simulamos que el TipoMaquina existe
        when(typeRepository.findById(typeId)).thenReturn(Optional.of(tipoMock));

        // 2. Simulamos que, al encontrar el tipo, busca las máquinas de ese tipo
        List<Maquina> maquinasDelTipo = Arrays.asList(new Maquina(), new Maquina());
        when(maquinaRepository.findByMaquinaType(tipoMock)).thenReturn(maquinasDelTipo);

        // WHEN
        List<Maquina> result = maquinaService.listarPorTipo(typeId);

        // THEN
        assertEquals(2, result.size());
        // Verificamos el flujo: primero buscó el tipo, luego usó ese tipo para buscar
        // máquinas
        verify(typeRepository).findById(typeId);
        verify(maquinaRepository).findByMaquinaType(tipoMock);
    }

    // --- TEST COMPLEJO: listarPorTipo (Caso No Existe Tipo) ---
    @Test
    @DisplayName("listarPorTipo: Si el Tipo NO existe, retorna lista vacía sin buscar máquinas")
    void listarPorTipo_TipoNoExiste_RetornaVacio() {
        // GIVEN
        UUID typeId = UUID.randomUUID();

        // 1. Simulamos que el TipoMaquina NO existe
        when(typeRepository.findById(typeId)).thenReturn(Optional.empty());

        // WHEN
        List<Maquina> result = maquinaService.listarPorTipo(typeId);

        // THEN
        assertTrue(result.isEmpty(), "Debería ser una lista vacía");

        verify(typeRepository).findById(typeId);
        // IMPORTANTE: Verificamos que NUNCA llamó al repo de máquinas,
        // porque el Optional estaba vacío y el .map() no se ejecutó.
        verify(maquinaRepository, never()).findByMaquinaType(any());
    }
}