package com.maquinaria_agricola.service.maquinarias;

import com.maquinaria_agricola.model.maquinarias.ArriendoMaquina;
import com.maquinaria_agricola.model.maquinarias.Maquina;
import com.maquinaria_agricola.model.usuario.Usuario;
import com.maquinaria_agricola.repository.maquinarias.ArriendoMaquinaRepository;
import com.maquinaria_agricola.repository.maquinarias.MaquinaRepository;
import com.maquinaria_agricola.repository.usuario.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArriendoMaquinaServiceTest {

    @Mock
    private ArriendoMaquinaRepository arriendoRepository;

    @Mock
    private MaquinaRepository maquinaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ArriendoMaquinaService arriendoService;

    // --- TEST: Crear Arriendo (Camino Feliz) ---
    @Test
    @DisplayName("Debería crear un arriendo correctamente calculando fechas")
    void crearArriendo_Exito() {
        // GIVEN
        UUID maquinaId = UUID.randomUUID();
        String username = "agricultor_juan";

        Maquina maquinaMock = new Maquina();
        maquinaMock.setId(maquinaId);

        Usuario usuarioMock = new Usuario();
        usuarioMock.setUserName(username);

        // 1. Simulamos que existen la máquina y el usuario
        when(maquinaRepository.findById(maquinaId)).thenReturn(Optional.of(maquinaMock));
        when(usuarioRepository.findByUserName(username)).thenReturn(Optional.of(usuarioMock));

        // 2. Simulamos el guardado retornando el mismo objeto para inspeccionarlo
        when(arriendoRepository.save(any(ArriendoMaquina.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        ArriendoMaquina resultado = arriendoService.crearArriendo(maquinaId, username);

        // THEN
        assertNotNull(resultado);
        assertEquals(maquinaMock, resultado.getMaquina());
        assertEquals(usuarioMock, resultado.getUsuario());

        // Validamos la lógica de negocio (fechas)
        assertEquals(LocalDate.now(), resultado.getFechaArriendo(), "La fecha de inicio debe ser hoy");
        assertEquals(LocalDate.now().plusDays(3), resultado.getFechaDevolucion(), "La devolución debe ser en 3 días");

        verify(arriendoRepository).save(any(ArriendoMaquina.class));
    }

    // --- TEST: Máquina No Encontrada ---
    @Test
    @DisplayName("Debería lanzar excepción si la máquina no existe (Fail Fast)")
    void crearArriendo_MaquinaNoExiste_LanzaExcepcion() {
        // GIVEN
        UUID maquinaId = UUID.randomUUID();
        String username = "usuario_test";

        when(maquinaRepository.findById(maquinaId)).thenReturn(Optional.empty());

        // WHEN & THEN
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            arriendoService.crearArriendo(maquinaId, username);
        });

        assertEquals("Máquina no encontrada", ex.getMessage());

        // VERIFICACIÓN DE EFICIENCIA:
        // Si no encontró la máquina, no debería perder tiempo buscando al usuario
        verify(usuarioRepository, never()).findByUserName(anyString());
        // Y por supuesto, no debe guardar nada
        verify(arriendoRepository, never()).save(any());
    }

    // --- TEST: Usuario No Encontrado ---
    @Test
    @DisplayName("Debería lanzar excepción si el usuario no existe")
    void crearArriendo_UsuarioNoExiste_LanzaExcepcion() {
        // GIVEN
        UUID maquinaId = UUID.randomUUID();
        String username = "usuario_fantasma";

        // La máquina SÍ existe
        when(maquinaRepository.findById(maquinaId)).thenReturn(Optional.of(new Maquina()));
        // Pero el usuario NO
        when(usuarioRepository.findByUserName(username)).thenReturn(Optional.empty());

        // WHEN & THEN
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            arriendoService.crearArriendo(maquinaId, username);
        });

        assertEquals("Usuario no encontrado", ex.getMessage());

        // Verificamos que no se guardó nada
        verify(arriendoRepository, never()).save(any());
    }
}