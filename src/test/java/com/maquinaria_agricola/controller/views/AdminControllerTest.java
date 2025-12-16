package com.maquinaria_agricola.controller.views;

import com.maquinaria_agricola.model.maquinarias.Maquina;
import com.maquinaria_agricola.model.maquinarias.MaquinaType;
import com.maquinaria_agricola.service.maquinarias.MaquinaService;
import com.maquinaria_agricola.service.maquinarias.MaquinaTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private MaquinaTypeService typeService;

    @Mock
    private MaquinaService maquinaService;

    @Mock
    private Model model;

    @InjectMocks
    private AdminController adminController;

    private MaquinaType tipo1;
    private Maquina maquina1;

    @BeforeEach
    void setUp() {
        tipo1 = new MaquinaType();
        tipo1.setId(UUID.randomUUID());
        tipo1.setNombre("Tractor");

        maquina1 = new Maquina();
        maquina1.setId(UUID.randomUUID());
        maquina1.setModelo("X123");
    }

    // ======== Tipos de máquina ========
    @Test
    void listarTipos_DeberiaRetornarVistaYModelo() {
        List<MaquinaType> tipos = Arrays.asList(tipo1);
        when(typeService.listarTodos()).thenReturn(tipos);

        String view = adminController.listarTipos(model);

        assertEquals("admin/types", view);
        verify(typeService, times(1)).listarTodos();
        verify(model).addAttribute("tipos", tipos);
        verify(model).addAttribute(eq("nuevoTipo"), any(MaquinaType.class));
    }

    @Test
    void crearTipo_DeberiaLlamarServicioYRedirigir() {
        String view = adminController.crearTipo(tipo1);
        assertEquals("redirect:/admin/types", view);
        verify(typeService, times(1)).guardar(tipo1);
    }

    @Test
    void eliminarTipo_DeberiaLlamarServicioYRedirigir() {
        UUID id = UUID.randomUUID();
        String view = adminController.eliminarTipo(id);
        assertEquals("redirect:/admin/types", view);
        verify(typeService, times(1)).eliminar(id);
    }

    // ======== Maquinas ========
    @Test
    void listarMaquinas_DeberiaRetornarVistaYModelo() {
        List<Maquina> maquinas = Arrays.asList(maquina1);
        List<MaquinaType> tipos = Arrays.asList(tipo1);

        when(maquinaService.listarTodos()).thenReturn(maquinas);
        when(typeService.listarTodos()).thenReturn(tipos);

        String view = adminController.listarMaquinas(model);

        assertEquals("admin/maquinas", view);
        verify(maquinaService, times(1)).listarTodos();
        verify(typeService, times(1)).listarTodos();
        verify(model).addAttribute("maquinas", maquinas);
        verify(model).addAttribute("tipos", tipos);
        verify(model).addAttribute(eq("nuevaMaquina"), any(Maquina.class));
    }

    @Test
    void crearMaquina_DeberiaLlamarServicioYRedirigir() {
        String view = adminController.crearMaquina(maquina1);
        assertEquals("redirect:/admin/maquinas", view);
        verify(maquinaService, times(1)).guardar(maquina1);
    }

    @Test
    void eliminarMaquina_DeberiaLlamarServicioYRedirigir() {
        UUID id = UUID.randomUUID();
        String view = adminController.eliminarMaquina(id);
        assertEquals("redirect:/admin/maquinas", view);
        verify(maquinaService, times(1)).eliminar(id);
    }
}
