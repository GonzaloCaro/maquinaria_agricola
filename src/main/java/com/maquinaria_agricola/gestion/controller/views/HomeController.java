package com.maquinaria_agricola.gestion.controller.views;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.maquinaria_agricola.gestion.model.maquinarias.Maquina;
import com.maquinaria_agricola.gestion.model.usuario.Usuario;
import com.maquinaria_agricola.gestion.service.maquinarias.MaquinaService;
import com.maquinaria_agricola.gestion.service.usuario.UsuarioService;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class HomeController {

    @Autowired
    private MaquinaService maquinaService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/")
    public String home(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String modelo,
            @RequestParam(required = false) Integer anio,
            @RequestParam(required = false) String estado,
            Model model) {

        Usuario usuario = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            usuario = usuarioService.getByUserName(auth.getName());
        }
        model.addAttribute("usuarioLogueado", usuario);

        List<Maquina> maquinas = maquinaService.buscarMaquinas(tipo, marca, modelo, anio, estado);
        List<Maquina> populares = maquinaService.buscarPopulares();
        List<Maquina> ultimas = maquinaService.buscarUltimas();

        model.addAttribute("maquinas", maquinas);
        model.addAttribute("populares", populares);
        model.addAttribute("ultimas", ultimas);
        model.addAttribute("usuarioLogueado", usuario);
        return "home";
    }
}
