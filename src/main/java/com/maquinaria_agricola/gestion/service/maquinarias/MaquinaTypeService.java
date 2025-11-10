package com.maquinaria_agricola.gestion.service.maquinarias;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maquinaria_agricola.gestion.model.maquinarias.MaquinaType;
import com.maquinaria_agricola.gestion.repository.maquinarias.TipoMaquinaRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MaquinaTypeService {

    @Autowired
    private TipoMaquinaRepository repository;

    public MaquinaTypeService(TipoMaquinaRepository repository) {
        this.repository = repository;
    }

    public List<MaquinaType> listarTodos() {
        return repository.findAll();
    }

    public MaquinaType guardar(MaquinaType type) {
        return repository.save(type);
    }

    public Optional<MaquinaType> obtener(UUID id) {
        return repository.findById(id);
    }

    public void eliminar(UUID id) {
        repository.deleteById(id);
    }
}