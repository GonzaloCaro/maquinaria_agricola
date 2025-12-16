package com.maquinaria_agricola.controller.views;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.maquinaria_agricola.model.maquinarias.MaquinaType;
import com.maquinaria_agricola.model.maquinarias.Maquina;
import com.maquinaria_agricola.service.maquinarias.MaquinaService;
import com.maquinaria_agricola.service.maquinarias.MaquinaTypeService;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private MaquinaTypeService typeService;

    @Autowired
    private MaquinaService maquinaService;

    // ======== Tipos de Máquina ========
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/types")
    public String listarTipos(Model model) {
        model.addAttribute("tipos", typeService.listarTodos());
        model.addAttribute("nuevoTipo", new MaquinaType());
        return "admin/types";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/types")
    public String crearTipo(@ModelAttribute MaquinaType tipo) {
        typeService.guardar(tipo);
        return "redirect:/admin/types";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/types/delete/{id}")
    public String eliminarTipo(@PathVariable UUID id) {
        typeService.eliminar(id);
        return "redirect:/admin/types";
    }

    // ======== Maquinas ========
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/maquinas")
    public String listarMaquinas(Model model) {
        model.addAttribute("maquinas", maquinaService.listarTodos());
        model.addAttribute("tipos", typeService.listarTodos());
        model.addAttribute("nuevaMaquina", new Maquina());
        return "admin/maquinas";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/maquinas")
    public String crearMaquina(@ModelAttribute Maquina maquina) {
        maquinaService.guardar(maquina);
        return "redirect:/admin/maquinas";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/maquinas/delete/{id}")
    public String eliminarMaquina(@PathVariable UUID id) {
        maquinaService.eliminar(id);
        return "redirect:/admin/maquinas";
    }
}
