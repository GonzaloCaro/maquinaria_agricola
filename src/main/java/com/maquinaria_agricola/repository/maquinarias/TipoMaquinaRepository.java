package com.maquinaria_agricola.repository.maquinarias;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maquinaria_agricola.model.maquinarias.MaquinaType;

@Repository
public interface TipoMaquinaRepository extends JpaRepository<MaquinaType, UUID> {
}