package com.maquinaria_agricola.gestion.repository.maquinarias;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maquinaria_agricola.gestion.model.maquinarias.Maquina;
import com.maquinaria_agricola.gestion.model.maquinarias.MaquinaType;

@Repository
public interface MaquinaRepository extends JpaRepository<Maquina, UUID> {
    List<Maquina> findByMaquinaType(MaquinaType type);

    List<Maquina> findTop6ByOrderByIdDesc();

    List<Maquina> findTop6ByOrderByIdAsc();
}