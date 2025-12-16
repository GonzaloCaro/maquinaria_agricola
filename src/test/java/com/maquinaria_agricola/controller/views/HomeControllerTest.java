package com.maquinaria_agricola.controller.views;

import com.maquinaria_agricola.model.usuario.Usuario;
import com.maquinaria_agricola.service.maquinarias.MaquinaService;
import com.maquinaria_agricola.service.usuario.UsuarioService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class HomeControllerTest {

    @InjectMocks
    private HomeController homeController;

    @Mock
    private MaquinaService maquinaService;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private Model model;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void home_WithAuthenticatedUser_AddsAttributesAndReturnsHomeView() {

        Authentication authMock = mock(Authentication.class);
        when(authMock.isAuthenticated()).thenReturn(true);
        when(authMock.getName()).thenReturn("testUser");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authMock);
        SecurityContextHolder.setContext(securityContext);

        Usuario usuarioMock = new Usuario();
        usuarioMock.setUserName("testUser");
        when(usuarioService.getByUserName("testUser")).thenReturn(usuarioMock);

        when(usuarioService.getByUserName("testUser")).thenReturn(usuarioMock);

        // Mock MaquinaService
        when(maquinaService.buscarMaquinas(null, null, null, null, null)).thenReturn(List.of());
        when(maquinaService.buscarPopulares()).thenReturn(List.of());
        when(maquinaService.buscarUltimas()).thenReturn(List.of());

        String view = homeController.home(null, null, null, null, null, model);

        assertEquals("home", view);
        verify(model).addAttribute("usuarioLogueado", usuarioMock);
        verify(model).addAttribute("maquinas", List.of());
        verify(model).addAttribute("populares", List.of());
        verify(model).addAttribute("ultimas", List.of());
    }

    @Test
    void home_WithNoAuthenticatedUser_ReturnsHomeViewWithNullUser() {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        when(maquinaService.buscarMaquinas(null, null, null, null, null)).thenReturn(List.of());
        when(maquinaService.buscarPopulares()).thenReturn(List.of());
        when(maquinaService.buscarUltimas()).thenReturn(List.of());

        String view = homeController.home(null, null, null, null, null, model);

        assertEquals("home", view);
        verify(model).addAttribute("usuarioLogueado", null);
        verify(model).addAttribute("maquinas", List.of());
        verify(model).addAttribute("populares", List.of());
        verify(model).addAttribute("ultimas", List.of());
    }
}
