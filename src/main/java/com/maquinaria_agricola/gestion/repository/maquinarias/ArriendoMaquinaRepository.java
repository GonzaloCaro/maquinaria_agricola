package com.maquinaria_agricola.gestion.repository.maquinarias;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maquinaria_agricola.gestion.model.maquinarias.ArriendoMaquina;

@Repository
public interface ArriendoMaquinaRepository extends JpaRepository<ArriendoMaquina, UUID> {
}
