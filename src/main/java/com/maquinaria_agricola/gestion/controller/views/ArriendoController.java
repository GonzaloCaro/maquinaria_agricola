package com.maquinaria_agricola.gestion.controller.views;

import java.security.Principal;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.maquinaria_agricola.gestion.model.maquinarias.ArriendoMaquina;
import com.maquinaria_agricola.gestion.model.maquinarias.Maquina;
import com.maquinaria_agricola.gestion.service.maquinarias.ArriendoMaquinaService;
import com.maquinaria_agricola.gestion.service.maquinarias.MaquinaService;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/arriendo")
public class ArriendoController {

    private final MaquinaService maquinaService;
    private final ArriendoMaquinaService arriendoService;

    public ArriendoController(MaquinaService maquinaService, ArriendoMaquinaService arriendoService) {
        this.maquinaService = maquinaService;
        this.arriendoService = arriendoService;
    }

    @GetMapping("/{id}")
    public String verDetallesMaquina(@PathVariable UUID id, Model model) {
        var maquina = maquinaService.obtener(id);
        if (maquina == null)
            return "redirect:/";
        model.addAttribute("maquina", maquina);
        return "arriendo/detalle";
    }

    @PostMapping("/{id}/confirmar")
    public String confirmarArriendo(@PathVariable UUID id, Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login"; // si no está logueado
        }

        String username = principal.getName();
        ArriendoMaquina arriendo = arriendoService.crearArriendo(id, username);

        model.addAttribute("maquina", arriendo.getMaquina());
        model.addAttribute("mensaje", "¡Arriendo registrado correctamente!");
        return "arriendo/confirmacion";
    }
}