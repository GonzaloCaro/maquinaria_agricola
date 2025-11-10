package com.maquinaria_agricola.gestion.controller.views;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthViewsController {
    // Vista de Login
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("pageTitle", "Iniciar Sesión");
        return "login"; // Carga templates/login.html
    }

    // Vista de Registro
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("pageTitle", "Registro");
        return "register"; // Carga templates/register.html
    }

    // Vista de Recuperar Contraseña
    @GetMapping("/forgot-password")
    public String forgotPassword(Model model) {
        model.addAttribute("pageTitle", "Recuperar Contraseña");
        return "forgot-password"; // Carga templates/forgot-password.html
    }
}
