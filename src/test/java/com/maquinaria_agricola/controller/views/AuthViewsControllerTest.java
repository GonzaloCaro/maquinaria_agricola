package com.maquinaria_agricola.controller.views;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthViewsControllerTest {

    @InjectMocks
    private AuthViewsController authViewsController;

    @Mock
    private Model model;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_AddsPageTitleAndReturnsLoginView() {
        String view = authViewsController.login(model);

        assertEquals("login", view);
        verify(model, times(1)).addAttribute("pageTitle", "Iniciar Sesión");
    }

    @Test
    void register_AddsPageTitleAndReturnsRegisterView() {
        String view = authViewsController.register(model);

        assertEquals("register", view);
        verify(model, times(1)).addAttribute("pageTitle", "Registro");
    }

    @Test
    void forgotPassword_AddsPageTitleAndReturnsForgotPasswordView() {
        String view = authViewsController.forgotPassword(model);

        assertEquals("forgot-password", view);
        verify(model, times(1)).addAttribute("pageTitle", "Recuperar Contraseña");
    }
}
