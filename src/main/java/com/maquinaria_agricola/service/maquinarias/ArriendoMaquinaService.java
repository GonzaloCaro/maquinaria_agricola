package com.maquinaria_agricola.service.maquinarias;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maquinaria_agricola.model.maquinarias.ArriendoMaquina;
import com.maquinaria_agricola.model.maquinarias.Maquina;
import com.maquinaria_agricola.model.usuario.Usuario;
import com.maquinaria_agricola.repository.maquinarias.ArriendoMaquinaRepository;
import com.maquinaria_agricola.repository.maquinarias.MaquinaRepository;
import com.maquinaria_agricola.repository.usuario.UsuarioRepository;

@Service
public class ArriendoMaquinaService {

    private final ArriendoMaquinaRepository arriendoRepository;
    private final MaquinaRepository maquinaRepository;
    private final UsuarioRepository usuarioRepository;

    public ArriendoMaquinaService(
            ArriendoMaquinaRepository arriendoRepository,
            MaquinaRepository maquinaRepository,
            UsuarioRepository usuarioRepository) {
        this.arriendoRepository = arriendoRepository;
        this.maquinaRepository = maquinaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public ArriendoMaquina crearArriendo(UUID maquinaId, String username) {
        Maquina maquina = maquinaRepository.findById(maquinaId)
                .orElseThrow(() -> new RuntimeException("Máquina no encontrada"));

        Usuario usuario = usuarioRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        ArriendoMaquina arriendo = new ArriendoMaquina();
        arriendo.setMaquina(maquina);
        arriendo.setUsuario(usuario);
        arriendo.setFechaArriendo(LocalDate.now());
        arriendo.setFechaDevolucion(LocalDate.now().plusDays(3)); // ejemplo: 3 días de arriendo

        return arriendoRepository.save(arriendo);
    }
}
