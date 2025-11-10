package com.maquinaria_agricola.gestion.repository.maquinarias;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maquinaria_agricola.gestion.model.maquinarias.MaquinaType;

@Repository
public interface TipoMaquinaRepository extends JpaRepository<MaquinaType, UUID> {
}