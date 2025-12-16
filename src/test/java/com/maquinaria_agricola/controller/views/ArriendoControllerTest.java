package com.maquinaria_agricola.controller.views;

import com.maquinaria_agricola.model.maquinarias.ArriendoMaquina;
import com.maquinaria_agricola.model.maquinarias.Maquina;
import com.maquinaria_agricola.service.maquinarias.ArriendoMaquinaService;
import com.maquinaria_agricola.service.maquinarias.MaquinaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArriendoControllerTest {

    @Mock
    private MaquinaService maquinaService;

    @Mock
    private ArriendoMaquinaService arriendoService;

    @Mock
    private Model model;

    @InjectMocks
    private ArriendoController arriendoController;

    private UUID maquinaId;
    private Maquina maquina;
    private ArriendoMaquina arriendo;

    @BeforeEach
    void setUp() {
        maquinaId = UUID.randomUUID();

        maquina = new Maquina();
        maquina.setId(maquinaId);
        maquina.setModelo("Tractor John Deere");

        arriendo = new ArriendoMaquina();
        arriendo.setMaquina(maquina);
    }

    // ===== GET /{id} =====
    @Test
    void verDetallesMaquina_MaquinaExiste_RetornaVistaDetalle() {
        when(maquinaService.obtener(maquinaId)).thenReturn(maquina);

        String view = arriendoController.verDetallesMaquina(maquinaId, model);

        assertEquals("arriendo/detalle", view);
        verify(maquinaService, times(1)).obtener(maquinaId);
        verify(model, times(1)).addAttribute("maquina", maquina);
    }

    @Test
    void verDetallesMaquina_MaquinaNoExiste_RedirectHome() {
        when(maquinaService.obtener(maquinaId)).thenReturn(null);

        String view = arriendoController.verDetallesMaquina(maquinaId, model);

        assertEquals("redirect:/", view);
        verify(maquinaService, times(1)).obtener(maquinaId);
        verify(model, never()).addAttribute(anyString(), any());
    }

    // ===== POST /{id}/confirmar =====
    @Test
    void confirmarArriendo_UsuarioNoLogueado_RedirectLogin() {
        String view = arriendoController.confirmarArriendo(maquinaId, null, model);

        assertEquals("redirect:/login", view);
        verify(arriendoService, never()).crearArriendo(any(), any());
        verify(model, never()).addAttribute(anyString(), any());
    }

    @Test
    void confirmarArriendo_UsuarioLogueado_CreaArriendoYRetornaConfirmacion() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("usuarioTest");
        when(arriendoService.crearArriendo(maquinaId, "usuarioTest")).thenReturn(arriendo);

        String view = arriendoController.confirmarArriendo(maquinaId, principal, model);

        assertEquals("arriendo/confirmacion", view);
        verify(arriendoService, times(1)).crearArriendo(maquinaId, "usuarioTest");
        verify(model, times(1)).addAttribute("maquina", maquina);
        verify(model, times(1)).addAttribute("mensaje", "¡Arriendo registrado correctamente!");
    }
}
