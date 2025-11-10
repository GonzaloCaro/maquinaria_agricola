package com.maquinaria_agricola.gestion.service.maquinarias;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.maquinaria_agricola.gestion.model.maquinarias.Maquina;
import com.maquinaria_agricola.gestion.repository.maquinarias.MaquinaRepository;
import com.maquinaria_agricola.gestion.repository.maquinarias.TipoMaquinaRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MaquinaService {
    private final MaquinaRepository maquinaRepository;
    private final TipoMaquinaRepository typeRepository;

    public MaquinaService(MaquinaRepository maquinaRepository, TipoMaquinaRepository typeRepository) {
        this.maquinaRepository = maquinaRepository;
        this.typeRepository = typeRepository;
    }

    public List<Maquina> buscarUltimas() {
        return maquinaRepository.findTop6ByOrderByIdDesc(); // Ejemplo
    }

    public List<Maquina> buscarPopulares() {
        return maquinaRepository.findTop6ByOrderByIdAsc(); // Cambia por criterio real
    }

    public List<Maquina> buscarMaquinas(String tipo, String marca, String modelo, Integer anio, String estado) {
        // Implementa tu lógica de filtro o usa un Specification dinámico
        return maquinaRepository.findAll();
    }

    public List<Maquina> listarTodos() {
        return maquinaRepository.findAll();
    }

    public Maquina guardar(Maquina maquina) {
        return maquinaRepository.save(maquina);
    }

    public Maquina obtener(UUID id) {

        return maquinaRepository.findById(id).orElse(null);
    }

    public void eliminar(UUID id) {
        maquinaRepository.deleteById(id);
    }

    public List<Maquina> listarPorTipo(UUID typeId) {
        return typeRepository.findById(typeId)
                .map(maquinaRepository::findByMaquinaType)
                .orElse(Collections.emptyList());
    }
}
